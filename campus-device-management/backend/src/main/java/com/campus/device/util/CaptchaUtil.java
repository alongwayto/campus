package com.campus.device.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final String CHAR_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int DEFAULT_LENGTH = 4;
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 40;

    private final Random random = new Random();

    public String generateCode() {
        return generateCode(DEFAULT_LENGTH);
    }

    public String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public String generateImageBase64(String code) {
        return generateImageBase64(code, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public String generateImageBase64(String code, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);

        // Draw characters
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x = 10 + i * (width - 20) / code.length();
            int y = 25 + random.nextInt(10);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }

        // Interference lines (5-8)
        int lineCount = 5 + random.nextInt(4);
        for (int i = 0; i < lineCount; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }

        // Noise dots (30-50)
        int dotCount = 30 + random.nextInt(21);
        for (int i = 0; i < dotCount; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(random.nextInt(width), random.nextInt(height), 2, 2);
        }

        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }
}
