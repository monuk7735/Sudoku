package com.monuk7735.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.paused, R.anim.slide_out_bottom);
    }
}
