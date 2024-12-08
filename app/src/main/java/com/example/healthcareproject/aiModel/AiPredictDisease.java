package com.example.healthcareproject.aiModel;

import android.util.Log;
import android.widget.Toast;

import com.example.healthcareproject.painInput.Eng2Kor;

import java.util.List;
import java.util.Map;

public class AiPredictDisease {
    public static void makePrediction(List<Map<String, String>> allPainInfo, AiCallback<String> callback) {
        Map<String, String> painInfo;
        String recentTime = null;
        StringBuilder recentPainInfo = new StringBuilder();
        for (int i = allPainInfo.size() - 1; i >= 0; i--) {
            painInfo = allPainInfo.get(i);
            String location = Eng2Kor.getKor(painInfo.get("painLocation"));
            String timestamp = painInfo.get("painStartTime");
            String painType = painInfo.get("painType");
            String painIntensity = painInfo.get("painIntensity");

            if(recentTime == null){
                recentTime = timestamp;
                recentPainInfo.append("PainStartTime : " + timestamp + " / ");
            }
            if(recentTime.equals(timestamp)){
                recentPainInfo.append("Location:" + location  +", Pain Type: " + painType + ", Pain Intensity: " + painIntensity + " & ");
            }
            else{
                break;
            }

            /*
            Log.d("PainDatabase", "Location: " + location
                    + ", Timestamp: " + timestamp
                    + ", Pain Type: " + painType
                    + ", Pain Intensity: " + painIntensity
                    );

             */
        }
        StringBuilder predictedDiseaseString = new StringBuilder();
        //Log.d("TESTTTEEST", recentPainInfo.toString());
        AiProcess aiProcess = new AiProcess();
        aiProcess.diseasePredictAi(recentPainInfo.toString(), new AiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
