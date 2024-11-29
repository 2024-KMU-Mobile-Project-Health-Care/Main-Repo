package com.example.healthcareproject.aiModel;

import android.util.Log;    
import java.util.List;
import java.util.Map;

public class AiPredictDisease {
    public static String makePrediction(List<Map<String, String>> allPainInfo) {
        for (Map<String, String> painInfo : allPainInfo) {
            String location = painInfo.get("painLocation");
            String timestamp = painInfo.get("painStartTime");
            String painType = painInfo.get("painType");
            String painIntensity = painInfo.get("painIntensity");
            //String predictedDisease = painInfo.get("predictedDisease"); 무조건 null값임

            Log.d("PainDatabase", "Location: " + location
                    + ", Timestamp: " + timestamp
                    + ", Pain Type: " + painType
                    + ", Pain Intensity: " + painIntensity
                    );
        }

        //예측하는 부분 작성

        String predictedDiseaseString = "척추측만증";
        return predictedDiseaseString;
    }
}
