package com.campus.device.service.impl;

import com.campus.device.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendFaultAlert(Long faultId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "FAULT_ALERT");
        payload.put("faultId", faultId);
        payload.put("message", message);
        payload.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/faults", payload);
        log.info("Fault alert sent for fault {}: {}", faultId, message);
    }

    @Override
    public void sendSystemNotification(String userId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "SYSTEM_NOTIFICATION");
        payload.put("message", message);
        payload.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", payload);
        log.info("Notification sent to user {}: {}", userId, message);
    }

    @Override
    public void broadcastSystemMessage(String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "BROADCAST");
        payload.put("message", message);
        payload.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/system", payload);
        log.info("Broadcast message sent: {}", message);
    }
}
