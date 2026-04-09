package com.campus.device.service.impl;

import com.campus.device.dao.DeviceMapper;
import com.campus.device.dao.FaultMapper;
import com.campus.device.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final DeviceMapper deviceMapper;
    private final FaultMapper faultMapper;

    @Override
    public List<Map<String, Object>> deviceUsageStats() {
        List<Map<String, Object>> raw = deviceMapper.countByStatus();
        // Ensure all statuses are represented
        Map<Integer, Long> statusMap = new HashMap<>();
        statusMap.put(0, 0L);
        statusMap.put(1, 0L);
        statusMap.put(2, 0L);
        for (Map<String, Object> row : raw) {
            Object statusObj = row.get("status");
            Object countObj = row.get("count");
            if (statusObj != null && countObj != null) {
                int status = ((Number) statusObj).intValue();
                long count = ((Number) countObj).longValue();
                statusMap.put(status, count);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        String[] labels = {"offline", "online", "fault"};
        for (int i = 0; i <= 2; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", i);
            item.put("label", labels[i]);
            item.put("count", statusMap.getOrDefault(i, 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> faultTypeStats() {
        List<Map<String, Object>> raw = faultMapper.countBySeverity();
        Map<Integer, Long> severityMap = new HashMap<>();
        severityMap.put(1, 0L);
        severityMap.put(2, 0L);
        severityMap.put(3, 0L);
        for (Map<String, Object> row : raw) {
            Object sevObj = row.get("severity");
            Object countObj = row.get("count");
            if (sevObj != null && countObj != null) {
                int severity = ((Number) sevObj).intValue();
                long count = ((Number) countObj).longValue();
                severityMap.put(severity, count);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        String[] labels = {"", "low", "medium", "high"};
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("severity", i);
            item.put("label", labels[i]);
            item.put("count", severityMap.getOrDefault(i, 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> maintenanceCostMock() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        Random random = new Random(42);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", month.format(formatter));
            item.put("cost", 2000 + random.nextInt(8000));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> deviceTrend() {
        return deviceMapper.countByMonthRegistered(12);
    }

    @Override
    public List<Map<String, Object>> faultTrend() {
        return faultMapper.countByMonth(6);
    }
}
