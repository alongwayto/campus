package com.campus.device.service;

import com.campus.device.model.vo.AiPredictionVO;
import java.util.List;
import java.util.Map;

public interface AiDiagnosisService {

    /**
     * 故障诊断 - 分析故障描述文本
     */
    Map<String, Object> diagnoseFault(String faultDescription, Long deviceId);

    /**
     * 设备故障预测 - 基于历史数据和设备指标预测失效概率
     */
    AiPredictionVO predictDeviceFailure(Long deviceId);

    /**
     * 批量设备故障预测
     */
    List<AiPredictionVO> batchPredictFailure(List<Long> deviceIds);

    /**
     * 异常检测 - 检测设备异常行为
     */
    Map<String, Object> detectAnomaly(Long deviceId, Map<String, Double> metrics);

    /**
     * 维护人员推荐
     */
    Map<String, Object> recommendMaintainer(Long faultId);

    /**
     * NLP分析 - 提取故障关键词和摘要
     */
    Map<String, Object> analyzeText(String text);

    /**
     * 智能分类 - 自动分类故障类型和优先级
     */
    Map<String, Object> smartCategorize(String faultDescription);
}
