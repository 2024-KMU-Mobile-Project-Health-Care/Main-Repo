package com.example.healthcareproject.painInput;

import java.util.ArrayList;
import java.util.List;

public class PainInfo {
    private String painType;
    private List<float[]> coordinates;

    public PainInfo(String painType) {
        this.painType = painType;
        this.coordinates = new ArrayList<>();
    }

    public String getPainType() {
        return painType;
    }

    public List<float[]> getCoordinates() {
        return coordinates;
    }

    public void addCoordinate(float x, float y) {
        coordinates.add(new float[]{x, y});
    }
}
