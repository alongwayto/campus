package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.service.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "Captcha")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @ApiOperation("Generate captcha")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        Map<String, String> captcha = captchaService.generateCaptcha();
        return Result.success(captcha);
    }
}
