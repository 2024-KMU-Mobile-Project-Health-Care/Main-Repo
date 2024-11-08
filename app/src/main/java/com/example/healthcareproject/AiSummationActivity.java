package com.example.healthcareproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthcareproject.aiModel.AiMessage;
import com.example.healthcareproject.aiModel.AiRequest;
import com.example.healthcareproject.aiModel.AiResponse;
import com.example.healthcareproject.aiModel.GPTApiService;
import com.example.healthcareproject.aiModel.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiSummationActivity extends AppCompatActivity {
    Button btnAiProcess;
    EditText etAiInput;
    TextView tvAiResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_summation);

        btnAiProcess = (Button) findViewById(R.id.btn_ai_process);
        etAiInput = (EditText) findViewById(R.id.et_ai_input);
        tvAiResult = (TextView) findViewById(R.id.tv_ai_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAiProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etAiInput.getText().toString();
                if(!inputText.isEmpty()){
                    sendValueToAi(inputText);
                }
                else{
                    tvAiResult.setText("값을 입력하세요.");
                }
            }
        });
    }

    void sendValueToAi(String value){
        String systemContent = "당신은 전문적인 의료 어시스턴트입니다. " +
                "환자의 상태 정보를 읽고, 각 통증 위치별로 주요 증상과 필요한 임상 정보를 중심으로 간결하게 요약해 주세요. " +
                "이 요약은 의사가 빠르게 파악하고 진료에 활용할 수 있도록 작성하고, 각각의 통증 위치는 개별 단락으로 분류해 주세요. " +
                "각 통증 위치별로 다음 형식을 따라 요약해 주세요:\n" +
                "1. 통증 위치 (예: '오른쪽 어깨')\n" +
                "2. 주요 증상 (예: '통증 강도: 중간 ~ 강 (악화 혹은 완화)', '통증 종류: 욱신거림')\n" +
                "3. 증상 발생 시각 (예: '2024.11.1 ~ 2024.11.5')\n" +
                "4. 기타 증상 및 정보 (예: '부기 (2024.11.3), 발열 (2024.11.5)')\n" +
                "입력된 내용 외에 새로운 통증 위치나 증상을 절대 생성하지 마세요. " +
                "스스로 판단한 의학적 소견은 절대 작성하지 마세요. " +
                "Markdown 문법은 사용하지 마세요.";

        AiMessage systemMessage = new AiMessage("system", systemContent);
        AiMessage userMessage = new AiMessage("user", value);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        AiRequest request = new AiRequest(
                "gpt-4o-mini-2024-07-18",
                messages,
                16000,
                0
        );

        // API 호출
        GPTApiService apiService = RetrofitClient.getApiService();
        Call<AiResponse> call = apiService.getCompletion(request);

        call.enqueue(new Callback<AiResponse>() {
            @Override
            public void onResponse(Call<AiResponse> call, Response<AiResponse> response) {
                if (response.isSuccessful()) {
                    AiResponse aiResponse = response.body();
                    if (aiResponse != null && aiResponse.getChoices() != null && !aiResponse.getChoices().isEmpty()) {
                        String result = aiResponse.getChoices().get(0).getMessage().getContent().trim();
                        tvAiResult.setText(result);
                    } else {
                        tvAiResult.setText("응답이 비어 있습니다.");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "응답 오류: " + errorBody);
                        tvAiResult.setText("응답 오류: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                        tvAiResult.setText("응답 오류가 발생했지만 상세 내용을 읽을 수 없습니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<AiResponse> call, Throwable t) {
                tvAiResult.setText("네트워크 오류: " + t.getMessage());
            }
        });
    }
}