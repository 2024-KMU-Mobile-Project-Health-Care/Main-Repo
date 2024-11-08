package com.example.healthcareproject.aiModel;

import java.util.List;

public class AiResponse {
    private List<Choice> choices;

    public AiResponse(List<Choice> choices) {
        this.choices = choices;
    }

    public List<Choice> getChoices() { return choices; }

    public void setChoices(List<Choice> choices) { this.choices = choices; }
}
