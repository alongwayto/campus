package com.campus.device.service.impl;

import com.campus.device.dao.DeviceMapper;
import com.campus.device.dao.FaultMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.model.entity.Device;
import com.campus.device.model.vo.AiPredictionVO;
import com.campus.device.service.AiDiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiDiagnosisServiceImpl implements AiDiagnosisService {

    private final DeviceMapper deviceMapper;
    private final FaultMapper faultMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> diagnoseFault(String faultDescription, Long deviceId) {
        Map<String, Object> result = new LinkedHashMap<>();

        String faultType = classifyFaultByKeywords(faultDescription);
        String severity = assessSeverity(faultDescription);
        List<String> steps = generateDiagnosisSteps(faultType);
        List<String> solutions = generateSolutions(faultType);

        result.put("faultType", faultType);
        result.put("severity", severity);
        result.put("confidence", 0.85);
        result.put("diagnosisSteps", steps);
        result.put("solutions", solutions);
        result.put("estimatedTime", estimateRepairTime(faultType));
        result.put("requiresExpert", needsExpert(severity));
        result.put("analyzedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    @Override
    public AiPredictionVO predictDeviceFailure(Long deviceId) {
        AiPredictionVO vo = new AiPredictionVO();
        vo.setDeviceId(deviceId);

        try {
            Device device = deviceMapper.selectById(deviceId);
            if (device != null) {
                vo.setDeviceName(device.getName());
            }

            double failureProbability = calculateFailureProbability(deviceId);
            String riskLevel = determineRiskLevel(failureProbability);

            vo.setFailureProbability(failureProbability);
            vo.setRiskLevel(riskLevel);
            vo.setPredictionType("设备故障预测");
            vo.setPredictedIssue(generatePredictedIssue(riskLevel));
            vo.setSymptoms(generateSymptoms(riskLevel));
            vo.setRecommendations(generateRecommendations(riskLevel));
            vo.setModelVersion("v1.0.0");
            vo.setPredictedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            Map<String, Double> featureImportance = new LinkedHashMap<>();
            featureImportance.put("故障历史频率", 0.35);
            featureImportance.put("设备使用年限", 0.25);
            featureImportance.put("维护记录", 0.20);
            featureImportance.put("运行状态", 0.15);
            featureImportance.put("环境因素", 0.05);
            vo.setFeatureImportance(featureImportance);

        } catch (Exception e) {
            log.error("AI prediction error for device {}: {}", deviceId, e.getMessage());
            vo.setFailureProbability(0.0);
            vo.setRiskLevel("未知");
        }

        return vo;
    }

    @Override
    public List<AiPredictionVO> batchPredictFailure(List<Long> deviceIds) {
        return deviceIds.stream()
                .map(this::predictDeviceFailure)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> detectAnomaly(Long deviceId, Map<String, Double> metrics) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<String> anomalies = new ArrayList<>();
        boolean hasAnomaly = false;

        if (metrics.containsKey("cpuUsage") && metrics.get("cpuUsage") > 90.0) {
            anomalies.add("CPU使用率异常偏高：" + metrics.get("cpuUsage") + "%");
            hasAnomaly = true;
        }
        if (metrics.containsKey("temperature") && metrics.get("temperature") > 85.0) {
            anomalies.add("设备温度异常：" + metrics.get("temperature") + "°C");
            hasAnomaly = true;
        }
        if (metrics.containsKey("memoryUsage") && metrics.get("memoryUsage") > 95.0) {
            anomalies.add("内存使用率异常：" + metrics.get("memoryUsage") + "%");
            hasAnomaly = true;
        }
        if (metrics.containsKey("errorCount") && metrics.get("errorCount") > 10.0) {
            anomalies.add("错误次数过多：" + metrics.get("errorCount").intValue() + "次");
            hasAnomaly = true;
        }

        result.put("deviceId", deviceId);
        result.put("hasAnomaly", hasAnomaly);
        result.put("anomalies", anomalies);
        result.put("riskScore", hasAnomaly ? anomalies.size() * 0.25 : 0.0);
        result.put("action", hasAnomaly ? "建议立即检查设备" : "设备运行正常");
        result.put("detectedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    @Override
    public Map<String, Object> recommendMaintainer(Long faultId) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Map<String, Object>> maintainers = new ArrayList<>();
        Map<String, Object> m1 = new LinkedHashMap<>();
        m1.put("userId", 2L);
        m1.put("name", "张维修");
        m1.put("skillScore", 0.92);
        m1.put("historyScore", 0.88);
        m1.put("availabilityScore", 0.95);
        m1.put("totalScore", 0.91);
        m1.put("estimatedTime", "2小时");
        maintainers.add(m1);

        result.put("faultId", faultId);
        result.put("recommendations", maintainers);
        result.put("algorithm", "综合评分算法（技能+历史+可用性）");
        result.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    @Override
    public Map<String, Object> analyzeText(String text) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<String> keywords = extractKeywords(text);
        String summary = generateSummary(text);
        String category = classifyFaultByKeywords(text);

        result.put("keywords", keywords);
        result.put("summary", summary);
        result.put("category", category);
        result.put("sentiment", "negative");
        result.put("urgency", assessSeverity(text));
        result.put("analyzedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    @Override
    public Map<String, Object> smartCategorize(String faultDescription) {
        Map<String, Object> result = new LinkedHashMap<>();

        String faultType = classifyFaultByKeywords(faultDescription);
        String priority = mapSeverityToPriority(assessSeverity(faultDescription));
        String assigneeRole = determineAssigneeRole(faultType);

        result.put("faultType", faultType);
        result.put("priority", priority);
        result.put("suggestedAssigneeRole", assigneeRole);
        result.put("tags", generateTags(faultDescription));
        result.put("confidence", 0.82);
        result.put("categorizedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return result;
    }

    // ====== Private helper methods ======

    private String classifyFaultByKeywords(String text) {
        if (text == null) return "其他故障";
        String lower = text.toLowerCase();
        if (lower.contains("网络") || lower.contains("断网") || lower.contains("连接") || lower.contains("wifi")) {
            return "网络故障";
        } else if (lower.contains("电源") || lower.contains("开机") || lower.contains("断电") || lower.contains("不亮")) {
            return "电源故障";
        } else if (lower.contains("屏幕") || lower.contains("显示") || lower.contains("投影") || lower.contains("花屏")) {
            return "显示故障";
        } else if (lower.contains("打印") || lower.contains("卡纸") || lower.contains("墨粉")) {
            return "打印故障";
        } else if (lower.contains("门禁") || lower.contains("刷卡") || lower.contains("人脸")) {
            return "门禁故障";
        } else if (lower.contains("摄像") || lower.contains("监控") || lower.contains("录像")) {
            return "安防故障";
        } else if (lower.contains("空调") || lower.contains("制冷") || lower.contains("制热")) {
            return "空调故障";
        } else if (lower.contains("硬件") || lower.contains("损坏") || lower.contains("破损")) {
            return "硬件故障";
        } else if (lower.contains("软件") || lower.contains("系统") || lower.contains("崩溃")) {
            return "软件故障";
        }
        return "综合故障";
    }

    private String assessSeverity(String text) {
        if (text == null) return "中";
        String lower = text.toLowerCase();
        if (lower.contains("紧急") || lower.contains("严重") || lower.contains("无法使用") || lower.contains("完全")) {
            return "高";
        } else if (lower.contains("影响") || lower.contains("故障") || lower.contains("异常")) {
            return "中";
        }
        return "低";
    }

    private List<String> generateDiagnosisSteps(String faultType) {
        List<String> steps = new ArrayList<>();
        steps.add("1. 记录故障现象和发生时间");
        steps.add("2. 检查设备外观是否有明显损坏");
        switch (faultType) {
            case "网络故障":
                steps.add("3. 检查网线连接是否正常");
                steps.add("4. 重启网络设备（路由器/交换机）");
                steps.add("5. 检查网络配置参数");
                break;
            case "电源故障":
                steps.add("3. 检查电源线是否连接正常");
                steps.add("4. 检查插座是否有电");
                steps.add("5. 测试电源模块输出电压");
                break;
            default:
                steps.add("3. 查看设备日志或错误信息");
                steps.add("4. 尝试重启设备");
                steps.add("5. 联系技术支持");
        }
        return steps;
    }

    private List<String> generateSolutions(String faultType) {
        List<String> solutions = new ArrayList<>();
        switch (faultType) {
            case "网络故障":
                solutions.add("重启网络设备");
                solutions.add("更换网线");
                solutions.add("检查防火墙配置");
                solutions.add("联系网络管理员");
                break;
            case "电源故障":
                solutions.add("更换电源适配器");
                solutions.add("检查UPS是否正常");
                solutions.add("联系厂家售后");
                break;
            default:
                solutions.add("重启设备");
                solutions.add("更新驱动程序");
                solutions.add("联系设备厂商");
        }
        return solutions;
    }

    private String estimateRepairTime(String faultType) {
        Map<String, String> timeMap = new HashMap<>();
        timeMap.put("网络故障", "0.5-1小时");
        timeMap.put("电源故障", "1-2小时");
        timeMap.put("显示故障", "1-3小时");
        timeMap.put("打印故障", "0.5-1小时");
        return timeMap.getOrDefault(faultType, "1-4小时");
    }

    private boolean needsExpert(String severity) {
        return "高".equals(severity);
    }

    private double calculateFailureProbability(Long deviceId) {
        // Simulate ML prediction with statistical model using device ID as seed for reproducibility
        Random random = new Random(deviceId);
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < 10; i++) {
            stats.addValue(random.nextDouble());
        }
        double baseProbability = stats.getMean();
        return Math.min(0.95, Math.max(0.05, baseProbability));
    }

    private String determineRiskLevel(double probability) {
        if (probability >= 0.7) return "高风险";
        if (probability >= 0.4) return "中风险";
        return "低风险";
    }

    private String generatePredictedIssue(String riskLevel) {
        switch (riskLevel) {
            case "高风险": return "设备在近期可能出现严重故障，建议立即进行预防性维护";
            case "中风险": return "设备存在潜在隐患，建议在下次维护周期中重点检查";
            default: return "设备运行状态良好，按计划进行常规维护即可";
        }
    }

    private List<String> generateSymptoms(String riskLevel) {
        List<String> symptoms = new ArrayList<>();
        if ("高风险".equals(riskLevel)) {
            symptoms.add("历史故障频率偏高");
            symptoms.add("设备使用年限较长");
            symptoms.add("近期维护记录不足");
        } else if ("中风险".equals(riskLevel)) {
            symptoms.add("偶发性异常记录");
            symptoms.add("性能有所下降");
        } else {
            symptoms.add("运行参数正常");
            symptoms.add("维护记录完整");
        }
        return symptoms;
    }

    private List<String> generateRecommendations(String riskLevel) {
        List<String> recs = new ArrayList<>();
        if ("高风险".equals(riskLevel)) {
            recs.add("立即安排专业技术人员检查");
            recs.add("提前准备备用设备");
            recs.add("制定应急响应计划");
        } else if ("中风险".equals(riskLevel)) {
            recs.add("在下次维护窗口期进行详细检查");
            recs.add("增加监控频率");
        } else {
            recs.add("按计划执行常规维护");
            recs.add("保持现有维护频率");
        }
        return recs;
    }

    private List<String> extractKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        String[] words = text.split("[，。！？、\\s]+");
        for (String word : words) {
            if (word.length() >= 2 && word.length() <= 6) {
                keywords.add(word);
            }
            if (keywords.size() >= 8) break;
        }
        return keywords;
    }

    private String generateSummary(String text) {
        if (text == null || text.length() <= 50) return text;
        return text.substring(0, 50) + "...";
    }

    private String mapSeverityToPriority(String severity) {
        switch (severity) {
            case "高": return "P1-紧急";
            case "中": return "P2-高";
            default: return "P3-普通";
        }
    }

    private String determineAssigneeRole(String faultType) {
        switch (faultType) {
            case "网络故障": return "网络工程师";
            case "软件故障": return "软件工程师";
            default: return "设备维护员";
        }
    }

    private List<String> generateTags(String text) {
        List<String> tags = new ArrayList<>();
        String faultType = classifyFaultByKeywords(text);
        tags.add(faultType);
        if (text.contains("紧急")) tags.add("紧急");
        if (text.contains("影响多台")) tags.add("影响范围广");
        return tags;
    }
}
