package com.campus.device.service;

public interface NotificationService {
    void sendFaultAlert(Long faultId, String message);
    void sendSystemNotification(String userId, String message);
    void broadcastSystemMessage(String message);
}
