package com.procurement.enterprise.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.procurement.enterprise.entity.*;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GoodsReceiptPdfServiceImpl implements GoodsReceiptPdfService {
    private static final Color NAVY = new Color(24, 55, 89);
    private static final Color LIGHT_BLUE = new Color(231, 239, 247);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final ReceiptRepository receiptRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] generate(Long receiptId) {
        Receipt receipt = receiptRepository.findByIdAndIsDeletedFalse(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", receiptId));
        Delivery delivery = receipt.getDelivery();
        if (delivery == null) throw new InvalidRequestException("The receipt has no delivery information.");
        PurchaseOrder order = delivery.getPurchaseOrder();
        if (order == null) throw new InvalidRequestException("The delivery has no purchase order information.");
        Vendor vendor = order.getVendor();
        if (vendor == null) throw new InvalidRequestException("The purchase order has no vendor information.");
        List<PurchaseOrderItem> items = order.getItems().stream()
                .filter(item -> !Boolean.TRUE.equals(item.getIsDeleted())).toList();
        if (items.isEmpty()) throw new InvalidRequestException("The purchase order has no receipt items.");

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 42, 42, 46, 50);
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPageEvent(new Footer());
            document.open();
            addHeader(document, receipt, order);
            addSection(document, "VENDOR INFORMATION");
            addKeyValues(document, new String[][] {
                    {"Vendor Name", value(vendor.getVendorName())}, {"Vendor Contact", value(vendor.getContactName())},
                    {"Vendor Email", value(vendor.getEmail())}, {"Vendor Address", value(vendor.getAddress())},
                    {"GST Number", value(vendor.getGstNumber())}
            });
            addSection(document, "DELIVERY INFORMATION");
            addKeyValues(document, new String[][] {
                    {"Delivery Date", date(delivery.getDeliveryDate())}, {"Delivery Status", value(delivery.getStatus())},
                    {"Received By", receiver(receipt)}, {"Receipt Status", "RECEIVED"}
            });
            addSection(document, "ITEM DETAILS");
            addItems(document, items);
            int ordered = items.stream().map(PurchaseOrderItem::getQuantity).filter(java.util.Objects::nonNull).mapToInt(Integer::intValue).sum();
            addSection(document, "RECEIPT SUMMARY");
            addKeyValues(document, new String[][] {{"Total Items", String.valueOf(items.size())},
                    {"Total Ordered Quantity", String.valueOf(ordered)}, {"Total Delivered Quantity", String.valueOf(ordered)},
                    {"Purchase Order Value", money(order.getTotalAmount())}});
            addSection(document, "INSPECTION / RECEIPT");
            addKeyValues(document, new String[][] {{"Receipt Status", "RECEIVED"}, {"Received By", receiver(receipt)},
                    {"Received Date", date(receipt.getReceiptDate())}, {"Inspection Notes", value(receipt.getConditionNotes())}});
            Paragraph signature = new Paragraph("\n\nAuthorized Receiver\nEnterprise Procurement System", new Font(Font.HELVETICA, 10, Font.BOLD, NAVY));
            signature.setAlignment(Element.ALIGN_RIGHT); document.add(signature);
            document.close();
            return output.toByteArray();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InvalidRequestException("Unable to generate the Goods Receipt PDF.");
        }
    }

    private void addHeader(Document doc, Receipt receipt, PurchaseOrder order) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[] { 2f, 1f }); table.setWidthPercentage(100);
        PdfPCell title = cell("ENTERPRISE PROCUREMENT SYSTEM\nGOODS RECEIPT NOTE", new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE), NAVY);
        title.setPadding(14); table.addCell(title);
        PdfPCell reference = cell("RECEIPT NUMBER\nGRN-" + String.format("%06d", receipt.getId()) + "\n\nPURCHASE ORDER\n" + value(order.getPurchaseOrderNumber()), new Font(Font.HELVETICA, 9, Font.BOLD, NAVY), LIGHT_BLUE);
        reference.setPadding(10); table.addCell(reference); doc.add(table); doc.add(Chunk.NEWLINE);
        addKeyValues(doc, new String[][] {{"Receipt Date", date(receipt.getReceiptDate())}, {"Purchase Request", order.getPurchaseRequest() == null ? "—" : value(order.getPurchaseRequest().getRequestNumber())}});
    }
    private void addSection(Document doc, String heading) throws DocumentException { Paragraph p = new Paragraph(heading, new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)); p.setSpacingBefore(12); p.setSpacingAfter(5); PdfPTable t = new PdfPTable(1); t.setWidthPercentage(100); PdfPCell c = cell(p, NAVY); c.setPadding(6); t.addCell(c); doc.add(t); }
    private void addKeyValues(Document doc, String[][] values) throws DocumentException { PdfPTable table = new PdfPTable(2); table.setWidthPercentage(100); table.setWidths(new float[] { 1f, 2f }); for (String[] row : values) { table.addCell(cell(row[0], new Font(Font.HELVETICA, 9, Font.BOLD, NAVY), Color.WHITE)); table.addCell(cell(row[1], new Font(Font.HELVETICA, 9), Color.WHITE)); } doc.add(table); }
    private void addItems(Document doc, List<PurchaseOrderItem> items) throws DocumentException { PdfPTable table = new PdfPTable(new float[] { .5f, 2.3f, 1.2f, 1f, 1f }); table.setWidthPercentage(100); String[] headings = {"S.No", "Product", "SKU", "Ordered Qty", "Delivered Qty"}; for (String h : headings) table.addCell(cell(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE), NAVY)); int i = 1; for (PurchaseOrderItem item : items) { Product product = item.getProduct(); if (product == null) throw new InvalidRequestException("A purchase order item has no product information."); Color fill = i % 2 == 0 ? LIGHT_BLUE : Color.WHITE; table.addCell(cell(String.valueOf(i++), new Font(Font.HELVETICA, 8), fill)); table.addCell(cell(value(product.getName()), new Font(Font.HELVETICA, 8), fill)); table.addCell(cell(value(product.getSku()), new Font(Font.HELVETICA, 8), fill)); table.addCell(cell(String.valueOf(item.getQuantity()), new Font(Font.HELVETICA, 8), fill)); table.addCell(cell(String.valueOf(item.getQuantity()), new Font(Font.HELVETICA, 8), fill)); } doc.add(table); }
    private PdfPCell cell(String text, Font font, Color fill) { PdfPCell cell = new PdfPCell(new Phrase(text, font)); cell.setPadding(6); cell.setBackgroundColor(fill); cell.setBorderColor(new Color(205, 215, 225)); return cell; }
    private PdfPCell cell(Paragraph paragraph, Color fill) { PdfPCell cell = new PdfPCell(paragraph); cell.setPadding(6); cell.setBackgroundColor(fill); cell.setBorderColor(new Color(205, 215, 225)); return cell; }
    private String value(Object value) { return value == null || value.toString().isBlank() ? "—" : value.toString(); }
    private String date(java.time.LocalDate value) { return value == null ? "—" : DATE.format(value); }
    private String receiver(Receipt receipt) { return receipt.getReceiver() == null ? "—" : receipt.getReceiver().getFirstName() + " " + receipt.getReceiver().getLastName(); }
    private String money(BigDecimal value) { return value == null ? "—" : NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(value); }
    private static class Footer extends PdfPageEventHelper { public void onEndPage(PdfWriter writer, Document document) { ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Enterprise Procurement System  |  Goods Receipt Note  |  Page " + writer.getPageNumber(), new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY)), (document.right() + document.left()) / 2, document.bottom() - 25, 0); } }
}
