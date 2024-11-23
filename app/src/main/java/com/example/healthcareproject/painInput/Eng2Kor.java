package com.example.healthcareproject.painInput;

import java.util.HashMap;
import java.util.Map;

public class Eng2Kor {
    private static final Map<String, String> engToKorMap = new HashMap<>();

    static {
        engToKorMap.put("Cervical vertebrae", "경추");
        engToKorMap.put("Thoracic vertebrae", "흉추");
        engToKorMap.put("Lumbar vertebrae", "요추");
        engToKorMap.put("Sacrum", "천추");
        engToKorMap.put("Coccyx", "미추");
        engToKorMap.put("Scapula Left", "왼쪽 견갑골");
        engToKorMap.put("Scapula Right", "오른쪽 견갑골");
        engToKorMap.put("Shoulder Left", "왼쪽 어깨");
        engToKorMap.put("Shoulder Right", "오른쪽 어깨");
        engToKorMap.put("Latissimus dorsi", "광배근");
        engToKorMap.put("Trapezius", "승모근");
        engToKorMap.put("Rhomboids", "능형근");
        engToKorMap.put("Erector spinae", "척추기립근");
        engToKorMap.put("Levator scapulae Left", "왼쪽 견갑거근");
        engToKorMap.put("Levator scapulae Right", "오른쪽 견갑거근");
        engToKorMap.put("Teres minor Left", "왼쪽 소원근");
        engToKorMap.put("Teres minor Right", "오른쪽 소원근");
        engToKorMap.put("Infraspinatus Left", "왼쪽 극하근");
        engToKorMap.put("Infraspinatus Right", "오른쪽 극하근");
    }

    public static String getKor(String englishKey) {
        return engToKorMap.getOrDefault(englishKey, "Unknown Key");
    }
}
