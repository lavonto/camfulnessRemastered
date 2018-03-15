package fi.hamk.calmfulnessV2.settings;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.R;
import fi.hamk.calmfulnessV2.azure.RouteContainer;

public class RoutePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = RoutePreferenceFragment.class.getName();
    private PreferenceCategory preferenceCategory;
    private SharedPreferences preferences;
    private AlertDialogProvider mAlertDialogProvider;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.route_preferences);

        mAlertDialogProvider = new AlertDialogProvider(getActivity());

        //Check if there already is an AzureServiceAdapter instance
        if (!RouteContainer.isInitialized()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //List for LatLng points
                    try {
                        //Initialize Adapter
                        RouteContainer.Initialize();
                        Log.i(TAG, "Initialized");

                    } catch (Exception e) {
                        Log.e("Azure Storage Error", e.toString(), e);
                        mAlertDialogProvider.createAndShowDialogFromTask("Azure Storage Error", e);
                    }
                    return null;
                }
            }.execute();
        }

        populatePreferenceScreen();
    }

    @Override
    public void onResume() {
        super.onResume();

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
     * Fetches all possible routes from database
     * Creates a checkbox preference from each route
     * Adds checkbox preferences to preference category
     * Adds preference category to preference screen and shows preference screen
     */
    private void populatePreferenceScreen() {

        //TODO some sort of loading indicator instead of a toast
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                preferenceCategory = (PreferenceCategory) findPreference("routeCategory");
                preferenceCategory.setTitle(getString(R.string.route_draw_title));

                Toast.makeText(getActivity(), "Loading routes...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //TODO use getBlobNames from RouteContainer
                    for (final ListBlobItem blobItem : RouteContainer.getRouteContainer().listBlobs()) {
                        final CloudBlockBlob blob = (CloudBlockBlob) blobItem;

                        final CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getActivity());
                        checkBoxPreference.setTitle(blob.getName());
                        checkBoxPreference.setSummary(getString(R.string.route_choose_summary));
                        checkBoxPreference.setKey(blob.getName());

                        if (!preferences.contains(blob.getName())) {
                            checkBoxPreference.setChecked(true);
                        } else {
                            checkBoxPreference.setChecked(preferences.getBoolean(blob.getName(), true));
                        }
                        preferenceCategory.addPreference(checkBoxPreference);
                    }
                } catch (Exception e) {
                    Log.e("Azure Storage Error", e.toString(), e);
                    mAlertDialogProvider.createAndShowDialogFromTask("Azure Storage Error",e);
                }
                return null;
            }

//            @Override
//            protected void onPostExecute(Void voids) {
//                Toast.makeText(getActivity(), "Routes loaded!", Toast.LENGTH_SHORT).show();
//            }
        }.execute();
    }
}