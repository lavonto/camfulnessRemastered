package fi.hamk.calmfulnessV2.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceManager;

import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.R;

/**
 * AppPreferencesFragment shows preferences specified in app_preferences.xml to users
 */

public class AppPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }

    @Override
    public void onResume() {

        // Check that associated activity is not null. If not, register onSharedPreferenceChangeListener
        if (getActivity() != null) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        // Unregister onSharedPreferenceChangeListener
        if (getActivity() != null) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        }
        super.onPause();
    }

    /**
     * Called when a preference is pressed
     *
     * @param preferenceScreen Preference screen which contains header
     * @param preference       Object of Preference class used to find and handle single preference
     * @return boolean
     */
    @Override
    public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, final Preference preference) {

        final Intent openPreferenceIntent = new Intent(getActivity(), SettingsFragment.class);

        switch (preference.getKey()) {
            //If user selects the route preference
            case "routeSettings":
                //Launch the route preference
                openPreferenceIntent.putExtra(SettingsFragment.EXTRA_SHOW_FRAGMENT, RoutePreferenceFragment.class.getName());
                openPreferenceIntent.putExtra(SettingsFragment.EXTRA_NO_HEADERS, true);
                this.startActivity(openPreferenceIntent);
                break;

            //If user selects the about preference
            case "about":
                openPreferenceIntent.putExtra(SettingsFragment.EXTRA_SHOW_FRAGMENT, AboutPreferenceFragment.class.getName());
                openPreferenceIntent.putExtra(SettingsFragment.EXTRA_NO_HEADERS, true);
                this.startActivity(openPreferenceIntent);
                break;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * Called when any changes has been made to preferences
     *
     * @param preferences Object of <code>{@link SharedPreferences}</code> which contains preferences used in app
     * @param key         Key of preference contained in shared preferences
     */
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String key) {

        switch (key) {

            case "locationPrecision":
                //If user selects Low or Medium precision
                if (preferences.getString("locationPrecision", "").equals("102") || preferences.getString("locationPrecision", "").equals("104")) {
                    new AlertDialogProvider(getActivity()).createAndShowDialog(getString(R.string.alert_title), getString(R.string.alert_location_precision));
                }
                break;

            case "locationInterval":
                //If user sets location interval out of bounds, bounds are 1 and 10 seconds
                if (Integer.parseInt(preferences.getString("locationInterval", "")) <= 0 || Integer.parseInt(preferences.getString("locationInterval", ""))*1000 > 10000) {
                    new AlertDialogProvider(getActivity()).createAndShowDialog(getString(R.string.alert_title), getString(R.string.alert_location_interval));
                    //Re-apply the default value
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("locationInterval", "1");
                    editor.apply();
                }
                break;
        }

        if (!SettingsFragment.isSettingsChanged()) {
            SettingsFragment.setChangedState(true);
        }
    }
}
