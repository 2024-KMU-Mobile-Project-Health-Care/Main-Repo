package com.example.healthcareproject;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.List;

import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareproject.painInput.PainDatabaseHelper;
import com.example.healthcareproject.painInput.PainInfo;
import com.example.healthcareproject.painInput.ProcessPainData;
import com.example.healthcareproject.painInput.ViewPainDrag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
//painDataContainer UI 구성
//중복 reload방지, 없으면 예외처리

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
    ImageView imgPainIntensity;
    TextView txtPainIntensity;
    SeekBar seekBarPainIntensity;
    ImageButton btnReload;

    Switch switchDotLine;

    private String[] painDesc;
    private int[] painImages;
    private int[] painColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deleteDatabase("PainData.db");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pain_gui);

        String[] painDesc = {
                getString(R.string.pain_intensity0_desc),
                getString(R.string.pain_intensity1_desc),
                getString(R.string.pain_intensity2_desc),
                getString(R.string.pain_intensity3_desc),
                getString(R.string.pain_intensity4_desc),
        };
        int[] painImages = {
                R.drawable.pain_intensity0,
                R.drawable.pain_intensity1,
                R.drawable.pain_intensity2,
                R.drawable.pain_intensity3,
                R.drawable.pain_intensity4
        };
        int[] painColors = {
                R.color.blue,
                R.color.green,
                R.color.yellow,
                R.color.orange,
                R.color.red
        };

        PainDatabaseHelper dbHelper = new PainDatabaseHelper(this); // for debugging
        dbHelper.deleteAllPainInfo(); // for debugging

        viewPainDrag = findViewById(R.id.view_pain_drag);
        layoutPainIntensity = findViewById(R.id.layout_pain_intensity);
        layoutPainDetails = findViewById(R.id.layout_pain_details);
        layoutPainDrag = findViewById(R.id.layout_pain_drag_grid);
        imgPainIntensity = findViewById(R.id.img_pain_intensity);
        txtPainIntensity = findViewById(R.id.txt_pain_intensity);
        seekBarPainIntensity = findViewById(R.id.seekbar_pain_intensity);

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
                    dbHelper.insertPainInfo(location, timeStamp, painType, seekBarPainIntensity.getProgress());
                }

                List<Map<String, String>> allPainInfo = dbHelper.getAllPainInfo();
                for (Map<String, String> painInfo : allPainInfo) {
                    Log.d("PainInfo", "Location: " + painInfo.get("painLocation")
                            + ", Time: " + painInfo.get("painStartTime")
                            + ", Type: " + painInfo.get("painType")
                            + ", Intensity: " + painInfo.get("painIntensity"));
                }

                dbHelper.close();
                viewPainDrag.clearPath();
                layoutPainDrag.setVisibility(View.VISIBLE);
                layoutPainIntensity.setVisibility(View.GONE);
                btnSave.setText("다음으로");

                seekBarPainIntensity.setProgress(0);
                txtPainIntensity.setText(painDesc[0]);
                imgPainIntensity.setImageResource(painImages[0]);
                int color = ContextCompat.getColor(PainGUI.this, painColors[0]);
                seekBarPainIntensity.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                seekBarPainIntensity.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
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
        btnPainType1.performClick();

        Drawable originalThumb = seekBarPainIntensity.getThumb();
        Drawable enlargedThumb = ContextCompat.getDrawable(this, R.drawable.round_thumb);
        seekBarPainIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPainIntensity.setText(painDesc[progress]);
                imgPainIntensity.setImageResource(painImages[progress]);
                int color = ContextCompat.getColor(PainGUI.this, painColors[progress]);
                seekBarPainIntensity.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                seekBarPainIntensity.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnReload = findViewById(R.id.btn_reload);
        btnReload.setOnClickListener(v -> {
            loadAndShowPain(2);
        });

        switchDotLine = findViewById(R.id.switch_dot_line);
        switchDotLine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewPainDrag.setCurrentSwitchMode(isChecked);
        });

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

    private void collapseLayout(){
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        layoutPainDetails.startAnimation(slideUp);
        layoutPainDetails.setVisibility(View.GONE);
    }

    private void loadAndShowPain(int painNum){
        PainDatabaseHelper dbHelper = new PainDatabaseHelper(this);
        List<Map<String, String>> allPainInfo = dbHelper.getAllPainInfo();

        Collections.sort(allPainInfo, (pain1, pain2) -> {
            String time1 = pain1.get("painStartTime");
            String time2 = pain2.get("painStartTime");
            return time2.compareTo(time1);
        });

        int dataCount = Math.min(painNum, allPainInfo.size());

        LinearLayout painDataContainer = findViewById(R.id.pain_data_container);
        for (int i = 0; i < dataCount; i++) {
            Map<String, String> painInfo = allPainInfo.get(i);

            View painDataView = getLayoutInflater().inflate(R.layout.pain_data_item, null);



            painDataContainer.addView(painDataView);
        }

        dbHelper.close();
    }

}