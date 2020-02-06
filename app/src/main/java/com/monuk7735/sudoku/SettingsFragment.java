package com.monuk7735.sudoku;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefrences);

        final String deviceInfo;
        final String[] email = new String[1];
        email[0] = "monuk7735@gmail.com";

        deviceInfo = "Manufacturer: " + Build.MANUFACTURER + "\nAndroid Version: " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MODEL + "\nOS: " + Build.DISPLAY + "\n\nDon't Delete These Texts👆\n======================\n\n";

        Preference instructions = findPreference("instructions");

        instructions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent iInstruction = new Intent(getActivity(), InstructionsActivity.class);
                startActivity(iInstruction);
                return true;
            }
        });

        findPreference("feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for com.monuk7735.sudoku");
                intent.putExtra(Intent.EXTRA_TEXT, deviceInfo);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
