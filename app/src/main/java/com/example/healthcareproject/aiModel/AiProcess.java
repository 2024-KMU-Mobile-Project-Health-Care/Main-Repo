package com.example.healthcareproject.aiModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AiProcess {

    // 증상을 읽고 요약해주는 메소드
    // 메인 쓰레드에서 호출 할 수 없음!!
    public String textSummationAi(String value){
        String systemContent = "당신은 전문적인 의료 어시스턴트입니다. " +
                "환자의 상태 정보를 읽고, 주요 증상과 필요한 임상 정보를 중심으로 간결하게 요약해 주세요. " +
                "다음 형식을 따라 요약해 주세요:\n" +
                "1. 통증 위치 (예: '오른쪽 어깨')\n" +
                "2. 통증 강도: (0-통증 없음, 1-약함, 2-중간, 3-강함 4-매우 강함, 예: 중간 ~ 강 (악화 혹은 완화))" +
                "3. 통증 종류: (예: 욱신거림)\n" +
                "4. 증상 발생 시각 (예: '2024.11.1 ~ 2024.11.5')\n" +
                "5. 기타 증상(예: '부기 (2024.11.3), 발열 (2024.11.5)')\n" +
                "입력된 내용 외에 새로운 통증 위치나 증상을 절대 생성하지 마세요. " +
                "스스로 판단한 의학적 소견은 절대 작성하지 마세요. " +
                "한국어를 사용하고, Markdown 문법은 사용하지 마세요.";

        try {
            return callAi(systemContent, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Ai를 직접적으로 호출하는 메소드. 타 메소드에서 사용. 직접 호출 금지! 메소드 이용
    private String callAi(String systemContent, String userContent) throws Exception {
        AiMessage systemMessage = new AiMessage("system", systemContent);
        AiMessage userMessage = new AiMessage("user", userContent);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        AiRequest request = new AiRequest(
                "gpt-4o-mini-2024-07-18",
                messages,
                16000,
                0
        );

        GPTApiService apiService = RetrofitClient.getApiService();
        Call<AiResponse> call = apiService.getCompletion(request);

        // 동기 호출 실행
        Response<AiResponse> response = call.execute();

        if (response.isSuccessful()) {
            AiResponse aiResponse = response.body();
            if (aiResponse != null && aiResponse.getChoices() != null && !aiResponse.getChoices().isEmpty()) {
                return aiResponse.getChoices().get(0).getMessage().getContent().trim();
            } else {
                throw new Exception("응답이 비어 있습니다.");
            }
        } else {
            String errorBody = response.errorBody().string();
            throw new Exception("응답 오류: " + errorBody);
        }
    }
}
