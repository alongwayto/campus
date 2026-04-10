package com.campus.device.util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class DataAnalysisUtil {

    private DataAnalysisUtil() {}

    /**
     * 计算描述性统计信息
     */
    public static Map<String, Double> descriptiveStats(List<Double> data) {
        Map<String, Double> stats = new LinkedHashMap<>();
        if (data == null || data.isEmpty()) return stats;

        DescriptiveStatistics ds = new DescriptiveStatistics();
        data.forEach(ds::addValue);

        stats.put("mean", ds.getMean());
        stats.put("median", ds.getPercentile(50));
        stats.put("stdDev", ds.getStandardDeviation());
        stats.put("min", ds.getMin());
        stats.put("max", ds.getMax());
        stats.put("count", (double) ds.getN());
        return stats;
    }

    /**
     * 线性回归预测
     */
    public static double linearRegressionPredict(List<Double> xValues, List<Double> yValues, double xPredict) {
        if (xValues == null || yValues == null || xValues.size() != yValues.size() || xValues.isEmpty()) {
            return 0.0;
        }
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < xValues.size(); i++) {
            regression.addData(xValues.get(i), yValues.get(i));
        }
        return regression.predict(xPredict);
    }

    /**
     * 计算故障率
     */
    public static double calculateFaultRate(long totalDevices, long faultDevices) {
        if (totalDevices == 0) return 0.0;
        return (double) faultDevices / totalDevices;
    }

    /**
     * 计算平均修复时间（小时）
     */
    public static double calculateMTTR(List<Long> repairTimeMinutes) {
        if (repairTimeMinutes == null || repairTimeMinutes.isEmpty()) return 0.0;
        return repairTimeMinutes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0) / 60.0;
    }
}
