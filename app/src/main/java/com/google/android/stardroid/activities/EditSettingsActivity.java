// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.android.stardroid.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.stardroid.R;
import com.google.android.stardroid.StardroidApplication;
import com.google.android.stardroid.activities.preference.LocationPreferencesFragment;
import com.google.android.stardroid.activities.preference.MainPreferencesFragment;
import com.google.android.stardroid.activities.preference.SensorPreferencesFragment;
import com.google.android.stardroid.activities.util.ActivityLightLevelChanger;
import com.google.android.stardroid.activities.util.ActivityLightLevelManager;
import com.google.android.stardroid.util.Analytics;
import com.google.android.stardroid.util.MiscUtil;

import javax.inject.Inject;

/**
 * Edit the user's preferences.
 */
public class EditSettingsActivity extends AppCompatActivity {

    private static final String TAG = MiscUtil.getTag(EditSettingsActivity.class);
    MainPreferencesFragment mFragment;
    @Inject
    Analytics analytics;
    private ActivityLightLevelManager activityLightLevelManager;

    private static int getTitleOfPage(int preferences) {
        switch (preferences) {
            case R.xml.preference_location:
                return R.string.location_prefs;
            case R.xml.preference_sensor:
                return R.string.sensor_prefs;
            default:
                return R.string.menu_settings;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((StardroidApplication) getApplication()).getApplicationComponent().inject(this);
        activityLightLevelManager = new ActivityLightLevelManager(
                new ActivityLightLevelChanger(this, null),
                PreferenceManager.getDefaultSharedPreferences(this));
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainPreferencesFragment()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityLightLevelManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        updatePreferences();
        activityLightLevelManager.onPause();
    }

    private PreferenceFragmentCompat getPreferenceScreen(int screen) {
        PreferenceFragmentCompat prefFragment = null;

        if (screen == R.xml.preference_location) {
            prefFragment = new LocationPreferencesFragment();
        } else if (screen == R.xml.preference_sensor) {
            prefFragment = new SensorPreferencesFragment();
        }
        return prefFragment;
    }

    public void openScreen(int screen) {
        PreferenceFragmentCompat fragment = getPreferenceScreen(screen);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment)
                .addToBackStack(getString(getTitleOfPage(screen))).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
            return true;
        }
        return false;
    }

    /**
     * Updates preferences on singletons, so we don't have to register
     * preference change listeners for them.
     */
    private void updatePreferences() {
        Log.d(TAG, "Updating preferences");
        analytics.setEnabled(mFragment.findPreference(Analytics.PREF_KEY).isEnabled());
    }
}
