package com.monuk7735.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Button bStart, bResume;
    ImageView mSettings;
    Intent iMainGame, iSettings;
    boolean isSaved;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ConstraintLayout constraintLayout;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bStart = findViewById(R.id.start);
        bResume = findViewById(R.id.resume);
        mSettings = findViewById(R.id.settings);
        constraintLayout = findViewById(R.id.main);

        preferences = this.getSharedPreferences("GAME_PREFS", MODE_PRIVATE);
        editor = preferences.edit();
        isSaved = preferences.getBoolean("isSaved", false);
        if (isSaved) {
            bResume.setVisibility(View.VISIBLE);
        } else
            bResume.setVisibility(View.GONE);

        iMainGame = new Intent(this, MainGameActivity.class);
        iSettings = new Intent(this, SettingsActivity.class);

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMainGame.putExtra("isResumed", false);
                startActivity(iMainGame);
            }
        });

        bResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMainGame.putExtra("isResumed", true);
                startActivity(iMainGame);
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(1000);
                startActivity(iSettings);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.paused);
                mSettings.startAnimation(anim);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getBoolean("isSaved", false))
            bResume.setVisibility(View.VISIBLE);
        else
            bResume.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), "Press again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
