package com.example.healthcareproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.os.Looper;
/*
설명할 거
UI를 접고 펼 수 있게 설정한 것
그 버튼의 클릭 빈도수를 조절한 것
 */
public class PainGUI extends AppCompatActivity {
    private ViewPainDrag viewPainDrag;
    private LinearLayout layoutPainDetails;
    private boolean isExpanded = true;
    private boolean isAnimating = false;
    Button btnPainInput;
    Button btnPainType1;
    Button btnPainType2;
    Button btnPainType3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pain_gui);

        viewPainDrag = findViewById(R.id.view_pain_drag);
        layoutPainDetails = findViewById(R.id.layout_pain_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnPainInput = (Button) findViewById(R.id.btn_pain_input);
        btnPainInput.setOnClickListener(new View.OnClickListener() {
            @Override
            //setText(▲▼)
            public void onClick(View v) {
                if (isExpanded) {
                    btnPainInput.setText("고통입력▲");
                    collapseLayout();
                } else {
                    btnPainInput.setText("고통입력▼");
                    expandLayout();
                }
                isExpanded = !isExpanded;
                new Handler(Looper.getMainLooper()).postDelayed(() -> isAnimating = false, 300);
            }
        });
        btnPainType1 = (Button) findViewById(R.id.btn_pain_type1);
        btnPainType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //draw 타입 변경
            }
        });
        btnPainType2 = (Button) findViewById(R.id.btn_pain_type2);
        btnPainType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //draw 타입 변경
            }
        });
        btnPainType3 = (Button) findViewById(R.id.btn_pain_type3);
        btnPainType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //draw 타입 변경
            }
        });

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