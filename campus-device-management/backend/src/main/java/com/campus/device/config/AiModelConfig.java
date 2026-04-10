package com.campus.device.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiModelConfig {

    private Model model = new Model();
    private Prediction prediction = new Prediction();

    @Data
    public static class Model {
        private String path = "./models/";
        private String faultDiagnosis = "fault_diagnosis_model.json";
        private String anomalyDetection = "anomaly_detection_model.json";
    }

    @Data
    public static class Prediction {
        private double threshold = 0.7;
        private int windowDays = 30;
    }
}
