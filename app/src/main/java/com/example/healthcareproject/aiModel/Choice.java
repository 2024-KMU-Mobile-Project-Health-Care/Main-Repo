package com.example.healthcareproject.aiModel;

public class Choice {
    private AiMessage message;

    public Choice(AiMessage message) { this.message = message; }

    public AiMessage getMessage() { return message; }

    public void setMessage(AiMessage message) { this.message = message; }
}