package com.example.healthcareproject.aiModel;

import java.util.List;

public class AiRequest {
    private String model;
    private List<AiMessage> messages;
    private int max_tokens;
    private double temperature;

    public AiRequest(String model, List<AiMessage> messages, int max_tokens, double temperature) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
    }

    public String getModel() { return model; }

    public List<AiMessage> getMessages() { return messages; }

    public int getMaxTokens() { return max_tokens; }

    public double getTemperature() { return temperature; }


    public void setModel(String model) { this.model = model; }

    public void setMessages(List<AiMessage> messages) { this.messages = messages; }

    public void setMaxTokens(int max_tokens) { this.max_tokens = max_tokens; }

    public void setTemperature(double temperature) { this.temperature = temperature; }
}