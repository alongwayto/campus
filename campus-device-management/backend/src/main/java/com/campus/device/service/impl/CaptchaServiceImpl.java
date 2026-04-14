package com.campus.device.service.impl;

import com.campus.device.service.CaptchaService;
import com.campus.device.util.CaptchaUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final long EXPIRY_MILLIS = 5 * 60 * 1000L;

    private final ConcurrentHashMap<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();
    private final CaptchaUtil captchaUtil = new CaptchaUtil();

    @Override
    public Map<String, String> generateCaptcha() {
        String sessionId = UUID.randomUUID().toString();
        String code = captchaUtil.generateCode();
        String imageBase64 = captchaUtil.generateImageBase64(code);

        captchaStore.put(sessionId, new CaptchaEntry(code, System.currentTimeMillis()));

        Map<String, String> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("captchaImage", "data:image/png;base64," + imageBase64);
        return result;
    }

    @Override
    public boolean validateCaptcha(String sessionId, String code) {
        if (sessionId == null || code == null) {
            return false;
        }
        CaptchaEntry entry = captchaStore.remove(sessionId);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() - entry.timestamp > EXPIRY_MILLIS) {
            return false;
        }
        return entry.code.equalsIgnoreCase(code);
    }

    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredCaptchas() {
        long now = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(e -> now - e.getValue().timestamp > EXPIRY_MILLIS);
    }

    private static class CaptchaEntry {
        final String code;
        final long timestamp;

        CaptchaEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}
