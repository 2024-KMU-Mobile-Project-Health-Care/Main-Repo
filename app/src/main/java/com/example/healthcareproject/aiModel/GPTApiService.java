package com.example.healthcareproject.aiModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.example.healthcareproject.BuildConfig;

public interface GPTApiService {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + BuildConfig.OPENAI_API_KEY,
            "OpenAI-Organization: " + BuildConfig.OPENAI_ORGANIZATION
    })
    @POST("v1/chat/completions")
    Call<AiResponse> getCompletion(@Body AiRequest request);
}