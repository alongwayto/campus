package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.service.AiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "AI Intelligence")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @ApiOperation("Diagnose fault")
    @PostMapping("/diagnose")
    public Result<Map<String, Object>> diagnoseFault(@RequestBody Map<String, Object> request) {
        Long deviceId = Long.valueOf(request.get("deviceId").toString());
        String description = (String) request.get("description");
        return Result.success(aiService.diagnoseFault(deviceId, description));
    }

    @ApiOperation("Predict fault probability")
    @GetMapping("/predict/{deviceId}")
    public Result<Map<String, Object>> predictFault(@PathVariable Long deviceId) {
        return Result.success(aiService.predictFault(deviceId));
    }

    @ApiOperation("Get device health score")
    @GetMapping("/health/{deviceId}")
    public Result<Map<String, Object>> getHealthScore(@PathVariable Long deviceId) {
        return Result.success(aiService.getDeviceHealthScore(deviceId));
    }

    @ApiOperation("Recommend maintainer")
    @PostMapping("/recommend-maintainer")
    public Result<List<Map<String, Object>>> recommendMaintainer(@RequestBody Map<String, Object> request) {
        Long deviceId = Long.valueOf(request.get("deviceId").toString());
        String description = (String) request.getOrDefault("description", "");
        return Result.success(aiService.recommendMaintainer(deviceId, description));
    }

    @ApiOperation("Analyze fault text with NLP")
    @PostMapping("/analyze-text")
    public Result<Map<String, Object>> analyzeText(@RequestBody Map<String, String> request) {
        return Result.success(aiService.analyzeFaultText(request.get("text")));
    }

    @ApiOperation("Get maintenance suggestions")
    @GetMapping("/maintenance-suggestion/{deviceId}")
    public Result<Map<String, Object>> getMaintenanceSuggestion(@PathVariable Long deviceId) {
        return Result.success(aiService.getMaintenanceSuggestion(deviceId));
    }

    @ApiOperation("Predict device lifecycle")
    @GetMapping("/lifecycle/{deviceId}")
    public Result<Map<String, Object>> predictLifecycle(@PathVariable Long deviceId) {
        return Result.success(aiService.predictDeviceLifecycle(deviceId));
    }

    @ApiOperation("Detect anomalies")
    @GetMapping("/anomalies")
    public Result<List<Map<String, Object>>> detectAnomalies() {
        return Result.success(aiService.detectAnomalies());
    }

    @ApiOperation("Recommend spare parts")
    @PostMapping("/recommend-parts")
    public Result<List<Map<String, Object>>> recommendSpareParts(@RequestBody Map<String, Object> request) {
        Long deviceId = Long.valueOf(request.get("deviceId").toString());
        String description = (String) request.getOrDefault("description", "");
        return Result.success(aiService.recommendSpareParts(deviceId, description));
    }
}
