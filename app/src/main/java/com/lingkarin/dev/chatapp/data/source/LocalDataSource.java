package com.lingkarin.dev.chatapp.data.source;

import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;

import java.util.List;

public interface LocalDataSource {
    void insertDeliveryReceipt(DeliveryReceiptData deliveryReceiptData);

    DeliveryReceiptData getDeliveryReceipt(String messageId);

    List<DeliveryReceiptData> getDeliveryReceipts(String fromJid, String toJid);

    void updateDeliveryReceipt(String messageId, String status);

    void deleteDeliveryReceipt(String messageId);
}
