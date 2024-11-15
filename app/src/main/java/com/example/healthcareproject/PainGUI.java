package com.example.healthcareproject;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.os.Looper;
import java.util.List;
import android.widget.Toast;

import com.example.healthcareproject.painInput.PainDatabaseHelper;
import com.example.healthcareproject.painInput.PainInfo;
import com.example.healthcareproject.painInput.ProcessPainData;
import com.example.healthcareproject.painInput.ViewPainDrag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/*
설명할 거
UI를 접고 펼 수 있게 설정한 것
그 버튼의 클릭 빈도수를 조절한 것
 */
public class PainGUI extends AppCompatActivity {
    private ViewPainDrag viewPainDrag;
    private LinearLayout layoutPainIntensity;
    private LinearLayout layoutPainDetails;
    private GridLayout layoutPainDrag;
    private boolean isExpanded = true;
    private boolean isAnimating = false;
    Button btnPainInput;
    Button btnSave;
    Button btnErase;
    ImageButton btnPainType1;
    ImageButton btnPainType2;
    ImageButton btnPainType3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pain_gui);

        PainDatabaseHelper dbHelper = new PainDatabaseHelper(this); // for debugging
        dbHelper.deleteAllPainInfo(); // for debugging

        viewPainDrag = findViewById(R.id.view_pain_drag);
        layoutPainIntensity = findViewById(R.id.layout_pain_intensity);
        layoutPainDetails = findViewById(R.id.layout_pain_details);
        layoutPainDrag = findViewById(R.id.layout_pain_drag_grid);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle the expand/collapse of layout
        btnPainInput = findViewById(R.id.btn_pain_input);
        btnPainInput.setOnClickListener(v -> {
            if (isExpanded) {
                btnPainInput.setText("고통입력▲");
                collapseLayout();
            } else {
                btnPainInput.setText("고통입력▼");
                expandLayout();
            }
            isExpanded = !isExpanded;
            new Handler(Looper.getMainLooper()).postDelayed(() -> isAnimating = false, 300);
        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            List<PainInfo> painInfoList = viewPainDrag.getPainInfoList();
            if (btnSave.getText().equals("다음으로")){
                if (painInfoList == null || painInfoList.isEmpty()) {
                    Toast.makeText(PainGUI.this, "저장할 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                layoutPainDrag.setVisibility(View.GONE);
                layoutPainIntensity.setVisibility(View.VISIBLE);
                btnSave.setText("저장하기");
            }
            else if (btnSave.getText().equals("저장하기")){
                Toast.makeText(PainGUI.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                String timeStamp = timeFormatter.format(calendar.getTime());
                List<Map<String, String>> processedPainData = ProcessPainData.processPainData(painInfoList, timeStamp);

                //PainDatabaseHelper dbHelper = new PainDatabaseHelper(PainGUI.this);
                for (Map<String, String> painData : processedPainData) {
                    String location = painData.get("painLocation");
                    String painType = painData.get("painType");
                    dbHelper.insertPainInfo(location, timeStamp, painType);
                }

                dbHelper.close();
                viewPainDrag.clearPath();
                layoutPainDrag.setVisibility(View.VISIBLE);
                layoutPainIntensity.setVisibility(View.GONE);
                btnSave.setText("다음으로");
            }
        });

        btnErase = findViewById(R.id.btnErase);
        btnErase.setOnClickListener(v -> {
            viewPainDrag.clearPath();
        });

        // Set up button listeners for pain types
        btnPainType1 = findViewById(R.id.btn_pain_type1);
        btnPainType1.setOnClickListener(v -> {
            viewPainDrag.setCurrentPainType(R.string.pain_type_1);
            setPainTypeBtnActive(btnPainType1, btnPainType2, btnPainType3);
        });

        btnPainType2 = findViewById(R.id.btn_pain_type2);
        btnPainType2.setOnClickListener(v -> {
            viewPainDrag.setCurrentPainType(R.string.pain_type_2);
            setPainTypeBtnActive(btnPainType2, btnPainType1, btnPainType3);
        });

        btnPainType3 = findViewById(R.id.btn_pain_type3);
        btnPainType3.setOnClickListener(v -> {
            viewPainDrag.setCurrentPainType(R.string.pain_type_3);
            setPainTypeBtnActive(btnPainType3, btnPainType1, btnPainType2);
        });
        btnPainType1.performClick(); // 자동으로 PainType1으로 시작하게끔 설정
    }

    private void setPainTypeBtnActive(ImageButton selectedButton, ImageButton otherButton1, ImageButton otherButton2) {
        // Disable the selected button and enable the other buttons
        selectedButton.setEnabled(false);
        otherButton1.setEnabled(true);
        otherButton2.setEnabled(true);
    }

    private void expandLayout() {
        layoutPainDetails.setVisibility(View.VISIBLE);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        layoutPainDetails.startAnimation(slideDown);
    }

    private void collapseLayout() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        layoutPainDetails.startAnimation(slideUp);
        layoutPainDetails.setVisibility(View.GONE);
    }
}