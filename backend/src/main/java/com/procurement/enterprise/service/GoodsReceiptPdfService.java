package com.procurement.enterprise.service;

/** Generates the official, server-side Goods Receipt Note document. */
public interface GoodsReceiptPdfService {
    byte[] generate(Long receiptId);
}
