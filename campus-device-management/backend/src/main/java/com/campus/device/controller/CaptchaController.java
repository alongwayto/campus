package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.util.CaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Api(tags = "Captcha")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    // In-memory captcha store with expiry (5 minutes)
    private static final Map<String, CaptchaEntry> CAPTCHA_STORE = new ConcurrentHashMap<>();
    private static final long CAPTCHA_EXPIRY_MS = 5 * 60 * 1000;

    @ApiOperation("Get captcha image")
    @GetMapping
    public Result<Map<String, String>> getCaptcha() throws Exception {
        // Clean expired entries
        cleanExpired();

        Map<String, Object> captcha = CaptchaUtil.generateCaptcha();
        String code = (String) captcha.get("code");
        BufferedImage image = (BufferedImage) captcha.get("image");

        // Generate session ID
        String sessionId = UUID.randomUUID().toString().replace("-", "");

        // Store captcha code (case-insensitive)
        CAPTCHA_STORE.put(sessionId, new CaptchaEntry(code.toLowerCase(), System.currentTimeMillis()));

        // Convert image to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());

        Map<String, String> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("image", base64Image);
        return Result.success(result);
    }

    /**
     * Validate captcha code. Returns true if valid, false otherwise.
     * One-time validation: captcha is removed after verification.
     */
    public static boolean validateCaptcha(String sessionId, String code) {
        if (sessionId == null || code == null) {
            return false;
        }
        CaptchaEntry entry = CAPTCHA_STORE.remove(sessionId);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() - entry.timestamp > CAPTCHA_EXPIRY_MS) {
            return false;
        }
        return entry.code.equals(code.toLowerCase());
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        CAPTCHA_STORE.entrySet().removeIf(e -> now - e.getValue().timestamp > CAPTCHA_EXPIRY_MS);
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
