package com.example.healthcareproject.aiModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GPTApiService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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",
            "OpenAI-Organization: org-FK382Ct9cS3oLJNoJQvBrSgu"
    })
    @POST("v1/chat/completions")
    Call<AiResponse> getCompletion(@Body AiRequest request);
}