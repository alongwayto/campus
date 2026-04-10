package com.campus.device.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AiModelUtil {

    private AiModelUtil() {}

    /**
     * 规则引擎预测故障概率
     */
    public static double predictFailureProbability(Map<String, Object> features) {
        double probability = 0.0;

        Object faultCount = features.get("faultCount");
        if (faultCount instanceof Number) {
            int count = ((Number) faultCount).intValue();
            probability += Math.min(0.5, count * 0.1);
        }

        Object age = features.get("deviceAgeYears");
        if (age instanceof Number) {
            double years = ((Number) age).doubleValue();
            probability += Math.min(0.3, years * 0.05);
        }

        return Math.min(0.95, probability);
    }

    /**
     * 计算特征重要性（模拟）
     */
    public static Map<String, Double> calculateFeatureImportance() {
        Map<String, Double> importance = new HashMap<>();
        importance.put("故障历史频率", 0.35);
        importance.put("设备使用年限", 0.25);
        importance.put("维护记录", 0.20);
        importance.put("运行状态", 0.15);
        importance.put("环境因素", 0.05);
        return importance;
    }

    /**
     * 文本相似度计算（简单实现）
     */
    public static double calculateTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        String[] words1 = text1.toLowerCase().split("\\s+");
        String[] words2 = text2.toLowerCase().split("\\s+");

        Set<String> wordSet2 = new HashSet<>(Arrays.asList(words2));
        int common = 0;
        for (String w1 : words1) {
            if (wordSet2.contains(w1)) {
                common++;
            }
        }

        int total = Math.max(words1.length, words2.length);
        return total == 0 ? 0.0 : (double) common / total;
    }
}
