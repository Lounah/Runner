package com.lounah.runner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lounah.runner.views.Star;
import com.lounah.runner.views.StarAnimationView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private Button btnStartGame;
    private Button btnSelectLevel;
    private TextView tvTitle;
    private StarAnimationView starAnimView;

    private String gameLevel;

    private static final String GAME_LEVEL_KEY = "GAME_LEVEL";
    private static final String PREFS_DEFAULT = "DEFAULT_PREFS";
    private static final String START_NEW_GAME = "Начать";

    private static final String GAME_LEVEL_EASY = "EASY";
    private static final String GAME_LEVEL_MEDIUM = "MEDIUM";
    private static final String GAME_LEVEL_HARD = "HARD";

    private int BASE_MARGIN;

    private float BASE_TEXT_SIZE;

    private static final float FPS = 1000 / 60;
    private static final int STARS_ON_FRAME = 60;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    private Timer timer;
    private TimerTask timerTask;
    private View[] stars;

    private GameView gameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        stars = generateStars();
        super.onCreate(savedInstanceState);

        BASE_MARGIN = (int) convertDpToPixel(16, this);
        BASE_TEXT_SIZE = spToPx(22, this);
        setUpStarAnimationView();
        setUpSettings();
        setUpBaseViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        starAnimView.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        starAnimView.pause();
    }

    private void setUpStarAnimationView() {
        starAnimView = new StarAnimationView(this);
        final LinearLayout.LayoutParams starLayoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        starAnimView.setLayoutParams(starLayoutParams);
        starLayoutParams.bottomMargin = (int) convertDpToPixel(120, this);
        starLayoutParams.topMargin = (int) convertDpToPixel(120, this);
        this.addContentView(starAnimView, starAnimView.getLayoutParams());
    }

    private void setUpSettings() {
        gameLevel = getSharedPreferences(PREFS_DEFAULT, Context.MODE_PRIVATE)
                .getString(GAME_LEVEL_KEY, GAME_LEVEL_EASY);
    }

    private void setUpBaseViews() {
        initializeTitle();
        initializeStartGameButton();
        initializeLevelButton();
    }

    private void initializeLevelButton() {
        btnSelectLevel = new Button(this);

        btnSelectLevel.setBackground(getResources().getDrawable(R.drawable.base_button_background));
        btnSelectLevel.setText(gameLevel);
        btnSelectLevel.setGravity(Gravity.BOTTOM);
        btnSelectLevel.setTextColor(getResources().getColor(android.R.color.white));
        btnSelectLevel.setAllCaps(true);
        btnSelectLevel.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        btnSelectLevel.setTextSize(BASE_TEXT_SIZE);
        btnSelectLevel.setPadding(BASE_MARGIN, 0, 0, 0);

        final LinearLayout.LayoutParams btnSelectLevelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnSelectLevelLayoutParams.setMargins(BASE_MARGIN, BASE_MARGIN * 13, BASE_MARGIN, BASE_MARGIN);
        btnSelectLevel.setLayoutParams(btnSelectLevelLayoutParams);

        btnSelectLevel.setOnClickListener(view -> onChangeLevel());

        this.addContentView(btnSelectLevel, btnSelectLevel.getLayoutParams());
    }

    private void initializeStartGameButton() {
        btnStartGame = new Button(this);

        btnStartGame.setBackground(getResources().getDrawable(R.drawable.base_button_background));
        btnStartGame.setText(START_NEW_GAME);
        btnStartGame.setGravity(Gravity.CENTER);
        btnStartGame.setTextColor(getResources().getColor(android.R.color.white));
        btnStartGame.setAllCaps(true);
        btnStartGame.setTextSize(BASE_TEXT_SIZE);
        btnStartGame.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        btnStartGame.setPadding(0, 0, BASE_MARGIN, 0);

        final LinearLayout.LayoutParams btnStartGameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnStartGameLayoutParams.setMargins(BASE_MARGIN, BASE_MARGIN * 19, BASE_MARGIN, BASE_MARGIN);

        btnStartGame.setLayoutParams(btnStartGameLayoutParams);

        btnStartGame.setOnClickListener(view -> onStartNewGame());

        this.addContentView(btnStartGame, btnStartGame.getLayoutParams());
    }

    private void initializeTitle() {
        tvTitle = new TextView(this);

        tvTitle.setText("RUNNER");
        tvTitle.setGravity(Gravity.TOP);
        tvTitle.setTextColor(getResources().getColor(android.R.color.white));
        tvTitle.setAllCaps(true);
        tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTitle.setTextSize(BASE_TEXT_SIZE);

        final LinearLayout.LayoutParams titleTextViewLayoutParams
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleTextViewLayoutParams.setMargins(BASE_MARGIN, BASE_MARGIN, BASE_MARGIN, BASE_MARGIN);

        tvTitle.setLayoutParams(titleTextViewLayoutParams);

        this.addContentView(tvTitle, tvTitle.getLayoutParams());
    }

    private void onChangeLevel() {
        if (gameLevel.equals(GAME_LEVEL_EASY)) {
            gameLevel = GAME_LEVEL_MEDIUM;
        } else if (gameLevel.equals(GAME_LEVEL_MEDIUM)) {
            gameLevel = GAME_LEVEL_HARD;
        } else {
            gameLevel = GAME_LEVEL_EASY;
        }
        setNewGameLevel(gameLevel);
    }

    private void setNewGameLevel(String level) {
        getSharedPreferences(PREFS_DEFAULT, Context.MODE_PRIVATE).edit().putString(GAME_LEVEL_KEY, level).apply();

        btnSelectLevel.setText(gameLevel);
    }

    private void onStartNewGame() {
        gameView = new GameView(this, gameLevel);
        gameView.setBackgroundColor(Color.BLACK);
        final LinearLayout.LayoutParams gameViewLayoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(gameView, gameViewLayoutParams);
        setUpStarAnimationView();
    }

    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private View[] generateStars() {
        final Random random = new Random();
        View[] stars = new View[STARS_ON_FRAME];
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(300);
        final LinearLayout.LayoutParams starLayoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (byte i = 0; i < STARS_ON_FRAME; i++) {
            final Star star = new Star(this, random.nextInt(900)+BASE_MARGIN, random.nextInt(getResources().getDisplayMetrics().heightPixels - BASE_MARGIN*6), random.nextInt(2), Color.GRAY);
            star.setLayoutParams(starLayoutParams);
            star.setAnimation(animation);
            stars[i] = star;
        }
        return stars;
    }
}

