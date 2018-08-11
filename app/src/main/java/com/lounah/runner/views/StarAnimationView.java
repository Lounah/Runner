package com.lounah.runner.views;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import java.util.Random;

public class StarAnimationView extends View {

    private Random random = new Random();

    private static class Star {
        private float x;
        private float y;
        private int v;
        private int h;
        private int sz;
        private int shape;

        private float scale;
        private float alpha;
        private float speed;
    }

    private static final int BASE_SPEED_DP_PER_S = 7;
    private static final int COUNT = 75;
    private static final int SEED = 1337;
    private static final int MAX_SQUARES = 6;

    private static final float SCALE_MIN_PART = 0.65f;

    private static final float SCALE_RANDOM_PART = 0.55f;

    private static final float ALPHA_SCALE_PART = 0.5f;

    private static final float ALPHA_RANDOM_PART = 0.5f;

    private final Star[] mStars = new Star[COUNT];
    private final Random mRnd = new Random(SEED);

    private TimeAnimator mTimeAnimator;

    private float mBaseSpeed;
    private float mBaseSize;
    private long mCurrentPlayTime;

    private Paint paint;
    private int currSquaresCount = 0;

    private boolean increaseState = true;

    public StarAnimationView(Context context) {
        super(context);
        init();
    }

    public StarAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GRAY);
        mBaseSpeed = BASE_SPEED_DP_PER_S * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        for (int i = 0; i < mStars.length; i++) {
            final Star star = new Star();
            initializeStar(star, width, height);
            mStars[i] = star;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewHeight = getHeight();
        for (final Star star : mStars) {

            final float starSize = star.scale * mBaseSize;
            if (star.y + starSize < 0 || star.y - starSize > viewHeight) {
                continue;
            }

            final int save = canvas.save();

            canvas.translate(star.x, star.y);

            final float progress = (star.y + starSize) / viewHeight;
            canvas.rotate(360 * progress);

            final int size = Math.round(starSize);
            if (star.shape == 0) {
                paint.setColor(Color.GRAY);
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(Math.round(255 * star.alpha));
                canvas.drawCircle(-size, -size, 16f, paint);
            } else if (star.shape == 1) {
                paint.setAlpha(255);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.DKGRAY);
                paint.setStrokeWidth(8);
                canvas.drawLine(-size-30, -size, -size+30, -size, paint);
                canvas.drawLine(-size, -size-30, -size, -size+30, paint);
            } else if (star.shape == 2){
                paint.setColor(Color.RED);
                paint.setStrokeWidth(8);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(-size-20, -size+20, size+20, size-20, paint);
            }
            canvas.restoreToCount(save);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimeAnimator = new TimeAnimator();
        mTimeAnimator.setTimeListener((animation, totalTime, deltaTime) -> {
            if (!isLaidOut()) {
                return;
            }
            updateState(deltaTime);
            invalidate();
        });
        mTimeAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimeAnimator.cancel();
        mTimeAnimator.setTimeListener(null);
        mTimeAnimator.removeAllListeners();
        mTimeAnimator = null;
    }

    public void pause() {
        if (mTimeAnimator != null && mTimeAnimator.isRunning()) {
            mCurrentPlayTime = mTimeAnimator.getCurrentPlayTime();
            mTimeAnimator.pause();
        }
    }

    public void resume() {
        if (mTimeAnimator != null && mTimeAnimator.isPaused()) {
            mTimeAnimator.start();
            mTimeAnimator.setCurrentPlayTime(mCurrentPlayTime);
        }
    }

    private void updateState(float deltaMs) {
        final float deltaSeconds = deltaMs / 1000f;
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        for (final Star star : mStars) {

            if(star.v == 0)
            {
                star.v = random.nextInt() % 400 * (random.nextInt()%2 == 0?1:-1);
            }

            if(star.h == 0)
            {
                star.h = random.nextInt() % 400 * (random.nextInt()%2 == 0?1:-1);
            }

            int c = 0;
            if(star.v < 0)
            {
                star.v++;
                c = 1;
            }
            else
            {
                star.v--;
                c=-1;
            }

            int ch = 0;
            if(star.h < 0)
            {
                star.h++;
                ch = 1;
            }
            else
            {
                star.h--;
                ch=-1;
            }

//            star.x += star.speed * deltaSeconds * c;
//            star.y -= star.speed * deltaSeconds * ch;

            if (star.alpha >= 255) increaseState = false;

            if (star.alpha == 0) increaseState = true;

            if (increaseState) star.alpha++; else star.alpha--;

            final float size = star.scale * mBaseSize;

            if (star.y + size < 0) {
                initializeStar(star, viewWidth, viewHeight);
            }
            if (star.x + size < 0) {
                initializeStar(star, viewWidth, viewHeight);
            }
        }
    }

    private void initializeStar(Star star, int viewWidth, int viewHeight) {
        star.scale = SCALE_MIN_PART + SCALE_RANDOM_PART * mRnd.nextFloat();

        star.x = viewWidth * mRnd.nextFloat();
        int shape = random.nextInt(4);
        if (shape == 3) currSquaresCount++;
        if (currSquaresCount >= MAX_SQUARES) shape = random.nextInt(2);
        star.shape = shape;
        star.v = 0;
        star.h = 0;
        star.sz = 0;

        star.y = random.nextInt()% viewHeight;

        star.alpha = ALPHA_SCALE_PART * star.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
        star.speed = mBaseSpeed * star.alpha * star.scale;
    }

}

