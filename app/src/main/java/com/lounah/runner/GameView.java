package com.lounah.runner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lounah.runner.views.StarAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View {

    private static final float CELL_SIZE = 48.0f;
    private static final float HERO_SIZE = 16.0f;
    private static final float X = 56.0f;

    private static final String TAG = GameView.class.getSimpleName();

    private Timer timer;

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        };
    }

    private float x;
    private float y;
    private float maxY;
    private float minY;


    private PointF screen = new PointF();
    private RectF floor = new RectF();

    private float heroSize;
    private float cellSize;
    private float floorHeight;
    private float deltaX;


    // paints
    private static final Paint ballPaint;
    private static final Paint floorPaint;

    private static final Paint cellPaint;

    private static final Paint textPaint;

    private float speed = 0.0f;

    private float maxSpeed;

    private float delta;

    private int cols;
    private int rows;

    private String difficultyLevel;

    char[][] level;

    static {
        ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(Color.WHITE);

        floorPaint = new Paint();
        floorPaint.setAntiAlias(true);
        floorPaint.setColor(Color.GRAY);

        cellPaint = new Paint();
        cellPaint.setAntiAlias(true);
        cellPaint.setColor(Color.DKGRAY);
        cellPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.YELLOW);
        textPaint.setFakeBoldText(true);
    }

    public GameView(Context context, String difficultyLevel) {
        super(context);
        this.difficultyLevel = difficultyLevel;
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    private void initialize(Context context) {
        setBackgroundColor(Color.BLACK);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final float density = displayMetrics.density;

        cellSize = CELL_SIZE * density;
        heroSize = HERO_SIZE * density;

        x = X * density;
        y = 0.0f;
        minY = 0.0f;
        deltaX = 1.5f * density;

        textPaint.setTextSize(18.0f * displayMetrics.scaledDensity);
    }

    public void start() {
        stop();
        setKeepScreenOn(true);

        reset();

        if (difficultyLevel != null) {
            LevelGenerator generator = new LevelGenerator(200, rows);
            level = generator.initLevel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(createTimerTask(), 0, 16);
    }

    public void stop() {
        setKeepScreenOn(false);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        screen.x = getWidth();
        screen.y = getHeight();

        cols = (int) (getWidth() / cellSize) + 1; // offscreen
        rows = (int) (getHeight() / cellSize); // floorHeight != 0.0f

        floorHeight = screen.y - rows * cellSize;

        floor.bottom = getHeight();
        floor.top = getHeight() - floorHeight;
        floor.left = 0.0f;
        floor.right = getWidth();

        maxSpeed = (float) (Math.sqrt(getHeight())) * 1.5f;
        delta = (float) Math.sqrt(9.8f);
        Log.d(TAG, "maxSpeed: " + maxSpeed);

        maxY = screen.y - heroSize - floorHeight;
    }

    float dx;
    int dj;

    private void reset() {
        ballPaint.setColor(Color.WHITE);
        y = 0.0f;
        dj = 0;
        dx = 0.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(floor, floorPaint);

        if (level != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    float left = j * cellSize - dx;
                    float top = i * cellSize;
                    float right = left + cellSize;
                    float bottom = top + cellSize;
                    switch (level[i][j + dj]) {
                        case 't':
                            drawDownPointingTriangle(canvas, left, top, right, bottom);
                            break;
                        case 'b':
                            drawUpPointingTriangle(canvas, left, top, right, bottom);
                            break;
                    }
                }
            }
        }

        float cy = (screen.y - y - floorHeight - heroSize / 2.0f);

        canvas.drawCircle(x - heroSize / 2.0f, cy, heroSize / 2.0f, ballPaint);

        canvas.drawText("SCORE: " + dj * 10, heroSize, heroSize, textPaint);


        if (speed > 0.0f) {
            speed = Math.max(0.0f, speed - delta);
            y = Math.min(y + speed, maxY);
        } else if (y > minY) {
            speed -= delta;
            y = Math.max(y + speed, minY);
        }
        dx += deltaX;
        if (dx > cellSize) {
            dx = dx - cellSize;
            if (dj < 200 - cols) {
                dj++;
            } else {
                stop(); // TODO
            }
        }

        int i = (int) (cy / cellSize);
        int j = (int) ((x + dx) / cellSize) + dj;
        if (level != null && level[i][j] != 'e') {
            ballPaint.setColor(Color.RED);
            stop();
        }
    }

    private static void drawDownPointingTriangle(Canvas canvas, float left, float top, float right, float bottom) {
        float halfSize = (right - left) / 2.0f;

        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(right, top);
        path.lineTo(left + halfSize, bottom);
        path.close();
        canvas.drawPath(path, cellPaint);
    }

    private static void drawUpPointingTriangle(Canvas canvas, float left, float top, float right, float bottom) {
        float halfSize = (right - left) / 2.0f;
        final Path path = new Path();
        path.moveTo(left, bottom);
        path.lineTo(left + halfSize, top);
        path.lineTo(right, bottom);
        path.close();
        canvas.drawPath(path, cellPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (timer != null) {
                speed = maxSpeed;
            } else {
                start();
            }
        }
        return true;
    }

}

