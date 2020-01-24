package com.monuk7735.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

import java.nio.charset.IllegalCharsetNameException;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefrences);

        Preference instructions = findPreference("instructions");

        instructions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent iInstruction = new Intent( getActivity(),InstructionsActivity.class);
                startActivity(iInstruction);
                return true;
            }
        });

        /*findPreference("feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String subject = "Feedback for com.monuk7735.sudoku";
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto: "+ "monuk7735@gmail.com"));
                //intent.putExtra(Intent.EXTRA_EMAIL, "monuk7735@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);

                startActivity(Intent.createChooser(intent, "Choose Email client to use"));
                return true;
            }
        });*/


    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
