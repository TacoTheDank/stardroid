package com.google.android.stardroid.activities.preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.google.android.stardroid.R;
import com.google.android.stardroid.activities.EditSettingsActivity;

public class MainPreferencesFragment extends PreferenceFragmentCompat {
    private static final String TAG = "MainPreferencesFragment";

    private static final String LOCATION_PREFS = "location_prefs";
    private static final String SENSOR_PREFS = "sensor_prefs";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_main);
        setupMainScreen();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((EditSettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_settings);
    }

    private void setupMainScreen() {
        findPreference(LOCATION_PREFS).setOnPreferenceClickListener(preference -> {
            ((EditSettingsActivity) getActivity()).openScreen(R.xml.preference_location);
            return true;
        });
        findPreference(SENSOR_PREFS).setOnPreferenceClickListener(preference -> {
            ((EditSettingsActivity) getActivity()).openScreen(R.xml.preference_sensor);
            return true;
        });
    }
}
