package fi.hamk.calmfulnessV2.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.azure.Route;
import fi.hamk.calmfulnessV2.R;

public class RoutePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.route_preferences);

        populatePreferenceScreen();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check that associated activity is not null. If not, register onSharedPreferenceChangeListener
        if (getActivity() != null) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister onSharedPreferenceChangeListener
        if (getActivity() != null) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    /**
     * onSharedPreferenceChanged is called when any changes has been made to preferences
     *
     * @param preferences Object of SharedPreferences interface which contains preferences used in app
     * @param key         Key of preference contained in shared preferences
     */
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String key) {

        if (!SettingsFragment.isSettingsChanged()) {
            SettingsFragment.setChangedState(true);
        }
    }

    /**
     * Fetches all possible routes from local database
     * Creates a checkbox preference from each route
     * Adds checkbox preferences to preference category
     * Adds preference category to preference screen and shows preference screen
     */
    private void populatePreferenceScreen() {

        try {

            final List<Route> results = AzureTableHandler.getRoutesFromDb();

            for (int i = 0; i < results.size(); i++) {

                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                final PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("routeCategory");
                preferenceCategory.setTitle(getString(R.string.route_draw_title));

                final CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());

                String title = "";

                if (Locale.getDefault().getDisplayLanguage().equals("suomi")) {
                    title = results.get(i).getNameFi();
                } else {
                    title = results.get(i).getNameEn();
                }

                checkBoxPreference.setTitle(title);
                checkBoxPreference.setKey(results.get(i).getId());
                checkBoxPreference.setSummary(getString(R.string.route_choose_summary));

                if (!preferences.contains(results.get(i).getNameFi())) {
                    checkBoxPreference.setChecked(true);
                }
                preferenceCategory.addPreference(checkBoxPreference);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
