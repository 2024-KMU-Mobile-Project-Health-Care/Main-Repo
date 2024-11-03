package com.example.healthcareproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ViewPainDrag extends View {
    private Paint paint;
    private Path path;
    private Bitmap background;
    private List<PainInfo> painInfoList;
    private PainInfo currentPainInfo;
    private String currentPainType;

    public ViewPainDrag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(45f);

        path = new Path();
        painInfoList = new ArrayList<>();
        currentPainType = getResources().getString(R.string.pain_type_0);

        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.body_back);
        float scaleFactor = (float) getResources().getDisplayMetrics().widthPixels / originalBackground.getWidth();
        int newWidth = (int) (originalBackground.getWidth() * scaleFactor);
        int newHeight = (int) (originalBackground.getHeight() * scaleFactor);
        background = Bitmap.createScaledBitmap(originalBackground, newWidth, newHeight, true);

        // Set default paint style based on the initial type
        updatePaintStyle(currentPainType);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentPainType.equals(getResources().getString(R.string.pain_type_0))) {
                    // Disable drawing if pain type is "비활성화"
                    return false;
                }
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
        updatePaintStyle(currentPainType);
    }

    private void updatePaintStyle(String painType) {
        if (painType.equals(getResources().getString(R.string.pain_type_1))) {
            // Type 1: Yellow spiked line
            paint.setColor(Color.YELLOW);
            paint.setPathEffect(new DashPathEffect(new float[]{15, 5, 5, 5}, 0)); // Spiked effect
        } else if (painType.equals(getResources().getString(R.string.pain_type_2))) {
            // Type 2: Gray dotted line
            paint.setColor(Color.GRAY);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0)); // Dotted effect
        } else if (painType.equals(getResources().getString(R.string.pain_type_3))) {
            // Type 3: Red smooth line
            paint.setColor(Color.RED);
            paint.setPathEffect(null); // Smooth line
        } else {
            // Default (Disabled or any other unknown type)
            paint.setColor(Color.TRANSPARENT); // Transparent to make it invisible
            paint.setPathEffect(null);
        }
    }

    public List<PainInfo> getPainInfoList() {
        return painInfoList;
    }
}
