package com.campus.device.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaptchaUtil {

    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final Random RANDOM = new Random();

    public static Map<String, Object> generateCaptcha(int width, int height, int length) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);

        // Interference lines
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(RANDOM.nextInt(200), RANDOM.nextInt(200), RANDOM.nextInt(200)));
            g.drawLine(RANDOM.nextInt(width), RANDOM.nextInt(height),
                       RANDOM.nextInt(width), RANDOM.nextInt(height));
        }

        // Interference dots
        for (int i = 0; i < 30; i++) {
            g.setColor(new Color(RANDOM.nextInt(200), RANDOM.nextInt(200), RANDOM.nextInt(200)));
            g.fillOval(RANDOM.nextInt(width), RANDOM.nextInt(height), 2, 2);
        }

        // Draw characters
        StringBuilder code = new StringBuilder();
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < length; i++) {
            char c = CHARS.charAt(RANDOM.nextInt(CHARS.length()));
            code.append(c);
            g.setColor(new Color(20 + RANDOM.nextInt(110), 20 + RANDOM.nextInt(110), 20 + RANDOM.nextInt(110)));
            g.drawString(String.valueOf(c), 20 + i * (width - 40) / length, 28 + RANDOM.nextInt(6));
        }

        g.dispose();

        Map<String, Object> result = new HashMap<>();
        result.put("code", code.toString());
        result.put("image", image);
        return result;
    }

    public static Map<String, Object> generateCaptcha() {
        return generateCaptcha(120, 40, 4);
    }
}
