package com.example.healthcareproject.aiModel;

public interface AiCallback<T> {
    void onSuccess(T result); // 성공적으로 결과를 받을 때
    void onFailure(Exception e); // 실패 시
}
