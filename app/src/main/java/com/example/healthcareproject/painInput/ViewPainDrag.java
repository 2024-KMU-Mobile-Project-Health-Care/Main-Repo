package com.example.healthcareproject.painInput;

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
import android.widget.Switch;

import com.example.healthcareproject.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPainDrag extends View {
    private Paint currentPaint;
    private Path currentPath;
    private Bitmap background;
    private List<PainInfo> painInfoList;
    private PainInfo currentPainInfo;
    private String currentPainType;
    private boolean isLineMode;

    public ViewPainDrag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(45f);

        currentPath = new Path();
        painInfoList = new ArrayList<>();
        currentPainType = getResources().getString(R.string.pain_type_0);

        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.body_back);
        float scaleFactor = (float) getResources().getDisplayMetrics().widthPixels / originalBackground.getWidth();
        int newWidth = (int) (originalBackground.getWidth() * scaleFactor);
        int newHeight = (int) (originalBackground.getHeight() * scaleFactor);
        background = Bitmap.createScaledBitmap(originalBackground, newWidth, newHeight, true);

        updatePaintStyle(currentPaint, currentPainType);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }
        for (PainInfo painInfo : painInfoList) {
            Paint paint = createPaintForPainType(painInfo.getPainType());
            Path path = new Path();
            List<float[]> coordinates = painInfo.getCoordinates();

            if (!coordinates.isEmpty()) {
                path.moveTo(coordinates.get(0)[0], coordinates.get(0)[1]);
                for (int i = 1; i < coordinates.size(); i++) {
                    path.lineTo(coordinates.get(i)[0], coordinates.get(i)[1]);
                }
            }

            canvas.drawPath(path, paint);
        }
        canvas.drawPath(currentPath, currentPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isLineMode) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (currentPainType.equals(getResources().getString(R.string.pain_type_0))) {
                        return false;
                    }
                    currentPath.moveTo(x, y);
                    currentPainInfo = new PainInfo(currentPainType);
                    currentPainInfo.addCoordinate(x, y);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    currentPath.lineTo(x, y);
                    if (currentPainInfo != null) {
                        currentPainInfo.addCoordinate(x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (currentPainInfo != null) {
                        painInfoList.add(currentPainInfo);
                        currentPainInfo = null;
                    }
                    currentPath = new Path();
                    break;
            }
        }
        invalidate();
        return true;
    }

    public void clearPath() {
        currentPath.reset();
        painInfoList.clear();
        invalidate();
    }

    public void setCurrentPainType(int painTypeResId) {
        currentPainType = getResources().getString(painTypeResId);
        updatePaintStyle(currentPaint, currentPainType);
    }

    public void setCurrentSwitchMode(boolean switchMode){
        isLineMode = switchMode;
    }

    private void updatePaintStyle(Paint paint, String painType) {
        if (painType.equals(getResources().getString(R.string.pain_type_1))) {
            paint.setColor(Color.YELLOW);
            paint.setPathEffect(new DashPathEffect(new float[]{15, 5}, 0));
        } else if (painType.equals(getResources().getString(R.string.pain_type_2))) {
            paint.setColor(Color.GRAY);
            paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        } else if (painType.equals(getResources().getString(R.string.pain_type_3))) {
            paint.setColor(Color.RED);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setPathEffect(null);
        } else {
            paint.setColor(Color.TRANSPARENT);
            paint.setPathEffect(null);
        }
    }

    private Paint createPaintForPainType(String painType) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(45f);
        updatePaintStyle(paint, painType);
        return paint;
    }

    public List<PainInfo> getPainInfoList() {
        return painInfoList;
    }
}
