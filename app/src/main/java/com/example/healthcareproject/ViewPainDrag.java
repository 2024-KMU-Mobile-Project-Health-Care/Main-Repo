package com.example.healthcareproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/*
요구사항
1. 그리기 모드 버튼
  1.1 저장하기 버튼
  1.2 고통 유형 3개중에 1개 선택(버튼)
  1.3 고통 입력 방법 점/선 중에 스위치로 선택
2. 스크롤하면 내가 입력한 기록들 보기
3. 통계 보기
*/

public class ViewPainDrag extends View {
    private Paint paint;
    private Path path;
    private Bitmap background;
    private List<PainInfo> painInfoList;
    private PainInfo currentPainInfo;
    private String currentPainType;
    // 회색 점선
    // 빨간색 둥근 선
    // 노란색 뾰족선
    public ViewPainDrag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);      // 드래그 경로 색상
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(25f);

        path = new Path();
        painInfoList = new ArrayList<>();
        currentPainType = getResources().getString(R.string.pain_type_0);

        // 배경 이미지 초기화 (drawable 리소스를 배경으로 설정)
        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.body_back);
        // 화면 너비에 맞게 배경 이미지 크기 조정
        float scaleFactor = (float) getResources().getDisplayMetrics().widthPixels / originalBackground.getWidth();
        int newWidth = (int) (originalBackground.getWidth() * scaleFactor);
        int newHeight = (int) (originalBackground.getHeight() * scaleFactor);
        background = Bitmap.createScaledBitmap(originalBackground, newWidth, newHeight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 배경 이미지 그리기
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }
        // 드래그 경로 그리기
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                currentPainInfo = new PainInfo(currentPainType);
                currentPainInfo.addCoordinate(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                if (currentPainInfo != null) {
                    currentPainInfo.addCoordinate(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPainInfo != null) {
                    painInfoList.add(currentPainInfo);
                    currentPainInfo = null;
                }
                break;
        }
        invalidate();
        return true;
    }

    public void clearPath() {
        path.reset();
        invalidate();
    }

    public void setCurrentPainType(int painTypeResId) {
        currentPainType = getResources().getString(painTypeResId);
    }

    public List<PainInfo> getPainInfoList() {
        return painInfoList;
    }
}
