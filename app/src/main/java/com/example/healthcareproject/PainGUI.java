package com.example.healthcareproject;

import android.content.ClipData;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareproject.painInput.Eng2Kor;
import com.example.healthcareproject.painInput.PainDatabaseHelper;
import com.example.healthcareproject.painInput.PainInfo;
import com.example.healthcareproject.painInput.ProcessPainData;
import com.example.healthcareproject.painInput.ViewPainDrag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
//기본을 선으로 설정
//데이터에서 중복을 허용하는 것
public class PainGUI extends AppCompatActivity {
    private ViewPainDrag viewPainDrag;
    private LinearLayout layoutPainIntensity;
    private LinearLayout layoutPainDetails;
    private GridLayout layoutPainDrag;
    private boolean isExpanded = true;
    private boolean isAnimating = false;
    private int dbPointer = 0;
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
    int[] painColors = {
            R.color.blue,
            R.color.green,
            R.color.yellow,
            R.color.orange,
            R.color.red
    };

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

        PainDatabaseHelper dbHelper = new PainDatabaseHelper(this); // for debugging
        dbHelper.deleteAllPainInfo(); // for debugging
        dbPointer = 0;

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
            loadAndShowPain(6);
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
            return time1.compareTo(time2);
        });


        if (dbPointer >= allPainInfo.size()) {
            Toast.makeText(this, "불러올 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int dataEnd = Math.min(dbPointer + painNum, allPainInfo.size());

        LinearLayout painDataContainer = findViewById(R.id.pain_data_container);
        for (int i = dbPointer; i < dataEnd; i++) {
            Map<String, String> painInfo = allPainInfo.get(i);

            View painDataView = getLayoutInflater().inflate(R.layout.pain_data_item, null);
            ImageView itemImg = painDataView.findViewById(R.id.item_img);
            TextView itemHeader = painDataView.findViewById(R.id.item_header);
            TextView itemTimestamp = painDataView.findViewById(R.id.item_timestamp);
            TextView itemPainType = painDataView.findViewById(R.id.item_pain_type);
            ProgressBar itemPainIntensityBar = painDataView.findViewById(R.id.item_pain_intensity_bar);
            ImageView itemCopyImg = painDataView.findViewById(R.id.item_copy_img);

            String partName = painInfo.get("painLocation");
            itemImg.setImageResource(selectItemImage(partName));

            itemHeader.setText(Eng2Kor.getKor(painInfo.getOrDefault("painLocation", "Unknown Location")));

            try {
                String timestamp = painInfo.getOrDefault("painStartTime", "Unknown Time");
                Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(timestamp);
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yy/MM/dd HH시 mm분");
                itemTimestamp.setText(simpleFormat.format(date));
            } catch (ParseException e) {
                itemTimestamp.setText("유효하지 않은 시간");
                e.printStackTrace();
            }

            itemPainType.setText(painInfo.getOrDefault("painType", "Unknown Pain Type"));

            int intensity = Integer.parseInt(painInfo.getOrDefault("painIntensity", "0"));
            itemPainIntensityBar.setProgress(intensity);
            int color = ContextCompat.getColor(PainGUI.this, painColors[Math.min(intensity, painColors.length - 1)]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                itemPainIntensityBar.getProgressDrawable().setColorFilter(
                        new BlendModeColorFilter(color, BlendMode.SRC_IN)
                );
            } else {
                itemPainIntensityBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }


            itemCopyImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String painLocation = painInfo.getOrDefault("painLocation", "알 수 없음");
                    String painTime = painInfo.getOrDefault("painStartTime", "알 수 없음");
                    String painType = painInfo.getOrDefault("painType", "알 수 없음");
                    int painIntensity = Integer.parseInt(painInfo.getOrDefault("painIntensity", "0")) + 1; // 강도는 1을 더함

                    String copyText = "통증 위치 : " + Eng2Kor.getKor(painLocation) + "\n"
                            + "통증 시간 : " + painTime + "\n"
                            + "통증 유형 : " + painType + "\n"
                            + "통증 강도 : " + painIntensity + "/5\n";

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("통증 정보", copyText);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(PainGUI.this, "복사되었습니다:\n", Toast.LENGTH_SHORT).show();
                }
            });


            painDataContainer.addView(painDataView);
        }

        dbPointer = dataEnd;
        Log.d("dbPointer", String.valueOf(allPainInfo.size()) + " " + String.valueOf(dataEnd));
        dbHelper.close();
    }
    private int selectItemImage(String partName) {
        switch (partName) {
            // 척추
            case "Cervical vertebrae":
            case "Thoracic vertebrae":
            case "Lumbar vertebrae":
            case "Sacrum":
            case "Coccyx":
                return R.drawable.body_back_backbone;

            // 어깨뼈
            case "Scapula Left":
                return R.drawable.body_back_wing;
            case "Scapula Right":
                return R.drawable.body_back_wing;

            // 날개뼈
            case "Shoulder Left":
                return R.drawable.body_back_shoulder;
            case "Shoulder Right":
                return R.drawable.body_back_shoulder;

            case "Trapezius":
            case "Levator scapulae Left":
            case "Levator scapulae Right":
                return R.drawable.body_back_muscle_upper;

            case "Rhomboids":
            case "Infraspinatus Left":
            case "Infraspinatus Right":
            case "Teres minor Left":
            case "Teres minor Right":
                return R.drawable.body_back_muscle_middle;

            case "Latissimus dorsi":
            case "Erector spinae":
                return R.drawable.body_back_muscle_lower;

            default:
                return 0;
        }
    }

}