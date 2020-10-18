package com.google.android.stardroid.activities.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.stardroid.ApplicationConstants;
import com.google.android.stardroid.R;
import com.google.android.stardroid.activities.EditSettingsActivity;
import com.google.android.stardroid.util.MiscUtil;

import javax.inject.Inject;

public class SensorPreferencesFragment extends PreferenceFragmentCompat {
    private static final String TAG = MiscUtil.getTag(SensorPreferencesFragment.class);
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_sensor);
        setupSensorScreen();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((EditSettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.sensor_prefs);
    }

    public void setupSensorScreen() {
        super.onStart();
        Preference gyroPreference = findPreference(
                ApplicationConstants.SHARED_PREFERENCE_DISABLE_GYRO);
        if (gyroPreference != null) {
            gyroPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d(TAG, "Toggling gyro preference " + newValue);
                    enableNonGyroSensorPrefs(((Boolean) newValue));
                    return true;
                }
            });
        }

        enableNonGyroSensorPrefs(
                sharedPreferences.getBoolean(ApplicationConstants.SHARED_PREFERENCE_DISABLE_GYRO,
                        false));
    }

    private void enableNonGyroSensorPrefs(boolean enabled) {
        // These settings aren't compatible with the gyro.
        findPreference(
                ApplicationConstants.SENSOR_SPEED_PREF_KEY).setEnabled(enabled);
        findPreference(
                ApplicationConstants.SENSOR_DAMPING_PREF_KEY).setEnabled(enabled);
        findPreference(
                ApplicationConstants.REVERSE_MAGNETIC_Z_PREFKEY).setEnabled(enabled);
    }
}
