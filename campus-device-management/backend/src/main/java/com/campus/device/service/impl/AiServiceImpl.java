package com.campus.device.service.impl;

import com.campus.device.dao.DeviceMapper;
import com.campus.device.dao.FaultMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.model.entity.Device;
import com.campus.device.model.entity.FaultRecord;
import com.campus.device.model.entity.User;
import com.campus.device.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final DeviceMapper deviceMapper;
    private final FaultMapper faultMapper;
    private final UserMapper userMapper;

    // Keywords for NLP-like text analysis
    private static final Map<String, List<String>> FAULT_KEYWORDS = new HashMap<>();
    static {
        FAULT_KEYWORDS.put("电源故障", Arrays.asList("无法开机", "电源", "不亮", "无反应", "断电", "不通电"));
        FAULT_KEYWORDS.put("网络故障", Arrays.asList("断网", "网络", "连接", "掉线", "丢包", "延迟", "WiFi", "信号"));
        FAULT_KEYWORDS.put("硬件故障", Arrays.asList("硬盘", "内存", "CPU", "风扇", "噪音", "过热", "蓝屏", "死机"));
        FAULT_KEYWORDS.put("显示故障", Arrays.asList("屏幕", "显示", "闪烁", "花屏", "黑屏", "模糊", "色彩", "偏色"));
        FAULT_KEYWORDS.put("打印故障", Arrays.asList("卡纸", "打印", "墨粉", "碳粉", "墨水", "条纹", "不清晰"));
        FAULT_KEYWORDS.put("机械故障", Arrays.asList("卡纸", "震动", "异响", "磨损", "松动", "抖动", "噪音"));
        FAULT_KEYWORDS.put("软件故障", Arrays.asList("系统", "软件", "升级", "固件", "配置", "初始化", "缓慢", "卡顿"));
        FAULT_KEYWORDS.put("安防故障", Arrays.asList("门禁", "摄像头", "刷卡", "人脸", "监控", "识别", "报警"));
    }

    private static final Map<String, List<String>> SOLUTION_MAP = new HashMap<>();
    static {
        SOLUTION_MAP.put("电源故障", Arrays.asList("检查电源线连接", "测试电源插座", "检查电源模块", "更换电源适配器", "检查保险丝"));
        SOLUTION_MAP.put("网络故障", Arrays.asList("检查网线连接", "重启网络设备", "检查IP配置", "更新固件", "检查信道干扰"));
        SOLUTION_MAP.put("硬件故障", Arrays.asList("运行硬件诊断程序", "检查散热系统", "更换故障组件", "清洁内部灰尘", "检查系统日志"));
        SOLUTION_MAP.put("显示故障", Arrays.asList("检查视频线缆", "调整显示设置", "更换显示模块", "检查显卡状态", "校准色彩"));
        SOLUTION_MAP.put("打印故障", Arrays.asList("清理进纸通道", "更换耗材", "清洁打印头", "校准打印质量", "检查驱动程序"));
        SOLUTION_MAP.put("机械故障", Arrays.asList("检查机械部件", "润滑活动部件", "紧固松动螺丝", "更换磨损部件", "检查传动系统"));
        SOLUTION_MAP.put("软件故障", Arrays.asList("重启设备", "更新固件/驱动", "重置配置", "清理缓存", "重新安装软件"));
        SOLUTION_MAP.put("安防故障", Arrays.asList("重启安防设备", "检查传感器", "更新识别数据库", "检查通讯模块", "校准识别模块"));
    }

    @Override
    public Map<String, Object> diagnoseFault(Long deviceId, String faultDescription) {
        log.info("AI diagnosing fault for device {} with description: {}", deviceId, faultDescription);
        Map<String, Object> result = new LinkedHashMap<>();

        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            result.put("error", "设备不存在");
            return result;
        }

        // Analyze fault description using keyword matching
        List<Map<String, Object>> diagnoses = new ArrayList<>();
        Map<String, Double> scores = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : FAULT_KEYWORDS.entrySet()) {
            String faultType = entry.getKey();
            List<String> keywords = entry.getValue();
            double score = 0;

            for (String keyword : keywords) {
                if (faultDescription != null && faultDescription.contains(keyword)) {
                    score += 1.0 / keywords.size();
                }
            }

            if (score > 0) {
                scores.put(faultType, Math.min(score * 100, 95));
            }
        }

        // Sort by confidence score
        scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> {
                    Map<String, Object> diagnosis = new LinkedHashMap<>();
                    diagnosis.put("faultType", entry.getKey());
                    diagnosis.put("confidence", Math.round(entry.getValue() * 10.0) / 10.0);
                    diagnosis.put("solutions", SOLUTION_MAP.getOrDefault(entry.getKey(), Collections.emptyList()));
                    diagnoses.add(diagnosis);
                });

        // If no matches found, provide a general diagnosis
        if (diagnoses.isEmpty()) {
            Map<String, Object> general = new LinkedHashMap<>();
            general.put("faultType", "未知故障");
            general.put("confidence", 30.0);
            general.put("solutions", Arrays.asList("联系设备厂商技术支持", "查阅设备手册", "安排现场检查", "记录故障详细信息"));
            diagnoses.add(general);
        }

        result.put("deviceId", deviceId);
        result.put("deviceName", device.getName());
        result.put("diagnoses", diagnoses);
        result.put("analysisTime", new Date());
        result.put("recommendation", diagnoses.get(0).get("faultType") + " 可能性最高，建议优先排查");

        return result;
    }

    @Override
    public Map<String, Object> predictFault(Long deviceId) {
        log.info("AI predicting faults for device {}", deviceId);
        Map<String, Object> result = new LinkedHashMap<>();

        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            result.put("error", "设备不存在");
            return result;
        }

        // Calculate risk based on device age and warranty status
        double baseRisk = 0.1;
        Calendar cal = Calendar.getInstance();

        // Age factor: older devices have higher risk
        if (device.getPurchaseDate() != null) {
            long ageMonths = (cal.getTimeInMillis() - device.getPurchaseDate().getTime()) / (1000L * 60 * 60 * 24 * 30);
            baseRisk += Math.min(ageMonths * 0.005, 0.3);
        }

        // Warranty factor: expired warranty increases risk
        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(cal.getTime())) {
            baseRisk += 0.15;
        }

        // Current status factor
        if (device.getStatus() != null) {
            if (device.getStatus() == 2) baseRisk += 0.3; // fault
            if (device.getStatus() == 0) baseRisk += 0.1; // offline
        }

        // Cap risk at 95%
        baseRisk = Math.min(baseRisk, 0.95);

        result.put("deviceId", deviceId);
        result.put("deviceName", device.getName());
        result.put("predictions", Map.of(
                "7days", Math.round(baseRisk * 30 * 10.0) / 10.0,
                "30days", Math.round(baseRisk * 60 * 10.0) / 10.0,
                "90days", Math.round(baseRisk * 85 * 10.0) / 10.0
        ));

        String riskLevel;
        if (baseRisk >= 0.6) riskLevel = "高风险";
        else if (baseRisk >= 0.3) riskLevel = "中风险";
        else riskLevel = "低风险";

        result.put("riskLevel", riskLevel);
        result.put("preventiveSuggestions", generatePreventiveSuggestions(device, riskLevel));
        result.put("analysisTime", new Date());

        return result;
    }

    @Override
    public Map<String, Object> getDeviceHealthScore(Long deviceId) {
        log.info("AI calculating health score for device {}", deviceId);
        Map<String, Object> result = new LinkedHashMap<>();

        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            result.put("error", "设备不存在");
            return result;
        }

        double healthScore = 100.0;

        // Deduct for device status
        if (device.getStatus() != null) {
            if (device.getStatus() == 2) healthScore -= 40; // fault
            if (device.getStatus() == 0) healthScore -= 15; // offline
        }

        // Deduct for age
        if (device.getPurchaseDate() != null) {
            long ageMonths = (System.currentTimeMillis() - device.getPurchaseDate().getTime()) / (1000L * 60 * 60 * 24 * 30);
            healthScore -= Math.min(ageMonths * 1.5, 30);
        }

        // Deduct for expired warranty
        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
            healthScore -= 10;
        }

        healthScore = Math.max(healthScore, 5);

        String level;
        if (healthScore >= 80) level = "优秀";
        else if (healthScore >= 60) level = "良好";
        else if (healthScore >= 40) level = "一般";
        else level = "较差";

        result.put("deviceId", deviceId);
        result.put("deviceName", device.getName());
        result.put("healthScore", Math.round(healthScore * 10.0) / 10.0);
        result.put("level", level);

        List<String> suggestions = new ArrayList<>();
        if (healthScore < 60) suggestions.add("建议安排全面检修");
        if (device.getStatus() != null && device.getStatus() == 2) suggestions.add("设备当前处于故障状态，请优先处理");
        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
            suggestions.add("设备已过保修期，建议购买延保或准备备用设备");
        }
        if (healthScore < 40) suggestions.add("设备老化严重，建议考虑更换");
        if (suggestions.isEmpty()) suggestions.add("设备状态良好，建议定期维护保养");

        result.put("suggestions", suggestions);
        result.put("analysisTime", new Date());

        return result;
    }

    @Override
    public List<Map<String, Object>> recommendMaintainer(Long deviceId, String faultDescription) {
        log.info("AI recommending maintainers for device {}", deviceId);
        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Get all users with maintainer role (role_id = 2)
        List<User> allUsers = userMapper.selectAll();
        List<User> maintainers = allUsers.stream()
                .filter(u -> u.getRoleId() != null && u.getRoleId() == 2)
                .collect(Collectors.toList());

        if (maintainers.isEmpty()) {
            Map<String, Object> noResult = new LinkedHashMap<>();
            noResult.put("message", "当前无可用维护人员");
            recommendations.add(noResult);
            return recommendations;
        }

        Random random = new Random(deviceId != null ? deviceId : 0);
        for (User maintainer : maintainers) {
            Map<String, Object> rec = new LinkedHashMap<>();
            rec.put("userId", maintainer.getId());
            rec.put("username", maintainer.getUsername());
            rec.put("realName", maintainer.getRealName());
            rec.put("phone", maintainer.getPhone());
            rec.put("matchScore", 70 + random.nextInt(25));
            rec.put("skills", generateSkills(maintainer));
            rec.put("currentLoad", random.nextInt(5) + " 个任务");
            rec.put("estimatedResponseTime", (15 + random.nextInt(45)) + " 分钟");
            recommendations.add(rec);
        }

        // Sort by match score descending
        recommendations.sort((a, b) -> ((Integer) b.get("matchScore")).compareTo((Integer) a.get("matchScore")));

        return recommendations;
    }

    @Override
    public Map<String, Object> analyzeFaultText(String text) {
        log.info("AI analyzing fault text: {}", text);
        Map<String, Object> result = new LinkedHashMap<>();

        if (text == null || text.trim().isEmpty()) {
            result.put("error", "文本内容为空");
            return result;
        }

        // Extract keywords
        List<String> extractedKeywords = new ArrayList<>();
        for (List<String> keywords : FAULT_KEYWORDS.values()) {
            for (String keyword : keywords) {
                if (text.contains(keyword)) {
                    extractedKeywords.add(keyword);
                }
            }
        }

        // Classify fault type
        Map<String, Integer> typeScores = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : FAULT_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) score++;
            }
            if (score > 0) typeScores.put(entry.getKey(), score);
        }

        String classifiedType = typeScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("未分类");

        // Determine severity
        String severity;
        if (text.contains("紧急") || text.contains("严重") || text.contains("瘫痪") || text.contains("全部")) {
            severity = "高";
        } else if (text.contains("影响") || text.contains("异常") || text.contains("故障")) {
            severity = "中";
        } else {
            severity = "低";
        }

        // Determine urgency
        String urgency;
        if (text.contains("立即") || text.contains("紧急") || text.contains("马上")) {
            urgency = "紧急";
        } else if (text.contains("尽快") || text.contains("影响")) {
            urgency = "较急";
        } else {
            urgency = "一般";
        }

        result.put("keywords", extractedKeywords);
        result.put("classifiedType", classifiedType);
        result.put("severity", severity);
        result.put("urgency", urgency);
        result.put("typeScores", typeScores);
        result.put("textLength", text.length());
        result.put("analysisTime", new Date());

        return result;
    }

    @Override
    public Map<String, Object> getMaintenanceSuggestion(Long deviceId) {
        log.info("AI generating maintenance suggestion for device {}", deviceId);
        Map<String, Object> result = new LinkedHashMap<>();

        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            result.put("error", "设备不存在");
            return result;
        }

        // Calculate optimal maintenance interval
        int suggestedIntervalDays = 90; // default quarterly
        if (device.getPurchaseDate() != null) {
            long ageMonths = (System.currentTimeMillis() - device.getPurchaseDate().getTime()) / (1000L * 60 * 60 * 24 * 30);
            if (ageMonths > 36) suggestedIntervalDays = 30; // monthly for old devices
            else if (ageMonths > 18) suggestedIntervalDays = 60; // bi-monthly
        }

        Calendar nextMaint = Calendar.getInstance();
        nextMaint.add(Calendar.DAY_OF_MONTH, suggestedIntervalDays);

        result.put("deviceId", deviceId);
        result.put("deviceName", device.getName());
        result.put("suggestedInterval", suggestedIntervalDays + " 天");
        result.put("nextMaintenanceDate", nextMaint.getTime());

        List<Map<String, Object>> maintenanceItems = new ArrayList<>();
        maintenanceItems.add(Map.of("item", "外观检查", "priority", "常规", "estimatedTime", "10分钟"));
        maintenanceItems.add(Map.of("item", "功能测试", "priority", "重要", "estimatedTime", "20分钟"));
        maintenanceItems.add(Map.of("item", "清洁保养", "priority", "常规", "estimatedTime", "15分钟"));
        maintenanceItems.add(Map.of("item", "固件/软件更新", "priority", "重要", "estimatedTime", "30分钟"));
        maintenanceItems.add(Map.of("item", "安全检查", "priority", "重要", "estimatedTime", "15分钟"));

        result.put("maintenanceItems", maintenanceItems);
        result.put("estimatedCost", "200-500元");
        result.put("analysisTime", new Date());

        return result;
    }

    @Override
    public Map<String, Object> predictDeviceLifecycle(Long deviceId) {
        log.info("AI predicting lifecycle for device {}", deviceId);
        Map<String, Object> result = new LinkedHashMap<>();

        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            result.put("error", "设备不存在");
            return result;
        }

        // Estimate remaining useful life based on typical device lifespan
        int typicalLifespanYears = 5;
        long ageMonths = 0;
        if (device.getPurchaseDate() != null) {
            ageMonths = (System.currentTimeMillis() - device.getPurchaseDate().getTime()) / (1000L * 60 * 60 * 24 * 30);
        }

        long remainingMonths = Math.max(typicalLifespanYears * 12 - ageMonths, 0);
        double usagePercentage = Math.min((double) ageMonths / (typicalLifespanYears * 12) * 100, 100);

        result.put("deviceId", deviceId);
        result.put("deviceName", device.getName());
        result.put("typicalLifespan", typicalLifespanYears + " 年");
        result.put("currentAge", ageMonths + " 个月");
        result.put("remainingLife", remainingMonths + " 个月");
        result.put("usagePercentage", Math.round(usagePercentage * 10.0) / 10.0);

        String phase;
        if (usagePercentage < 30) phase = "成长期";
        else if (usagePercentage < 70) phase = "稳定期";
        else if (usagePercentage < 90) phase = "老化期";
        else phase = "淘汰期";
        result.put("lifecyclePhase", phase);

        Calendar suggestedReplacement = Calendar.getInstance();
        suggestedReplacement.add(Calendar.MONTH, (int) Math.max(remainingMonths - 3, 0));
        result.put("suggestedReplacementDate", suggestedReplacement.getTime());

        result.put("costAnalysis", Map.of(
                "currentValue", "约 " + Math.round(Math.max(0, 100 - usagePercentage)) + "% 残值",
                "maintenanceTrend", usagePercentage > 60 ? "维护成本上升" : "维护成本稳定",
                "replacementAdvice", usagePercentage > 80 ? "建议近期规划更换" : "可继续使用"
        ));
        result.put("analysisTime", new Date());

        return result;
    }

    @Override
    public List<Map<String, Object>> detectAnomalies() {
        log.info("AI detecting anomalies across all devices");
        List<Map<String, Object>> anomalies = new ArrayList<>();

        List<Device> devices = deviceMapper.selectAll();
        for (Device device : devices) {
            // Check for fault status
            if (device.getStatus() != null && device.getStatus() == 2) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("deviceId", device.getId());
                anomaly.put("deviceName", device.getName());
                anomaly.put("type", "设备故障");
                anomaly.put("severity", "高");
                anomaly.put("description", "设备当前处于故障状态");
                anomaly.put("suggestion", "尽快安排维修人员处理");
                anomaly.put("detectedAt", new Date());
                anomalies.add(anomaly);
            }

            // Check for offline status
            if (device.getStatus() != null && device.getStatus() == 0) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("deviceId", device.getId());
                anomaly.put("deviceName", device.getName());
                anomaly.put("type", "设备离线");
                anomaly.put("severity", "中");
                anomaly.put("description", "设备处于离线状态");
                anomaly.put("suggestion", "检查设备电源和网络连接");
                anomaly.put("detectedAt", new Date());
                anomalies.add(anomaly);
            }

            // Check warranty expiry
            if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("deviceId", device.getId());
                anomaly.put("deviceName", device.getName());
                anomaly.put("type", "保修过期");
                anomaly.put("severity", "低");
                anomaly.put("description", "设备保修已到期");
                anomaly.put("suggestion", "考虑购买延保服务或准备备用设备");
                anomaly.put("detectedAt", new Date());
                anomalies.add(anomaly);
            }
        }

        return anomalies;
    }

    @Override
    public List<Map<String, Object>> recommendSpareParts(Long deviceId, String faultDescription) {
        log.info("AI recommending spare parts for device {} with fault: {}", deviceId, faultDescription);
        List<Map<String, Object>> recommendations = new ArrayList<>();

        if (faultDescription != null) {
            if (faultDescription.contains("灯泡") || faultDescription.contains("投影")) {
                recommendations.add(Map.of("name", "投影仪灯泡", "model", "ELPLP96", "urgency", "高", "estimatedCost", "680元"));
            }
            if (faultDescription.contains("墨粉") || faultDescription.contains("碳粉") || faultDescription.contains("打印")) {
                recommendations.add(Map.of("name", "硒鼓", "model", "CF230A", "urgency", "中", "estimatedCost", "280元"));
            }
            if (faultDescription.contains("硬盘") || faultDescription.contains("存储")) {
                recommendations.add(Map.of("name", "服务器硬盘", "model", "ST2000NM0008", "urgency", "高", "estimatedCost", "1800元"));
            }
            if (faultDescription.contains("网络") || faultDescription.contains("光模块") || faultDescription.contains("交换机")) {
                recommendations.add(Map.of("name", "交换机光模块", "model", "SFP-10G-SR", "urgency", "高", "estimatedCost", "420元"));
            }
            if (faultDescription.contains("探头") || faultDescription.contains("示波器")) {
                recommendations.add(Map.of("name", "示波器探头", "model", "TPP0200", "urgency", "中", "estimatedCost", "350元"));
            }
            if (faultDescription.contains("门禁") || faultDescription.contains("刷卡")) {
                recommendations.add(Map.of("name", "门禁IC卡", "model", "M1-S50", "urgency", "低", "estimatedCost", "2.5元/张"));
            }
            if (faultDescription.contains("制冷") || faultDescription.contains("空调")) {
                recommendations.add(Map.of("name", "空调制冷剂", "model", "R410A-10kg", "urgency", "中", "estimatedCost", "280元"));
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add(Map.of("name", "通用维修工具包", "model", "TOOLKIT-01", "urgency", "低", "estimatedCost", "150元"));
        }

        return recommendations;
    }

    private List<String> generatePreventiveSuggestions(Device device, String riskLevel) {
        List<String> suggestions = new ArrayList<>();
        if ("高风险".equals(riskLevel)) {
            suggestions.add("立即安排全面检修");
            suggestions.add("准备备用设备");
            suggestions.add("联系厂商技术支持");
        } else if ("中风险".equals(riskLevel)) {
            suggestions.add("安排预防性维护");
            suggestions.add("定期监控设备状态");
            suggestions.add("准备常用备件");
        } else {
            suggestions.add("保持定期维护");
            suggestions.add("记录设备运行数据");
        }
        return suggestions;
    }

    private List<String> generateSkills(User maintainer) {
        List<String> skills = new ArrayList<>();
        skills.add("设备巡检");
        skills.add("故障排查");
        if (maintainer.getId() % 2 == 0) {
            skills.add("网络维护");
            skills.add("服务器管理");
        } else {
            skills.add("电气维修");
            skills.add("机械维护");
        }
        return skills;
    }
}
