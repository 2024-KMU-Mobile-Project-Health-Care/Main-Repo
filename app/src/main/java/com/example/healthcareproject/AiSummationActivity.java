package com.example.healthcareproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthcareproject.aiModel.AiProcess;
import com.example.healthcareproject.aiModel.AiFragment;
import com.example.healthcareproject.painInput.Eng2Kor;
import com.example.healthcareproject.painInput.PainDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AiSummationActivity extends AppCompatActivity {
    Button btnAiProcess;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_summation);

        PainDatabaseHelper painDBHelper = new PainDatabaseHelper(getApplicationContext());

        btnAiProcess = (Button) findViewById(R.id.btn_ai_process);

        handler = new Handler(Looper.getMainLooper()) { // AI 결과를 받아오는 메세지 핸들러
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) { // AI 통신 성공 시
                    Map<String,String> resultMap = (Map<String, String>) msg.obj;
                    createFragment(resultMap);
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
                    unifiedAiCall(allPainInfo); // AI 호출
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
                    result.append(painInfo.get("painIntensity")).append(", ").append(painInfo.get("painType")).append(", ").append(painInfo.get("painStartTime"));
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

    // 모든 메소드를 활용하여 증상 부위별로 분리
    // 증상의 요약을 Map 형태로 핸들러에 넘김
    void unifiedAiCall(List<Map<String, String>> allPainInfo) {
        new Thread(() -> {
            try {
                Set<String> painSet = getAllPainSet(allPainInfo);
                Map<String, String> painMap = choicePainInfo(allPainInfo, painSet);
                AiProcess aiProcess = new AiProcess();
                Map<String, String> resultMap = new HashMap<>();

                for (String painLocation : painSet) {
                    String painInfo = painMap.get(painLocation);
                    String translatedPainLocation = Eng2Kor.getKor(painLocation);
                    String summationInfo = aiProcess.textSummationAi(translatedPainLocation + painInfo);
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

    // 동적 프래그먼트 생성
    void createFragment(Map<String, String> painMap){
        Fragment fragment = new AiFragment(painMap);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer, fragment); // XML로 정의된 FrameLayout 사용
        transaction.commit();
    }
}