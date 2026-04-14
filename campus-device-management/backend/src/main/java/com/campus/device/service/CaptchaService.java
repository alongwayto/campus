package com.campus.device.service;

import java.util.Map;

public interface CaptchaService {

    Map<String, String> generateCaptcha();

    boolean validateCaptcha(String sessionId, String code);
}
