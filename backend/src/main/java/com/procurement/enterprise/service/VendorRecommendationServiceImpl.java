package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.VendorRecommendationResponse;
import com.procurement.enterprise.entity.PurchaseRequestItem;
import com.procurement.enterprise.entity.VendorProduct;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.PurchaseRequestItemRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.SupplierPerformanceRepository;
import com.procurement.enterprise.repository.VendorProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Deterministic vendor recommendation engine.
 *
 * Scoring weights:
 *   Availability  25%  — how well the vendor exceeds the required quantity
 *   Price         25%  — relative to the cheapest eligible vendor
 *   Quality       20%  — avg quality+pricing rating from SupplierPerformance (1-5 → 0-100)
 *   Delivery      15%  — avg delivery rating from SupplierPerformance (1-5 → 0-100)
 *   History       15%  — successfulOrders / totalOrders (0-100; new vendors get 50)
 *
 * Eligibility: vendor must be active, supply the product, have a valid price,
 * and have availableQuantity >= requestedQuantity.
 */
@Service
@RequiredArgsConstructor
public class VendorRecommendationServiceImpl implements VendorRecommendationService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final VendorProductRepository vendorProductRepository;
    private final SupplierPerformanceRepository supplierPerformanceRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public VendorRecommendationResponse recommendForPurchaseRequest(Long purchaseRequestId) {
        purchaseRequestRepository.findByIdAndIsDeletedFalse(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Request", purchaseRequestId));

        List<PurchaseRequestItem> items = purchaseRequestItemRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(purchaseRequestId);
        if (items.isEmpty()) {
            throw new InvalidRequestException("Purchase request has no items.");
        }

        PurchaseRequestItem item = items.get(0);
        Long productId = item.getProduct().getId();
        String productName = item.getProduct().getName();
        int requestedQty = item.getQuantity();

        // Eligible vendors: active, supply product, valid price, sufficient quantity
        List<VendorProduct> eligible = vendorProductRepository
                .findEligibleByProductAndQuantity(productId, requestedQty);

        // All active vendors for this product (to show ineligible ones)
        List<VendorProduct> allActive = vendorProductRepository.findAllActiveByProduct(productId);

        List<VendorRecommendationResponse.IneligibleVendor> ineligible = allActive.stream()
                .filter(vp -> eligible.stream().noneMatch(e -> e.getId().equals(vp.getId())))
                .map(vp -> new VendorRecommendationResponse.IneligibleVendor(
                        vp.getVendor().getId(),
                        vp.getVendor().getVendorName(),
                        vp.getPrice(),
                        vp.getAvailableQuantity(),
                        "Insufficient quantity (available: " + vp.getAvailableQuantity() + ", required: " + requestedQty + ")"
                ))
                .toList();

        if (eligible.isEmpty()) {
            return new VendorRecommendationResponse(
                    purchaseRequestId, productName, requestedQty,
                    null, List.of(), ineligible,
                    "No vendor can currently fulfill the requested quantity of " + requestedQty + " units."
            );
        }

        List<Long> eligibleVendorIds = eligible.stream().map(vp -> vp.getVendor().getId()).toList();

        // Fetch performance ratings: [vendorId, avgQuality, avgDelivery]
        Map<Long, double[]> ratings = new HashMap<>();
        supplierPerformanceRepository.findRecommendationRatingsByVendorIds(eligibleVendorIds)
                .forEach(row -> ratings.put(
                        ((Number) row[0]).longValue(),
                        new double[]{ratingToScore((Number) row[1]), ratingToScore((Number) row[2])}
                ));

        // Fetch historical order counts: [vendorId, totalOrders, successfulOrders]
        Map<Long, long[]> history = new HashMap<>();
        purchaseOrderRepository.recommendationHistoryByVendorAndProduct(productId, eligibleVendorIds)
                .forEach(row -> history.put(
                        ((Number) row[0]).longValue(),
                        new long[]{((Number) row[1]).longValue(), ((Number) row[2]).longValue()}
                ));

        BigDecimal minPrice = eligible.stream().map(VendorProduct::getPrice).min(BigDecimal::compareTo).orElseThrow();
        int maxQty = eligible.stream().mapToInt(vp -> vp.getAvailableQuantity() != null ? vp.getAvailableQuantity() : 0).max().orElse(requestedQty);

        List<VendorRecommendationResponse.VendorRanking> rankings = eligible.stream()
                .map(vp -> score(vp, minPrice, maxQty, requestedQty,
                        ratings.get(vp.getVendor().getId()),
                        history.get(vp.getVendor().getId())))
                .sorted(Comparator.comparingDouble(VendorRecommendationResponse.VendorRanking::overallScore).reversed())
                .toList();

        return new VendorRecommendationResponse(
                purchaseRequestId, productName, requestedQty,
                rankings.get(0), rankings, ineligible, null
        );
    }

    /**
     * Weights: availability 25%, price 25%, quality 20%, delivery 15%, history 15%
     */
    private VendorRecommendationResponse.VendorRanking score(
            VendorProduct vp, BigDecimal minPrice, int maxQty, int requestedQty,
            double[] ratings, long[] history) {

        int available = vp.getAvailableQuantity() != null ? vp.getAvailableQuantity() : 0;

        // Availability score: vendor with max qty gets 100; vendor that barely meets gets ~50
        double availScore = maxQty <= requestedQty
                ? 100.0
                : round(50.0 + 50.0 * (available - requestedQty) / (double)(maxQty - requestedQty));
        availScore = Math.min(100.0, availScore);

        // Price score: cheapest gets 100
        double priceScore = round(minPrice.divide(vp.getPrice(), 8, RoundingMode.HALF_UP).doubleValue() * 100);

        double qualityScore = ratings == null ? 50.0 : ratings[0];
        double deliveryScore = ratings == null ? 50.0 : ratings[1];

        long totalOrders = history == null ? 0 : history[0];
        long successfulOrders = history == null ? 0 : history[1];
        double historyScore = totalOrders == 0 ? 50.0 : round((double) successfulOrders / totalOrders * 100);

        double overall = round(
                availScore   * 0.25 +
                priceScore   * 0.25 +
                qualityScore * 0.20 +
                deliveryScore * 0.15 +
                historyScore * 0.15
        );

        return new VendorRecommendationResponse.VendorRanking(
                vp.getVendor().getId(),
                vp.getVendor().getVendorName(),
                vp.getPrice(),
                available,
                overall,
                round(priceScore),
                round(availScore),
                round(qualityScore),
                round(deliveryScore),
                round(historyScore),
                vp.getLeadTimeDays(),
                totalOrders,
                successfulOrders
        );
    }

    /** Convert 1-5 rating to 0-100 score. */
    private double ratingToScore(Number value) {
        if (value == null) return 50.0;
        return round(Math.max(0, Math.min(5, value.doubleValue())) * 20.0);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
