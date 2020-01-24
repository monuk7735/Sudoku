package com.monuk7735.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class MainGameActivity extends AppCompatActivity {
    TableLayout[] mBox = new TableLayout[9];
    TextView[][] mSquare = new TextView[9][9];
    TextView mRemainingSquares;
    int mRemaining;
    Button[] mButton = new Button[10];
    TextView[][] individualBoxSquare = new TextView[9][9];
    Chronometer mStopwatch;
    ImageView mSettings;
    Vibrator vibrator;
    Intent iSettings;
    MediaPlayer tapSound, winnerSound;

    String SelectedNum = null;

    ConstraintLayout constraintLayout;

    private long chronometerOffset;
    char difficulty;
    boolean isSoundON, isVibrationON, isResumed, doubleBackToExitPressedOnce = false;

    SharedPreferences preferences, settings;
    SharedPreferences.Editor editor;

    Dialog dialog;

    @Override
    public void onBackPressed() {

        final SharedPreferences preferences = getSharedPreferences("GAME_PREFS", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        if (doubleBackToExitPressedOnce) {
            editor.clear();
            editor.putBoolean("isSaved", true);
            editor.apply();
            saveGame();
            super.onBackPressed();
            overridePendingTransition(R.anim.paused, R.anim.slide_out_bottom);
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

    @Override
    protected void onPause() {
        super.onPause();
        chronometerOffset = SystemClock.elapsedRealtime() - mStopwatch.getBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStopwatch.setBase(SystemClock.elapsedRealtime() - chronometerOffset);
        mStopwatch.start();
        preferences = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE);
        isSoundON = preferences.getBoolean(getString(R.string.prefs_sound), true);
        isVibrationON = preferences.getBoolean(getString(R.string.prefs_vibrate), true);
        if (difficulty != preferences.getString(getString(R.string.prefs_difficulty), "0").toCharArray()[0])
            Snackbar.make(constraintLayout, "Difficulty Changed", Snackbar.LENGTH_LONG)
                    .setAction("Restart", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NewGame();
                        }
                    })
                    .show();
        difficulty = preferences.getString(getString(R.string.prefs_difficulty), "0").toCharArray()[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        isResumed = getIntent().getExtras().getBoolean("isResumed", false);

        mStopwatch = findViewById(R.id.stopwatch);
        mRemainingSquares = findViewById(R.id.remainingSquares);
        mSettings = findViewById(R.id.settings);

        settings = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE);
        difficulty = settings.getString(getString(R.string.prefs_difficulty), "1").toCharArray()[0];
        isSoundON = settings.getBoolean(getString(R.string.prefs_sound), true);
        isVibrationON = settings.getBoolean(getString(R.string.prefs_vibrate), true);

        tapSound = MediaPlayer.create(this, R.raw.tone_tap_sound);
        winnerSound = MediaPlayer.create(this, R.raw.tone_winner);

        constraintLayout = findViewById(R.id.mParent);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mBox[0] = findViewById(R.id.square0);
        mBox[1] = findViewById(R.id.square1);
        mBox[2] = findViewById(R.id.square2);
        mBox[3] = findViewById(R.id.square3);
        mBox[4] = findViewById(R.id.square4);
        mBox[5] = findViewById(R.id.square5);
        mBox[6] = findViewById(R.id.square6);
        mBox[7] = findViewById(R.id.square7);
        mBox[8] = findViewById(R.id.square8);

        mButton[0] = findViewById(R.id.btn0);
        mButton[1] = findViewById(R.id.btn1);
        mButton[2] = findViewById(R.id.btn2);
        mButton[3] = findViewById(R.id.btn3);
        mButton[4] = findViewById(R.id.btn4);
        mButton[5] = findViewById(R.id.btn5);
        mButton[6] = findViewById(R.id.btn6);
        mButton[7] = findViewById(R.id.btn7);
        mButton[8] = findViewById(R.id.btn8);
        mButton[9] = findViewById(R.id.btn9);

        for (int i = 0; i < 9; i++) {
            individualBoxSquare[i][0] = mBox[i].findViewById(R.id.textView0);
            individualBoxSquare[i][1] = mBox[i].findViewById(R.id.textView1);
            individualBoxSquare[i][2] = mBox[i].findViewById(R.id.textView2);
            individualBoxSquare[i][3] = mBox[i].findViewById(R.id.textView3);
            individualBoxSquare[i][4] = mBox[i].findViewById(R.id.textView4);
            individualBoxSquare[i][5] = mBox[i].findViewById(R.id.textView5);
            individualBoxSquare[i][6] = mBox[i].findViewById(R.id.textView6);
            individualBoxSquare[i][7] = mBox[i].findViewById(R.id.textView7);
            individualBoxSquare[i][8] = mBox[i].findViewById(R.id.textView8);
        }

        for (int i = 0; i < 9; i += 3) {
            mSquare[i][0] = mBox[0 + i].findViewById(R.id.textView0);
            mSquare[i][1] = mBox[0 + i].findViewById(R.id.textView1);
            mSquare[i][2] = mBox[0 + i].findViewById(R.id.textView2);
            mSquare[i][3] = mBox[1 + i].findViewById(R.id.textView0);
            mSquare[i][4] = mBox[1 + i].findViewById(R.id.textView1);
            mSquare[i][5] = mBox[1 + i].findViewById(R.id.textView2);
            mSquare[i][6] = mBox[2 + i].findViewById(R.id.textView0);
            mSquare[i][7] = mBox[2 + i].findViewById(R.id.textView1);
            mSquare[i][8] = mBox[2 + i].findViewById(R.id.textView2);

            mSquare[i + 1][0] = mBox[0 + i].findViewById(R.id.textView3);
            mSquare[i + 1][1] = mBox[0 + i].findViewById(R.id.textView4);
            mSquare[i + 1][2] = mBox[0 + i].findViewById(R.id.textView5);
            mSquare[i + 1][3] = mBox[1 + i].findViewById(R.id.textView3);
            mSquare[i + 1][4] = mBox[1 + i].findViewById(R.id.textView4);
            mSquare[i + 1][5] = mBox[1 + i].findViewById(R.id.textView5);
            mSquare[i + 1][6] = mBox[2 + i].findViewById(R.id.textView3);
            mSquare[i + 1][7] = mBox[2 + i].findViewById(R.id.textView4);
            mSquare[i + 1][8] = mBox[2 + i].findViewById(R.id.textView5);

            mSquare[i + 2][0] = mBox[0 + i].findViewById(R.id.textView6);
            mSquare[i + 2][1] = mBox[0 + i].findViewById(R.id.textView7);
            mSquare[i + 2][2] = mBox[0 + i].findViewById(R.id.textView8);
            mSquare[i + 2][3] = mBox[1 + i].findViewById(R.id.textView6);
            mSquare[i + 2][4] = mBox[1 + i].findViewById(R.id.textView7);
            mSquare[i + 2][5] = mBox[1 + i].findViewById(R.id.textView8);
            mSquare[i + 2][6] = mBox[2 + i].findViewById(R.id.textView6);
            mSquare[i + 2][7] = mBox[2 + i].findViewById(R.id.textView7);
            mSquare[i + 2][8] = mBox[2 + i].findViewById(R.id.textView8);
        }

        if (isResumed) {
            Resume();
            mStopwatch.setBase(SystemClock.elapsedRealtime() - chronometerOffset);
        }
        else
            NewGame();
        mStopwatch.start();

        iSettings = new Intent(this, SettingsActivity.class);

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(1000);
                mSettings.startAnimation(anim);
                startActivity(iSettings);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.paused);
            }
        });

        //All Putting Buttons Number Click Events
        mButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                ButtonClickEvents(x);

            }
        });

        mButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                ButtonClickEvents(x);
            }
        });

        mButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                ButtonClickEvents(x);
            }
        });

        mButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                ButtonClickEvents(x);
            }
        });

        mButton[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                ButtonClickEvents(x);
            }
        });

        mButton[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                ButtonClickEvents(x);
            }
        });

        mButton[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                ButtonClickEvents(x);
            }
        });

        mButton[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                ButtonClickEvents(x);
            }
        });

        mButton[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                ButtonClickEvents(x);
            }
        });

        mButton[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 9;
                ButtonClickEvents(x);
            }
        });


        //All Square Button Click Events

        //Row1
        mSquare[0][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[0][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[0][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 0;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row2
        mSquare[1][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[1][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[1][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 1;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row3
        mSquare[2][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[2][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[2][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 2;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row4
        mSquare[3][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[3][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[3][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 3;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row5
        mSquare[4][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[4][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[4][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 4;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row6
        mSquare[5][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[5][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[5][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 5;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row7
        mSquare[6][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[6][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[6][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 6;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row8
        mSquare[7][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[7][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[7][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 7;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

        //Row8
        mSquare[8][0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 0;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 1;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 2;
                SquareButtonClickEvent(x, y);

            }
        });

        mSquare[8][3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 3;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 4;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 5;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 6;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 7;
                SquareButtonClickEvent(x, y);
            }
        });

        mSquare[8][8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = 8;
                int y = 8;
                SquareButtonClickEvent(x, y);
            }
        });

    }

    //Resume Game Functions
    private void Resume() {
        SetSquareColor();
        preferences = getSharedPreferences(getString(R.string.prefs_main_game), MODE_PRIVATE);
        String table, squareEnabled;
        table = preferences.getString(getString(R.string.prefs_table), "0");
        squareEnabled = preferences.getString(getString(R.string.prefs_hidden_squares), "0");
        chronometerOffset = preferences.getLong(getString(R.string.prefs_chronometer), 0);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                mSquare[i][j].setTextSize(25);
                mSquare[i][j].setText(String.format("%s", table.toCharArray()[i * 9 + j]));
                if (squareEnabled.toCharArray()[i * 9 + j] == '1') {
                    mSquare[i][j].setEnabled(true);
                    mSquare[i][j].setTextColor(getColor(R.color.pure_black));
                } else {
                    mSquare[i][j].setEnabled(false);
                    mSquare[i][j].setTextColor(getColor(R.color.pure_grey));
                }
            }
        }
        CountRemainingSquares();
        SetSquareColor();
        CheckEveryClick();
        Snackbar.make(constraintLayout, "Saved Game Loaded", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    //New Game Functions
    private void NewGame() {
        mStopwatch.start();
        Load(((int) difficulty) - 48);
        SetSquareColor();
        SetButtonColor();
        SetUnclickableSquare();
    }

    public void Load(int difficulty) {
        SudokuTableGenerator sudo = new SudokuTableGenerator();
        sudo.fillValues();
        Random hide = new Random();
        int favourable_probability = (difficulty + 3) * 100000;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                mSquare[i][j].setTextSize(25);
                if (hide.nextInt(1000000) < favourable_probability)
                    mSquare[i][j].setText(String.format("%s", " "));
                else
                    mSquare[i][j].setText(String.format("%s", (char) (sudo.returnValue(i, j) + 48)));
            }
        }
        CountRemainingSquares();
    }

    private void SetUnclickableSquare() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                mSquare[i][j].setTextColor(getColor(R.color.pure_black));
                if (!TextUtils.equals(mSquare[i][j].getText(), " ")) {
                    mSquare[i][j].setEnabled(false);
                    mSquare[i][j].setTextColor(getColor(R.color.pure_grey));
                }
            }
        }
    }

    private void SetSquareColor() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((i + j) % 2 == 0)
                    mSquare[i][j].setBackground(getDrawable(R.drawable.normal_even_cell));
                else
                    mSquare[i][j].setBackground(getDrawable(R.drawable.normal_odd_cell));
            }
        }
    }

    private void SetButtonColor() {
        for (int i = 0; i <= 9; i++) {
            mButton[i].setBackground(getDrawable(R.drawable.normal_button));
        }
    }

    //Count Remaining Square
    private void CountRemainingSquares() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (TextUtils.equals(mSquare[i][j].getText(), " "))
                    count++;
            }
        }
        mRemaining = count;
        mRemainingSquares.setText(String.format(" Remaining Squares: %s ", mRemaining));
    }

    //All in One Checker
    private boolean Check(int x, int y) {
        if (TextUtils.equals(mSquare[x][y].getText(), " "))
            return false;
        return CheckRow(x, y) || CheckColumn(x, y) || CheckBox(x, y);
    }

    //Row Functions
    private boolean CheckRow(int x, int y) {
        for (int i = 0; i < 9; i++) {
            if (i == y)
                continue;
            if (TextUtils.equals(mSquare[x][y].getText(), mSquare[x][i].getText()))
                return true;
        }
        return false;
    }


    //Column Functions
    private boolean CheckColumn(int x, int y) {
        for (int i = 0; i < 9; i++) {
            if (i == x)
                continue;
            if (TextUtils.equals(mSquare[x][y].getText(), mSquare[i][y].getText()))
                return true;
        }
        return false;
    }


    //Square Box Related Functions
    private boolean CheckBox(int x, int y) {
        int BoxX = returnBoxNumber(x, y);
        int BoxY = returnBoxY(x, y);
        for (int i = 0; i < 9; i++) {
            if (i == BoxY)
                ;
            else if (TextUtils.equals(individualBoxSquare[BoxX][BoxY].getText(), individualBoxSquare[BoxX][i].getText()))
                return true;
        }
        return false;
    }


    private int returnBoxNumber(int x, int y) {
        if (x <= 2) {
            if (y <= 2)
                return 0;
            else if (y <= 5)
                return 1;
            else
                return 2;
        } else if (x <= 5) {
            if (y <= 2)
                return 3;
            else if (y <= 5)
                return 4;
            else
                return 5;
        } else {
            if (y <= 2)
                return 6;
            else if (y <= 5)
                return 7;
            else
                return 8;
        }
    }

    private int returnBoxY(int x, int y) {
        if (x == 0 || x == 3 || x == 6) {
            if (y <= 2)
                return y;
            else if (y <= 5)
                return y - 3;
            else
                return y - 6;
        } else if (x == 1 || x == 4 || x == 7) {
            if (y <= 2)
                return y + 3;
            else if (y <= 5)
                return y;
            else
                return y - 3;
        } else {
            if (y <= 2)
                return y + 6;
            else if (y <= 5)
                return y + 3;
            else
                return y;
        }
    }

    // Check Win
    private void checkWin() {
        if (mRemaining > 0)
            return;
        CheckEveryClick();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (Check(i, j))
                    return;
            }
        }
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_win);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGame();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        if (isSoundON)
            winnerSound.start();
    }

    private void SquareButtonClickEvent(int x, int y) {
        SetSquareColor();
        if (SelectedNum != null) {
            if (isSoundON)
                tapSound.start();
            mSquare[x][y].setText(SelectedNum);
            if (Check(x, y)) {
                mSquare[x][y].setBackground(getDrawable(R.drawable.incorrect_cell));
            }
            if (isVibrationON)
                vibrator.vibrate(50);
            SetButtonColor();
            SelectedNum = null;
            CountRemainingSquares();
        }
        if (mRemaining > 0)
            CheckEveryClick();
        else
            checkWin();
    }

    private void ButtonClickEvents(int x) {
        if (SelectedNum != mButton[x].getText().toString()) {
            SelectedNum = mButton[x].getText().toString();
            SetButtonColor();
            mButton[x].setBackground(getDrawable(R.drawable.selected_button));
        } else {
            SelectedNum = null;
            SetButtonColor();
        }
    }

    private void CheckEveryClick() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (TextUtils.equals(mSquare[i][j].getText(), " "))
                    continue;
                if (mSquare[i][j].isEnabled() && Check(i, j))
                    mSquare[i][j].setBackground(getDrawable(R.drawable.incorrect_cell));
            }
        }
    }

    private void saveGame() {
        StringBuffer table = new StringBuffer(), squareEnabled = new StringBuffer();
        long timer = SystemClock.elapsedRealtime() - mStopwatch.getBase();
        mStopwatch.stop();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                table.append(mSquare[i][j].getText().toString());
                if (mSquare[i][j].isEnabled())
                    squareEnabled.append("1");
                else
                    squareEnabled.append("0");
            }
        }
        preferences = getSharedPreferences(getString(R.string.prefs_main_game), MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(getString(R.string.prefs_table), table.toString());
        editor.putString(getString(R.string.prefs_hidden_squares), squareEnabled.toString());
        editor.putLong(getString(R.string.prefs_chronometer), timer);
        editor.apply();
    }
}