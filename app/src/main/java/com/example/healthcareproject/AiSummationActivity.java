package com.example.healthcareproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.healthcareproject.painInput.PainDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiSummationActivity extends AppCompatActivity {
    Button btnAiProcess;
    EditText etAiInput;
    TextView tvAiResult;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_summation);

        PainDatabaseHelper painDBHelper = new PainDatabaseHelper(getApplicationContext());

        btnAiProcess = (Button) findViewById(R.id.btn_ai_process);
        etAiInput = (EditText) findViewById(R.id.et_ai_input);
        tvAiResult = (TextView) findViewById(R.id.tv_ai_result);

        handler = new Handler(Looper.getMainLooper()) { // AI 결과를 받아오는 메세지 핸들러
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) { // AI 통신 성공 시
                    Map<String,String> resultMap = (Map<String, String>) msg.obj;
                    tvAiResult.setText(resultMap.toString());
                } else if (msg.what == 0) { // 에러 발생 시
                    String errorMessage = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAiProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Map<String, String>> allPainInfo = painDBHelper.getAllPainInfo();
                Toast.makeText(getApplicationContext(), "답변을 생성하는 중입니다. 잠시 기다려 주세요.", Toast.LENGTH_LONG).show();
                if(!allPainInfo.isEmpty()){
                    unifiedAiCall(allPainInfo);
                }
                else{
                    Toast.makeText(getApplicationContext(), "저장된 증상이 없습니다. 증상을 먼저 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // 증상 정보를 추출해 String화 시키는 메소드
    Map<String, String> choicePainInfo(List<Map<String, String>> allPainInfo, Set<String> painSet){
        Map<String, String> painList = new HashMap<>();
        boolean firstValue = true;
        for(String painPoint:painSet){
            StringBuilder result = new StringBuilder();
            for(Map<String, String> painInfo:allPainInfo){
                String temp = painInfo.get("painLocation");
                if(temp != null && temp.equals(painPoint)){
                    if(firstValue){
                        firstValue = false;
                        result.append("[");
                    }
                    else{
                        result.append("&");
                    }
                    result.append(painInfo.get("painStartTime")).append(", ").append(painInfo.get("painType"));
                }
            }
            result.append("]");
            painList.put(painPoint, result.toString());
            firstValue = true;
        }
        return painList;
    }

    // 증상 발생 위치만 정리하는 메소드
    Set<String> getAllPainSet(List<Map<String, String>> allPainInfo) {
        Set<String> painSet = new LinkedHashSet<String>();
        for (Map<String, String> i : allPainInfo) {
            String temp = i.get("painLocation");
            if (temp != null) {
                painSet.add(temp);
            }
        }
        return painSet;
    }

    // 증상 발생 위치를 영어 학술명에서 한국어로 번역해주는 메소드
    // 메인 쓰레드에서 호출 할 수 없음!!
    String translateAi(String value){
        String systemContent = "다음 단어를 일반인도 알아보기 쉬운 한국어로 번역해주세요. " +
                "답변은 문장이 아닌 한 단어로 작성해주세요.";
        try {
            return callAi(systemContent, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 증상을 읽고 요약해주는 메소드
    // 메인 쓰레드에서 호출 할 수 없음!!
    String textSummationAi(String value){
        String systemContent = "당신은 전문적인 의료 어시스턴트입니다. " +
                "환자의 상태 정보를 읽고, 각 통증 위치별로 주요 증상과 필요한 임상 정보를 중심으로 간결하게 요약해 주세요. " +
                "이 요약은 의사가 빠르게 파악하고 진료에 활용할 수 있도록 작성하고, 각각의 통증 위치는 개별 단락으로 분류해 주세요. " +
                "각 통증 위치별로 다음 형식을 따라 요약해 주세요:\n" +
                "1. 통증 위치 (예: '오른쪽 어깨')\n" +
                "2. 주요 증상 (예: '통증 강도: 중간 ~ 강 (악화 혹은 완화)', '통증 종류: 욱신거림')\n" +
                "3. 증상 발생 시각 (예: '2024.11.1 ~ 2024.11.5')\n" +
                "4. 기타 증상(예: '부기 (2024.11.3), 발열 (2024.11.5)')\n" +
                "입력된 내용 외에 새로운 통증 위치나 증상을 절대 생성하지 마세요. " +
                "스스로 판단한 의학적 소견은 절대 작성하지 마세요. " +
                "한국어를 사용하고, Markdown 문법은 사용하지 마세요.";

        try {
            return callAi(systemContent, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Ai를 직접적으로 호출하는 메소드. 타 메소드에서 사용
    // 메인 쓰레드에서 호출 할 수 없음!!
    String callAi(String systemContent, String userContent) throws Exception {
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

    // 모든 메소드를 활용하여 증상 부위별로 분리하며, 증상의 요약을 하여 Map 형태로 핸들러에 넘김
    // 메인 쓰레드에서 호출 가능
    void unifiedAiCall(List<Map<String, String>> allPainInfo) {
        new Thread(() -> {
            try {
                Set<String> painSet = getAllPainSet(allPainInfo);
                Map<String, String> painMap = choicePainInfo(allPainInfo, painSet);
                Map<String, String> resultMap = new HashMap<>();

                for (String painLocation : painSet) {
                    String painInfo = painMap.get(painLocation);
                    String translatedPainLocation = translateAi(painLocation);
                    String summationInfo = textSummationAi(translatedPainLocation + painInfo);
                    resultMap.put(translatedPainLocation, summationInfo);
                }
                Message message = handler.obtainMessage(1, resultMap);
                handler.sendMessage(message);
            } catch (Exception e) {
                Message message = handler.obtainMessage(0, e.getMessage());
                handler.sendMessage(message);
            }
        }).start();
    }
}