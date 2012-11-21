package com.example.set.game;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SetPreferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
