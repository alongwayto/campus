package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.model.dto.AiDiagnosisResult;
import com.campus.device.model.dto.AiPredictionResult;
import com.campus.device.service.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "AI Analysis")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @ApiOperation("AI故障诊断")
    @GetMapping("/diagnose/{faultId}")
    public Result<AiDiagnosisResult> diagnoseFault(@PathVariable Long faultId) {
        return Result.success(aiService.diagnoseFault(faultId));
    }

    @ApiOperation("设备健康预测")
    @GetMapping("/predict/{deviceId}")
    public Result<AiPredictionResult> predictDeviceHealth(@PathVariable Long deviceId) {
        return Result.success(aiService.predictDeviceHealth(deviceId));
    }

    @ApiOperation("异常检测")
    @GetMapping("/anomalies")
    public Result<List<Map<String, Object>>> detectAnomalies() {
        return Result.success(aiService.detectAnomalies());
    }

    @ApiOperation("设备健康评分")
    @GetMapping("/health/{deviceId}")
    public Result<Map<String, Object>> getDeviceHealthScore(@PathVariable Long deviceId) {
        return Result.success(aiService.getDeviceHealthScore(deviceId));
    }

    @ApiOperation("推荐维修人员")
    @GetMapping("/recommend-maintainer/{faultId}")
    public Result<List<Map<String, Object>>> recommendMaintainer(@PathVariable Long faultId) {
        return Result.success(aiService.recommendMaintainer(faultId));
    }

    @ApiOperation("文本分析")
    @PostMapping("/analyze-text")
    public Result<Map<String, Object>> analyzeText(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        return Result.success(aiService.analyzeText(text));
    }
}
