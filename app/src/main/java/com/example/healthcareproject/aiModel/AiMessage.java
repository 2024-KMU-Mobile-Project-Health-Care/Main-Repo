package com.example.healthcareproject.aiModel;

public class AiMessage {
    private String role;
    private String content;

    public AiMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role;}

    public String getContent() { return content; }

    public void setRole(String role) { this.role = role; }

    public void setContent(String content) { this.content = content; }
}

