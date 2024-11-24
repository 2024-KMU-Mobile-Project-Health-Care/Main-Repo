package com.example.healthcareproject.painInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessPainData {
    private static final Map<String, int[]> regions = new HashMap<String, int[]>() {{
        put("Cervical vertebrae", new int[]{270, 30, 330, 100});
        put("Thoracic vertebrae", new int[]{270, 100, 330, 350});
        put("Lumbar vertebrae", new int[]{270, 350, 330, 500});
        put("Sacrum", new int[]{270, 500, 330, 580});
        put("Coccyx", new int[]{270, 580, 330, 600});
        put("Scapula Right", new int[]{170, 80, 270, 230});
        put("Scapula Left", new int[]{330, 80, 430, 230});
        put("Shoulder Right", new int[]{100, 75, 170, 155});
        put("Shoulder Left", new int[]{430, 75, 500, 155});
        put("Latissimus dorsi", new int[]{150, 280, 450, 500});
        put("Trapezius", new int[]{140, 30, 460, 100});
        put("Rhomboids", new int[]{265, 100, 335, 200});
        put("Erector spinae", new int[]{290, 0, 310, 550});
        put("Levator scapulae Right", new int[]{240, 0, 270, 70});
        put("Levator scapulae Left", new int[]{330, 0, 360, 70});
        put("Teres minor Right", new int[]{210, 150, 265, 220});
        put("Teres minor Left", new int[]{335, 150, 390, 220});
        put("Infraspinatus Right", new int[]{100, 130, 210, 190});
        put("Infraspinatus Left", new int[]{390, 130, 500, 190});
    }};

    public static List<Map<String, String>> processPainData(List<PainInfo> painInfoList, String timeStamp) {
        // 중복 제거를 위해 Set 사용
        Set<String> uniquePainDataSet = new HashSet<>();
        List<Map<String, String>> painDataList = new ArrayList<>();

        for (PainInfo painInfo : painInfoList) {
            String painType = painInfo.getPainType();
            List<float[]> coordinates = painInfo.getCoordinates();

            for (float[] coordinate : coordinates) {
                String closestRegion = findClosestRegion(coordinate[0], coordinate[1]);

                String uniqueKey = closestRegion + "|" + painType;

                if (uniquePainDataSet.add(uniqueKey)) {
                    Map<String, String> painData = new HashMap<>();
                    painData.put("painLocation", closestRegion);
                    painData.put("painStartTime", timeStamp);
                    painData.put("painType", painType);

                    painDataList.add(painData);
                }
            }
        }

        return painDataList;
    }

    private static String findClosestRegion(float x, float y) {
        String closestRegion = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, int[]> entry : regions.entrySet()) {
            String regionName = entry.getKey();
            int[] bounds = entry.getValue();

            if (x >= bounds[0] && x <= bounds[2] && y >= bounds[1] && y <= bounds[3]) {
                return regionName;
            }
            double distance = calculateDistance(x, y, bounds);
            if (distance < minDistance) {
                minDistance = distance;
                closestRegion = regionName;
            }
        }
        return closestRegion;
    }

    // 영역의 중심과 좌표 간의 거리 계산
    private static double calculateDistance(float x, float y, int[] bounds) {
        float centerX = (bounds[0] + bounds[2]) / 2f;
        float centerY = (bounds[1] + bounds[3]) / 2f;
        return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
    }
}