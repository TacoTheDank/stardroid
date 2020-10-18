package com.google.android.stardroid.activities.preference;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.stardroid.R;
import com.google.android.stardroid.activities.EditSettingsActivity;
import com.google.android.stardroid.util.MiscUtil;

import java.io.IOException;
import java.util.List;

public class LocationPreferencesFragment extends PreferenceFragmentCompat {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LOCATION = "location";
    private static final String TAG = MiscUtil.getTag(LocationPreferencesFragment.class);
    private Geocoder geocoder;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        geocoder = new Geocoder(getContext());
        addPreferencesFromResource(R.xml.preference_location);
        setupLocationScreen();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((EditSettingsActivity) getActivity()).getSupportActionBar().setTitle(R.string.location_prefs);
    }

    private void setupLocationScreen() {
        super.onStart();
        final Preference locationPreference = findPreference(LOCATION);
        Preference latitudePreference = findPreference(LATITUDE);
        Preference longitudePreference = findPreference(LONGITUDE);
        locationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(TAG, "Place to be updated to " + newValue);
                return setLatLongFromPlace(newValue.toString());
            }
        });

        latitudePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((EditTextPreference) locationPreference).setText("");
                return true;
            }
        });

        longitudePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((EditTextPreference) locationPreference).setText("");
                return true;
            }
        });
    }

    protected boolean setLatLongFromPlace(String place) {
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(place, 1);
        } catch (IOException e) {
            Toast.makeText(getContext(), getString(R.string.location_unable_to_geocode), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (addresses.size() == 0) {
            showNotFoundDialog(place);
            return false;
        }
        // TODO(johntaylor) let the user choose, but for now just pick the first.
        Address first = addresses.get(0);
        setLatLong(first.getLatitude(), first.getLongitude());
        return true;
    }

    private void setLatLong(double latitude, double longitude) {
        EditTextPreference latPreference = findPreference(LATITUDE);
        EditTextPreference longPreference = findPreference(LONGITUDE);
        latPreference.setText(Double.toString(latitude));
        longPreference.setText(Double.toString(longitude));
        String message = String.format(getString(R.string.location_place_found), latitude, longitude);
        Log.d(TAG, message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showNotFoundDialog(String place) {
        String message = String.format(getString(R.string.location_not_found), place);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.location_not_found_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
