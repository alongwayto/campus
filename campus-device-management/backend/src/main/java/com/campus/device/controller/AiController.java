package com.campus.device.controller;

import com.campus.device.exception.Result;
import com.campus.device.model.vo.AiPredictionVO;
import com.campus.device.service.AiDiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AI智能功能", description = "AI故障诊断、设备预测、异常检测等接口")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiDiagnosisService aiDiagnosisService;

    @Operation(summary = "故障智能诊断", description = "根据故障描述文本进行AI智能诊断")
    @PostMapping("/diagnose")
    public Result<Map<String, Object>> diagnoseFault(
            @RequestParam(required = false) Long deviceId,
            @RequestBody Map<String, String> body) {
        String description = body.getOrDefault("description", "");
        return Result.success(aiDiagnosisService.diagnoseFault(description, deviceId));
    }

    @Operation(summary = "设备故障预测", description = "预测指定设备的故障概率")
    @GetMapping("/predict/{deviceId}")
    public Result<AiPredictionVO> predictDeviceFailure(@PathVariable Long deviceId) {
        return Result.success(aiDiagnosisService.predictDeviceFailure(deviceId));
    }

    @Operation(summary = "批量设备故障预测", description = "批量预测多个设备的故障概率")
    @PostMapping("/predict/batch")
    public Result<List<AiPredictionVO>> batchPredictFailure(@RequestBody List<Long> deviceIds) {
        return Result.success(aiDiagnosisService.batchPredictFailure(deviceIds));
    }

    @Operation(summary = "设备异常检测", description = "检测设备实时指标是否存在异常")
    @PostMapping("/anomaly/{deviceId}")
    public Result<Map<String, Object>> detectAnomaly(
            @PathVariable Long deviceId,
            @RequestBody Map<String, Double> metrics) {
        return Result.success(aiDiagnosisService.detectAnomaly(deviceId, metrics));
    }

    @Operation(summary = "推荐维护人员", description = "基于故障类型和技能评分推荐最佳维护人员")
    @GetMapping("/recommend-maintainer/{faultId}")
    public Result<Map<String, Object>> recommendMaintainer(@PathVariable Long faultId) {
        return Result.success(aiDiagnosisService.recommendMaintainer(faultId));
    }

    @Operation(summary = "NLP文本分析", description = "提取故障描述关键词、摘要和分类")
    @PostMapping("/analyze-text")
    public Result<Map<String, Object>> analyzeText(@RequestBody Map<String, String> body) {
        String text = body.getOrDefault("text", "");
        return Result.success(aiDiagnosisService.analyzeText(text));
    }

    @Operation(summary = "智能分类", description = "自动分类故障类型、优先级并推荐处理人员")
    @PostMapping("/categorize")
    public Result<Map<String, Object>> smartCategorize(@RequestBody Map<String, String> body) {
        String description = body.getOrDefault("description", "");
        return Result.success(aiDiagnosisService.smartCategorize(description));
    }
}
