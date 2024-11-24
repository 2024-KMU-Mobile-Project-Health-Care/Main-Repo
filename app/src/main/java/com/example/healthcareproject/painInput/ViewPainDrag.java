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

    private BitmapLayer bitmapLayer;
    private PathLayer pathLayer;

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

        // Initialize layers
        bitmapLayer = new BitmapLayer();
        pathLayer = new PathLayer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }

        // Draw Path Layer
        pathLayer.draw(canvas);

        // Draw Bitmap Layer
        bitmapLayer.draw(canvas);

        if (!currentPath.isEmpty()) {
            canvas.drawPath(currentPath, currentPaint);
        }
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
                    invalidate();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    currentPath.lineTo(x, y);
                    if (currentPainInfo != null) {
                        currentPainInfo.addCoordinate(x, y);
                    }
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    if (currentPainInfo != null) {
                        painInfoList.add(currentPainInfo);
                        pathLayer.addPath(currentPainInfo, currentPath, currentPaint);
                        currentPainInfo = null;
                    }
                    currentPath = new Path();
                    invalidate();
                    break;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentPath.moveTo(x, y);
                    currentPainInfo = new PainInfo(currentPainType);
                    currentPainInfo.addCoordinate(x, y);
                    bitmapLayer.addBitmap(currentPainInfo, x, y);
                    return true;

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
        pathLayer.clearPaths();
        bitmapLayer.clearBitmaps();
        invalidate();
    }

    public void setCurrentPainType(int painTypeResId) {
        currentPainType = getResources().getString(painTypeResId);
        updatePaintStyle(currentPaint, currentPainType);
    }

    public void setCurrentSwitchMode(boolean switchMode) {
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

    private Bitmap getBitmapForPainType(String painType) {
        if (painType.equals(getResources().getString(R.string.pain_type_1))) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.pain_stabbing);
        } else if (painType.equals(getResources().getString(R.string.pain_type_2))) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.pain_heavy);
        } else if (painType.equals(getResources().getString(R.string.pain_type_3))) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.pain_throbbing);
        }
        return null;
    }

    private class PathLayer {
        private List<PathInfo> paths;

        public PathLayer() {
            paths = new ArrayList<>();
        }

        public void addPath(PainInfo painInfo, Path path, Paint paint) {
            paths.add(new PathInfo(path, createPaintForPainType(painInfo.getPainType())));
        }

        public void draw(Canvas canvas) {
            for (PathInfo pathInfo : paths) {
                canvas.drawPath(pathInfo.path, pathInfo.paint);
            }
        }

        public void clearPaths() {
            paths.clear();
        }

        private class PathInfo {
            Path path;
            Paint paint;

            public PathInfo(Path path, Paint paint) {
                this.path = new Path(path);
                this.paint = new Paint(paint);
            }
        }
    }

    private class BitmapLayer {
        private List<BitmapInfo> bitmaps;

        public BitmapLayer() {
            bitmaps = new ArrayList<>();
        }

        public void addBitmap(PainInfo painInfo, float x, float y) {
            Bitmap bitmap = getBitmapForPainType(painInfo.getPainType());
            if (bitmap != null) {
                bitmaps.add(new BitmapInfo(bitmap, x, y));
            }
        }

        public void draw(Canvas canvas) {
            for (BitmapInfo bitmapInfo : bitmaps) {
                canvas.drawBitmap(bitmapInfo.bitmap, bitmapInfo.x - (bitmapInfo.bitmap.getWidth() / 2),
                        bitmapInfo.y - (bitmapInfo.bitmap.getHeight() / 2), null);
            }
        }

        public void clearBitmaps() {
            bitmaps.clear();
        }

        private class BitmapInfo {
            Bitmap bitmap;
            float x;
            float y;

            public BitmapInfo(Bitmap bitmap, float x, float y) {
                this.bitmap = bitmap;
                this.x = x;
                this.y = y;
            }
        }
    }
}
