package com.campus.device.service.impl;

import com.campus.device.dao.DeviceMapper;
import com.campus.device.dao.FaultMapper;
import com.campus.device.dao.UserMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.AiDiagnosisResult;
import com.campus.device.model.dto.AiPredictionResult;
import com.campus.device.model.dto.FaultQueryParam;
import com.campus.device.model.entity.Device;
import com.campus.device.model.entity.FaultRecord;
import com.campus.device.model.entity.User;
import com.campus.device.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final DeviceMapper deviceMapper;
    private final FaultMapper faultMapper;
    private final UserMapper userMapper;

    // ==================== 故障诊断 ====================

    @Override
    public AiDiagnosisResult diagnoseFault(Long faultId) {
        FaultRecord fault = faultMapper.selectById(faultId);
        if (fault == null) {
            throw new BusinessException(404, "故障记录不存在: " + faultId);
        }

        Device device = deviceMapper.selectById(fault.getDeviceId());
        String deviceName = device != null ? device.getName() : "未知设备";

        String text = Optional.ofNullable(fault.getTitle()).orElse("")
                + " " + Optional.ofNullable(fault.getDescription()).orElse("");

        List<AiDiagnosisResult.DiagnosisItem> diagnoses = buildDiagnoses(text);

        // 如果没有匹配到任何关键词，返回通用诊断
        if (diagnoses.isEmpty()) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("设备一般性故障")
                    .confidence(0.5)
                    .category("综合")
                    .troubleshootingSteps(Arrays.asList(
                            "检查设备外观是否有明显损坏",
                            "重启设备观察是否恢复正常",
                            "查看设备日志获取更多错误信息",
                            "联系厂商技术支持获取帮助"
                    ))
                    .build());
        }

        // 根据故障历史调整置信度
        FaultQueryParam historyParam = new FaultQueryParam();
        historyParam.setDeviceId(fault.getDeviceId());
        historyParam.setPageNum(1);
        historyParam.setPageSize(100);
        List<FaultRecord> history = faultMapper.selectByCondition(historyParam);
        if (history.size() > 3) {
            for (AiDiagnosisResult.DiagnosisItem item : diagnoses) {
                item.setConfidence(Math.min(item.getConfidence() + 0.05, 0.99));
            }
        }

        String urgencyLevel = mapUrgencyLevel(fault.getSeverity());

        List<String> recommendedActions = buildRecommendedActions(fault, diagnoses);

        double overallConfidence = diagnoses.stream()
                .mapToDouble(AiDiagnosisResult.DiagnosisItem::getConfidence)
                .max().orElse(0.5);

        return AiDiagnosisResult.builder()
                .deviceName(deviceName)
                .faultTitle(fault.getTitle())
                .diagnoses(diagnoses)
                .recommendedActions(recommendedActions)
                .urgencyLevel(urgencyLevel)
                .overallConfidence(overallConfidence)
                .build();
    }

    private List<AiDiagnosisResult.DiagnosisItem> buildDiagnoses(String text) {
        List<AiDiagnosisResult.DiagnosisItem> diagnoses = new ArrayList<>();

        if (containsAny(text, "无法开机", "不通电", "电源", "启动失败", "开不了机")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("电源系统故障")
                    .confidence(0.85)
                    .category("电源")
                    .troubleshootingSteps(Arrays.asList(
                            "检查电源线是否连接正常",
                            "检查电源插座是否有电",
                            "检查电源适配器指示灯是否正常",
                            "尝试更换电源线或适配器",
                            "如仍无法启动，可能是主板电源模块损坏，需送修"
                    ))
                    .build());
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("主板硬件损坏")
                    .confidence(0.45)
                    .category("硬件")
                    .troubleshootingSteps(Arrays.asList(
                            "观察主板是否有烧焦痕迹或鼓包电容",
                            "检查内存条是否松动",
                            "尝试最小化配置启动",
                            "联系售后进行专业检测"
                    ))
                    .build());
        }

        if (containsAny(text, "黑屏", "显示", "屏幕", "花屏", "闪屏", "无画面")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("显示模块故障")
                    .confidence(0.82)
                    .category("显示")
                    .troubleshootingSteps(Arrays.asList(
                            "检查显示器电源和信号线连接",
                            "尝试调整分辨率和刷新率",
                            "更换视频线缆（HDMI/VGA/DP）",
                            "将显示器连接至其他设备测试",
                            "检查显卡是否松动或过热"
                    ))
                    .build());
        }

        if (containsAny(text, "卡纸", "打印", "墨", "碳粉", "进纸")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("打印机械故障")
                    .confidence(0.88)
                    .category("打印机")
                    .troubleshootingSteps(Arrays.asList(
                            "打开打印机盖板取出卡纸",
                            "检查进纸托盘纸张是否整齐放置",
                            "清洁进纸滚轮",
                            "检查墨盒/碳粉是否充足",
                            "运行打印机自检程序"
                    ))
                    .build());
        }

        if (containsAny(text, "断网", "网络", "无法连接", "上不了网", "WiFi", "wifi", "IP", "掉线")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("网络连接异常")
                    .confidence(0.80)
                    .category("网络")
                    .troubleshootingSteps(Arrays.asList(
                            "检查网线连接是否牢固",
                            "重启路由器和交换机",
                            "检查IP地址配置是否正确",
                            "使用ping命令测试网络连通性",
                            "检查防火墙和安全策略设置"
                    ))
                    .build());
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("网卡驱动异常")
                    .confidence(0.40)
                    .category("网络")
                    .troubleshootingSteps(Arrays.asList(
                            "在设备管理器中检查网卡状态",
                            "重新安装或更新网卡驱动",
                            "尝试禁用再启用网卡"
                    ))
                    .build());
        }

        if (containsAny(text, "异常", "噪音", "异响", "震动", "声音")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("机械部件磨损或松动")
                    .confidence(0.75)
                    .category("硬件")
                    .troubleshootingSteps(Arrays.asList(
                            "确定异响来源位置",
                            "检查风扇是否积灰或损坏",
                            "检查硬盘是否有异响（可能预示硬盘故障）",
                            "检查设备内部螺丝是否松动",
                            "及时备份重要数据"
                    ))
                    .build());
        }

        if (containsAny(text, "过热", "温度", "烫", "发热", "散热")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("散热系统异常")
                    .confidence(0.83)
                    .category("散热")
                    .troubleshootingSteps(Arrays.asList(
                            "清理设备散热口和风扇灰尘",
                            "检查散热风扇是否正常运转",
                            "更换导热硅脂",
                            "确保设备放置在通风良好的位置",
                            "检查环境温度是否过高"
                    ))
                    .build());
        }

        if (containsAny(text, "投影", "灯泡", "亮度", "投影仪", "幕布")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("投影设备故障")
                    .confidence(0.80)
                    .category("投影仪")
                    .troubleshootingSteps(Arrays.asList(
                            "检查投影仪灯泡使用时长，可能需要更换",
                            "清洁投影镜头",
                            "检查信号源连接和输入源选择",
                            "调整投影仪焦距和梯形校正",
                            "检查滤网是否需要清洁"
                    ))
                    .build());
        }

        if (containsAny(text, "门禁", "刷卡", "指纹", "人脸", "闸机", "开门")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("门禁系统故障")
                    .confidence(0.78)
                    .category("门禁")
                    .troubleshootingSteps(Arrays.asList(
                            "检查门禁控制器电源和网络连接",
                            "清洁读卡器或指纹采集窗",
                            "检查门锁电磁铁是否正常工作",
                            "重新录入用户权限信息",
                            "检查门禁软件是否需要更新"
                    ))
                    .build());
        }

        if (containsAny(text, "监控", "摄像", "录像", "画面模糊", "夜视")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("监控摄像设备故障")
                    .confidence(0.79)
                    .category("监控")
                    .troubleshootingSteps(Arrays.asList(
                            "检查摄像头电源和网线连接",
                            "清洁摄像头镜头",
                            "检查NVR/DVR存储空间和录像设置",
                            "检查红外灯是否正常（夜视功能）",
                            "重启摄像头或恢复出厂设置"
                    ))
                    .build());
        }

        if (containsAny(text, "蓝屏", "死机", "卡死", "系统崩溃", "重启")) {
            diagnoses.add(AiDiagnosisResult.DiagnosisItem.builder()
                    .possibleCause("系统软件故障")
                    .confidence(0.76)
                    .category("软件")
                    .troubleshootingSteps(Arrays.asList(
                            "记录蓝屏错误代码",
                            "检查最近安装的软件或更新",
                            "运行系统文件检查工具（sfc /scannow）",
                            "检查内存是否存在问题（Windows内存诊断）",
                            "考虑重装系统或恢复到之前的还原点"
                    ))
                    .build());
        }

        // 限制最多返回3个诊断结果，按置信度排序
        return diagnoses.stream()
                .sorted(Comparator.comparingDouble(AiDiagnosisResult.DiagnosisItem::getConfidence).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    private List<String> buildRecommendedActions(FaultRecord fault, List<AiDiagnosisResult.DiagnosisItem> diagnoses) {
        List<String> actions = new ArrayList<>();

        if (fault.getSeverity() != null && fault.getSeverity() >= 3) {
            actions.add("该故障严重级别较高，建议立即安排维修人员现场处理");
        }

        if (fault.getStatus() != null && fault.getStatus() == 0) {
            actions.add("当前故障尚未分配维修人员，建议尽快指派");
        }

        if (!diagnoses.isEmpty()) {
            AiDiagnosisResult.DiagnosisItem topDiagnosis = diagnoses.get(0);
            actions.add("优先排查「" + topDiagnosis.getPossibleCause() + "」，置信度: "
                    + String.format("%.0f%%", topDiagnosis.getConfidence() * 100));
        }

        actions.add("建议在维修完成后进行全面功能测试");
        actions.add("如多次出现同类故障，建议评估设备是否需要更换");

        return actions;
    }

    private String mapUrgencyLevel(Integer severity) {
        if (severity == null) return "一般";
        switch (severity) {
            case 3: return "紧急";
            case 2: return "较高";
            case 1: return "一般";
            default: return "一般";
        }
    }

    // ==================== 设备健康预测 ====================

    @Override
    public AiPredictionResult predictDeviceHealth(Long deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(404, "设备不存在: " + deviceId);
        }

        FaultQueryParam faultParam = new FaultQueryParam();
        faultParam.setDeviceId(deviceId);
        faultParam.setPageNum(1);
        faultParam.setPageSize(1000);
        List<FaultRecord> faultHistory = faultMapper.selectByCondition(faultParam);

        int healthScore = calculateHealthScore(device, faultHistory);
        String healthLevel = mapHealthLevel(healthScore);
        String riskLevel = mapRiskLevel(healthScore);

        double prob7 = calculateFailureProbability(healthScore, 7);
        double prob30 = calculateFailureProbability(healthScore, 30);
        double prob90 = calculateFailureProbability(healthScore, 90);

        List<String> riskFactors = identifyRiskFactors(device, faultHistory);
        List<String> preventiveActions = suggestPreventiveActions(device, faultHistory, healthScore);
        String remainingLife = estimateRemainingLife(device, healthScore);
        String nextMaintenance = recommendNextMaintenance(device, faultHistory, healthScore);

        return AiPredictionResult.builder()
                .deviceName(device.getName())
                .healthScore(healthScore)
                .healthLevel(healthLevel)
                .failureProbability7Days(prob7)
                .failureProbability30Days(prob30)
                .failureProbability90Days(prob90)
                .riskLevel(riskLevel)
                .riskFactors(riskFactors)
                .preventiveActions(preventiveActions)
                .estimatedRemainingLife(remainingLife)
                .nextMaintenanceRecommendation(nextMaintenance)
                .build();
    }

    private int calculateHealthScore(Device device, List<FaultRecord> faultHistory) {
        int score = 100;

        // 设备使用年限扣分（每年扣3分）
        if (device.getPurchaseDate() != null) {
            long ageInDays = (System.currentTimeMillis() - device.getPurchaseDate().getTime())
                    / (1000L * 60 * 60 * 24);
            int ageYears = (int) (ageInDays / 365);
            score -= ageYears * 3;
        }

        // 保修状态扣分
        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
            long expiredDays = (System.currentTimeMillis() - device.getWarrantyExpiry().getTime())
                    / (1000L * 60 * 60 * 24);
            if (expiredDays > 365) {
                score -= 15;
            } else {
                score -= 8;
            }
        }

        // 故障历史扣分
        int faultCount = faultHistory.size();
        score -= faultCount * 5;

        // 近期故障额外扣分（30天内）
        long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        long recentFaults = faultHistory.stream()
                .filter(f -> f.getCreatedAt() != null && f.getCreatedAt().getTime() > thirtyDaysAgo)
                .count();
        score -= recentFaults * 8;

        // 高严重级别故障额外扣分
        long highSeverityFaults = faultHistory.stream()
                .filter(f -> f.getSeverity() != null && f.getSeverity() >= 3)
                .count();
        score -= highSeverityFaults * 5;

        // 当前状态扣分
        if (device.getStatus() != null) {
            switch (device.getStatus()) {
                case 0: // offline
                    score -= 10;
                    break;
                case 2: // fault
                    score -= 20;
                    break;
                default:
                    break;
            }
        }

        // 未解决故障扣分
        long unresolvedFaults = faultHistory.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() < 3)
                .count();
        score -= unresolvedFaults * 7;

        return Math.max(0, Math.min(100, score));
    }

    private double calculateFailureProbability(int healthScore, int days) {
        // 基于健康分数和时间窗口计算故障概率
        double baseProb = (100.0 - healthScore) / 100.0;
        double timeFactor = 1.0 - Math.exp(-days / 60.0);
        double probability = baseProb * timeFactor;
        return Math.round(probability * 1000.0) / 1000.0;
    }

    private String mapHealthLevel(int score) {
        if (score >= 90) return "优秀";
        if (score >= 75) return "良好";
        if (score >= 60) return "一般";
        if (score >= 40) return "较差";
        return "危险";
    }

    private String mapRiskLevel(int score) {
        if (score >= 80) return "低风险";
        if (score >= 60) return "中等风险";
        if (score >= 40) return "较高风险";
        return "高风险";
    }

    private List<String> identifyRiskFactors(Device device, List<FaultRecord> faultHistory) {
        List<String> factors = new ArrayList<>();

        if (device.getPurchaseDate() != null) {
            long ageInDays = (System.currentTimeMillis() - device.getPurchaseDate().getTime())
                    / (1000L * 60 * 60 * 24);
            if (ageInDays > 365 * 5) {
                factors.add("设备使用年限超过5年，硬件老化风险较高");
            } else if (ageInDays > 365 * 3) {
                factors.add("设备使用年限超过3年，建议加强巡检");
            }
        }

        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
            factors.add("设备已过保修期，维修成本可能增加");
        }

        if (faultHistory.size() > 5) {
            factors.add("历史故障次数较多（" + faultHistory.size() + "次），设备可靠性下降");
        } else if (faultHistory.size() > 3) {
            factors.add("已有" + faultHistory.size() + "次故障记录，需关注设备状态");
        }

        long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        long recentFaults = faultHistory.stream()
                .filter(f -> f.getCreatedAt() != null && f.getCreatedAt().getTime() > thirtyDaysAgo)
                .count();
        if (recentFaults >= 2) {
            factors.add("近30天内发生" + recentFaults + "次故障，故障频率偏高");
        }

        if (device.getStatus() != null && device.getStatus() == 2) {
            factors.add("设备当前处于故障状态");
        }

        if (device.getStatus() != null && device.getStatus() == 0) {
            factors.add("设备当前处于离线状态，可能存在异常");
        }

        long unresolvedCount = faultHistory.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() < 3)
                .count();
        if (unresolvedCount > 0) {
            factors.add("存在" + unresolvedCount + "个未解决的故障工单");
        }

        if (factors.isEmpty()) {
            factors.add("暂未发现明显风险因素");
        }

        return factors;
    }

    private List<String> suggestPreventiveActions(Device device, List<FaultRecord> faultHistory, int healthScore) {
        List<String> actions = new ArrayList<>();

        if (healthScore < 40) {
            actions.add("建议立即对设备进行全面检测和维护");
            actions.add("考虑制定设备替换计划");
        } else if (healthScore < 60) {
            actions.add("建议在一周内安排预防性维护");
            actions.add("准备备用设备以防突发故障");
        } else if (healthScore < 75) {
            actions.add("建议在本月内进行常规保养");
        }

        if (device.getWarrantyExpiry() != null) {
            long daysUntilExpiry = (device.getWarrantyExpiry().getTime() - System.currentTimeMillis())
                    / (1000L * 60 * 60 * 24);
            if (daysUntilExpiry > 0 && daysUntilExpiry <= 90) {
                actions.add("保修将在" + daysUntilExpiry + "天后到期，建议在保修期内进行全面检查");
            }
        }

        if (faultHistory.size() > 3) {
            actions.add("建议建立该设备的专项巡检计划");
        }

        actions.add("定期清洁设备，保持良好的运行环境");
        actions.add("保持设备固件和驱动程序为最新版本");

        return actions;
    }

    private String estimateRemainingLife(Device device, int healthScore) {
        if (device.getPurchaseDate() == null) {
            return "数据不足，无法估算";
        }

        long ageInDays = (System.currentTimeMillis() - device.getPurchaseDate().getTime())
                / (1000L * 60 * 60 * 24);
        int ageYears = (int) (ageInDays / 365);

        // 假设一般设备寿命8年，根据健康分数调整
        int expectedLifeYears = 8;
        double adjustedRemaining = (expectedLifeYears - ageYears) * (healthScore / 100.0);

        if (adjustedRemaining <= 0) {
            return "设备已超出预期使用寿命，建议尽快更换";
        } else if (adjustedRemaining < 1) {
            int months = (int) Math.max(1, adjustedRemaining * 12);
            return "预计剩余使用寿命约" + months + "个月";
        } else {
            return "预计剩余使用寿命约" + String.format("%.1f", adjustedRemaining) + "年";
        }
    }

    private String recommendNextMaintenance(Device device, List<FaultRecord> faultHistory, int healthScore) {
        if (healthScore < 40) {
            return "建议立即维护";
        } else if (healthScore < 60) {
            return "建议7天内安排维护";
        } else if (healthScore < 75) {
            return "建议30天内安排维护";
        } else if (faultHistory.size() > 2) {
            return "建议60天内安排例行检查";
        } else {
            return "建议90天内安排下次例行保养";
        }
    }

    // ==================== 异常检测 ====================

    @Override
    public List<Map<String, Object>> detectAnomalies() {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        List<Device> allDevices = deviceMapper.selectAll();

        for (Device device : allDevices) {
            FaultQueryParam faultParam = new FaultQueryParam();
            faultParam.setDeviceId(device.getId());
            faultParam.setPageNum(1);
            faultParam.setPageSize(1000);
            List<FaultRecord> faults = faultMapper.selectByCondition(faultParam);

            // 频繁故障设备
            if (faults.size() > 3) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("type", "频繁故障");
                anomaly.put("deviceId", device.getId());
                anomaly.put("deviceName", device.getName());
                anomaly.put("location", device.getLocation());
                anomaly.put("faultCount", faults.size());
                anomaly.put("severity", faults.size() > 5 ? "高" : "中");
                anomaly.put("message", "设备「" + device.getName() + "」已发生" + faults.size()
                        + "次故障，远超正常水平，建议重点关注");
                anomaly.put("recommendation", "建议对设备进行全面检测，评估是否需要更换");
                anomalies.add(anomaly);
            }

            // 离线设备异常
            if (device.getStatus() != null && device.getStatus() == 0) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("type", "设备离线");
                anomaly.put("deviceId", device.getId());
                anomaly.put("deviceName", device.getName());
                anomaly.put("location", device.getLocation());
                anomaly.put("severity", "中");
                anomaly.put("message", "设备「" + device.getName() + "」当前处于离线状态，可能存在连接问题");
                anomaly.put("recommendation", "建议检查设备网络连接和电源状态");
                anomalies.add(anomaly);
            }

            // 过保设备
            if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
                long expiredDays = (System.currentTimeMillis() - device.getWarrantyExpiry().getTime())
                        / (1000L * 60 * 60 * 24);
                if (expiredDays > 180) {
                    Map<String, Object> anomaly = new LinkedHashMap<>();
                    anomaly.put("type", "保修过期");
                    anomaly.put("deviceId", device.getId());
                    anomaly.put("deviceName", device.getName());
                    anomaly.put("location", device.getLocation());
                    anomaly.put("expiredDays", expiredDays);
                    anomaly.put("severity", expiredDays > 365 ? "高" : "低");
                    anomaly.put("message", "设备「" + device.getName() + "」保修已过期"
                            + expiredDays + "天，维修成本风险增加");
                    anomaly.put("recommendation", "建议评估设备状态并考虑续保或替换计划");
                    anomalies.add(anomaly);
                }
            }

            // 故障状态设备
            if (device.getStatus() != null && device.getStatus() == 2) {
                long unresolvedCount = faults.stream()
                        .filter(f -> f.getStatus() != null && f.getStatus() < 3)
                        .count();
                if (unresolvedCount > 0) {
                    Map<String, Object> anomaly = new LinkedHashMap<>();
                    anomaly.put("type", "持续故障");
                    anomaly.put("deviceId", device.getId());
                    anomaly.put("deviceName", device.getName());
                    anomaly.put("location", device.getLocation());
                    anomaly.put("unresolvedFaults", unresolvedCount);
                    anomaly.put("severity", "高");
                    anomaly.put("message", "设备「" + device.getName() + "」处于故障状态且有"
                            + unresolvedCount + "个未解决工单");
                    anomaly.put("recommendation", "建议优先处理该设备的故障工单");
                    anomalies.add(anomaly);
                }
            }
        }

        // 按严重度排序：高 > 中 > 低
        anomalies.sort((a, b) -> {
            Map<String, Integer> severityOrder = new HashMap<>();
            severityOrder.put("高", 3);
            severityOrder.put("中", 2);
            severityOrder.put("低", 1);
            int sa = severityOrder.getOrDefault(a.get("severity"), 0);
            int sb = severityOrder.getOrDefault(b.get("severity"), 0);
            return Integer.compare(sb, sa);
        });

        return anomalies;
    }

    // ==================== 设备健康评分详情 ====================

    @Override
    public Map<String, Object> getDeviceHealthScore(Long deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(404, "设备不存在: " + deviceId);
        }

        FaultQueryParam faultParam = new FaultQueryParam();
        faultParam.setDeviceId(deviceId);
        faultParam.setPageNum(1);
        faultParam.setPageSize(1000);
        List<FaultRecord> faultHistory = faultMapper.selectByCondition(faultParam);

        int healthScore = calculateHealthScore(device, faultHistory);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("deviceId", device.getId());
        result.put("deviceName", device.getName());
        result.put("healthScore", healthScore);
        result.put("healthLevel", mapHealthLevel(healthScore));
        result.put("riskLevel", mapRiskLevel(healthScore));

        // 评分细节
        Map<String, Object> breakdown = new LinkedHashMap<>();

        int ageScore = 100;
        if (device.getPurchaseDate() != null) {
            long ageInDays = (System.currentTimeMillis() - device.getPurchaseDate().getTime())
                    / (1000L * 60 * 60 * 24);
            int ageYears = (int) (ageInDays / 365);
            ageScore = Math.max(0, 100 - ageYears * 15);
        }
        breakdown.put("设备年限评分", ageScore);

        int warrantyScore = 100;
        if (device.getWarrantyExpiry() != null && device.getWarrantyExpiry().before(new Date())) {
            long expiredDays = (System.currentTimeMillis() - device.getWarrantyExpiry().getTime())
                    / (1000L * 60 * 60 * 24);
            warrantyScore = expiredDays > 365 ? 30 : 60;
        }
        breakdown.put("保修状态评分", warrantyScore);

        int faultScore = Math.max(0, 100 - faultHistory.size() * 15);
        breakdown.put("故障历史评分", faultScore);

        int statusScore = 100;
        if (device.getStatus() != null) {
            if (device.getStatus() == 0) statusScore = 60;
            else if (device.getStatus() == 2) statusScore = 20;
        }
        breakdown.put("当前状态评分", statusScore);

        result.put("scoreBreakdown", breakdown);
        result.put("totalFaults", faultHistory.size());

        long unresolvedCount = faultHistory.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() < 3)
                .count();
        result.put("unresolvedFaults", unresolvedCount);
        result.put("recommendation", recommendNextMaintenance(device, faultHistory, healthScore));

        return result;
    }

    // ==================== 维修人员推荐 ====================

    @Override
    public List<Map<String, Object>> recommendMaintainer(Long faultId) {
        FaultRecord fault = faultMapper.selectById(faultId);
        if (fault == null) {
            throw new BusinessException(404, "故障记录不存在: " + faultId);
        }

        Device device = deviceMapper.selectById(fault.getDeviceId());
        Long targetDeviceTypeId = device != null ? device.getDeviceTypeId() : null;

        // 获取所有用户
        List<User> allUsers = userMapper.selectAll();

        // 获取所有故障记录用于统计
        FaultQueryParam allFaultsParam = new FaultQueryParam();
        allFaultsParam.setPageNum(1);
        allFaultsParam.setPageSize(10000);
        List<FaultRecord> allFaults = faultMapper.selectByCondition(allFaultsParam);

        // 统计每个维修人员的处理记录
        Map<Long, List<FaultRecord>> maintainerFaults = allFaults.stream()
                .filter(f -> f.getAssigneeId() != null)
                .collect(Collectors.groupingBy(FaultRecord::getAssigneeId));

        // 预加载同类设备ID集合，避免在循环中重复查询
        Set<Long> sameTypeDeviceIds = Collections.emptySet();
        if (targetDeviceTypeId != null) {
            sameTypeDeviceIds = deviceMapper.selectAll().stream()
                    .filter(d -> targetDeviceTypeId.equals(d.getDeviceTypeId()))
                    .map(Device::getId)
                    .collect(Collectors.toSet());
        }

        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (Map.Entry<Long, List<FaultRecord>> entry : maintainerFaults.entrySet()) {
            Long userId = entry.getKey();
            List<FaultRecord> handled = entry.getValue();

            User user = allUsers.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst().orElse(null);
            if (user == null || (user.getEnabled() != null && !user.getEnabled())) {
                continue;
            }

            // 计算该维修人员处理同类设备的数量
            long sameTypeCount = 0;
            if (targetDeviceTypeId != null) {
                Set<Long> finalSameTypeDeviceIds = sameTypeDeviceIds;
                sameTypeCount = handled.stream()
                        .filter(f -> finalSameTypeDeviceIds.contains(f.getDeviceId()))
                        .count();
            }

            // 计算已解决的故障数量
            long resolvedCount = handled.stream()
                    .filter(f -> f.getStatus() != null && f.getStatus() >= 3)
                    .count();

            // 计算解决率
            double resolveRate = handled.isEmpty() ? 0.0 : (double) resolvedCount / handled.size();

            // 计算当前处理中的工单数量
            long activeCount = handled.stream()
                    .filter(f -> f.getStatus() != null && f.getStatus() < 3)
                    .count();

            // 综合评分
            double score = 0;
            score += sameTypeCount * 20;      // 同类设备经验加分
            score += resolvedCount * 5;        // 解决数量加分
            score += resolveRate * 30;         // 解决率加分
            score -= activeCount * 10;         // 当前工作量减分

            Map<String, Object> rec = new LinkedHashMap<>();
            rec.put("userId", userId);
            rec.put("username", user.getUsername());
            rec.put("realName", user.getRealName());
            rec.put("phone", user.getPhone());
            rec.put("totalHandled", handled.size());
            rec.put("resolvedCount", resolvedCount);
            rec.put("resolveRate", String.format("%.1f%%", resolveRate * 100));
            rec.put("sameTypeExperience", sameTypeCount);
            rec.put("currentWorkload", activeCount);
            rec.put("recommendScore", Math.round(score * 10.0) / 10.0);

            String reason;
            if (sameTypeCount > 0 && resolveRate > 0.8) {
                reason = "具有同类设备维修经验且解决率较高，强烈推荐";
            } else if (sameTypeCount > 0) {
                reason = "具有同类设备维修经验，推荐";
            } else if (resolveRate > 0.8) {
                reason = "整体故障解决率较高，推荐";
            } else if (activeCount == 0) {
                reason = "当前无在处理工单，可立即响应";
            } else {
                reason = "有一定维修经验，可作为备选";
            }
            rec.put("recommendReason", reason);

            recommendations.add(rec);
        }

        // 按推荐评分排序
        recommendations.sort((a, b) -> Double.compare(
                ((Number) b.get("recommendScore")).doubleValue(),
                ((Number) a.get("recommendScore")).doubleValue()
        ));

        // 返回前5个推荐
        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    // ==================== 文本分析 ====================

    @Override
    public Map<String, Object> analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new BusinessException("分析文本不能为空");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("originalText", text);

        // 关键词提取
        List<String> keywords = extractKeywords(text);
        result.put("keywords", keywords);

        // 故障类型分类
        String faultType = classifyFaultType(text);
        result.put("faultType", faultType);

        // 严重程度评估
        Map<String, Object> severityAssessment = assessSeverity(text);
        result.put("severity", severityAssessment);

        // 情感分析（紧急程度）
        String sentimentLabel = analyzeSentiment(text);
        result.put("urgency", sentimentLabel);

        // 实体提取
        Map<String, List<String>> entities = extractEntities(text);
        result.put("entities", entities);

        // 建议的处理优先级
        int priority = determinePriority(text);
        result.put("suggestedPriority", priority);
        result.put("priorityLabel", priority == 1 ? "低优先级" : priority == 2 ? "中优先级" : "高优先级");

        return result;
    }

    private List<String> extractKeywords(String text) {
        List<String> allKeywords = Arrays.asList(
                "故障", "损坏", "异常", "无法", "失败", "报错", "黑屏", "蓝屏",
                "卡纸", "断网", "过热", "噪音", "漏水", "短路", "死机",
                "打印机", "投影仪", "空调", "电脑", "服务器", "路由器", "交换机",
                "门禁", "监控", "摄像头", "电源", "网络", "硬盘", "内存", "显卡",
                "紧急", "严重", "立即", "无法使用", "影响教学", "影响办公"
        );
        return allKeywords.stream()
                .filter(text::contains)
                .collect(Collectors.toList());
    }

    private String classifyFaultType(String text) {
        if (containsAny(text, "电源", "开机", "通电", "供电")) return "电源故障";
        if (containsAny(text, "网络", "断网", "WiFi", "wifi", "上网", "IP")) return "网络故障";
        if (containsAny(text, "黑屏", "显示", "花屏", "屏幕")) return "显示故障";
        if (containsAny(text, "打印", "卡纸", "墨", "碳粉")) return "打印机故障";
        if (containsAny(text, "投影", "灯泡", "亮度")) return "投影设备故障";
        if (containsAny(text, "门禁", "刷卡", "指纹", "人脸")) return "门禁系统故障";
        if (containsAny(text, "监控", "摄像", "录像")) return "监控设备故障";
        if (containsAny(text, "空调", "制冷", "制热", "温度")) return "空调系统故障";
        if (containsAny(text, "噪音", "异响", "震动")) return "机械故障";
        if (containsAny(text, "蓝屏", "死机", "系统", "软件", "崩溃")) return "系统软件故障";
        if (containsAny(text, "过热", "散热", "发烫")) return "散热故障";
        return "其他故障";
    }

    private Map<String, Object> assessSeverity(String text) {
        Map<String, Object> severity = new LinkedHashMap<>();
        int level;
        String label;

        if (containsAny(text, "紧急", "严重", "立即", "瘫痪", "全部", "无法使用", "影响教学", "安全隐患", "火灾", "漏电")) {
            level = 3;
            label = "高";
        } else if (containsAny(text, "较多", "频繁", "多次", "反复", "部分功能", "不稳定", "偶尔")) {
            level = 2;
            label = "中";
        } else {
            level = 1;
            label = "低";
        }

        severity.put("level", level);
        severity.put("label", label);
        severity.put("description", level == 3 ? "故障影响范围较大，需立即处理"
                : level == 2 ? "故障影响正常使用，需尽快处理"
                : "故障影响较小，可按计划处理");

        return severity;
    }

    private String analyzeSentiment(String text) {
        if (containsAny(text, "紧急", "立即", "马上", "尽快", "着急", "急需", "瘫痪", "严重影响")) {
            return "非常紧急";
        } else if (containsAny(text, "尽快", "需要", "无法", "影响", "不能")) {
            return "较为紧急";
        } else {
            return "一般";
        }
    }

    private Map<String, List<String>> extractEntities(String text) {
        Map<String, List<String>> entities = new LinkedHashMap<>();

        List<String> deviceTypes = new ArrayList<>();
        String[] deviceKeywords = {"电脑", "打印机", "投影仪", "空调", "服务器", "路由器",
                "交换机", "门禁", "摄像头", "监控", "显示器", "扫描仪"};
        for (String keyword : deviceKeywords) {
            if (text.contains(keyword)) deviceTypes.add(keyword);
        }
        if (!deviceTypes.isEmpty()) entities.put("设备类型", deviceTypes);

        List<String> locations = new ArrayList<>();
        String[] locationKeywords = {"教室", "办公室", "实验室", "图书馆", "机房", "会议室",
                "大厅", "走廊", "食堂", "宿舍", "体育馆"};
        for (String keyword : locationKeywords) {
            if (text.contains(keyword)) locations.add(keyword);
        }
        if (!locations.isEmpty()) entities.put("位置", locations);

        List<String> symptoms = new ArrayList<>();
        String[] symptomKeywords = {"黑屏", "蓝屏", "卡纸", "断网", "过热", "噪音",
                "死机", "漏水", "闪烁", "无响应"};
        for (String keyword : symptomKeywords) {
            if (text.contains(keyword)) symptoms.add(keyword);
        }
        if (!symptoms.isEmpty()) entities.put("故障症状", symptoms);

        return entities;
    }

    private int determinePriority(String text) {
        if (containsAny(text, "紧急", "严重", "立即", "瘫痪", "安全", "火灾", "漏电",
                "影响教学", "无法使用")) {
            return 3;
        } else if (containsAny(text, "频繁", "多次", "不稳定", "影响", "无法")) {
            return 2;
        }
        return 1;
    }

    // ==================== 工具方法 ====================

    private boolean containsAny(String text, String... keywords) {
        if (text == null) return false;
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}
