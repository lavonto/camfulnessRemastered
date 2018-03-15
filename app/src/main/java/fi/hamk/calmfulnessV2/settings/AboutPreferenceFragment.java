package fi.hamk.calmfulnessV2.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.R;

public class AboutPreferenceFragment extends PreferenceFragment {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load preferences from .xml
        addPreferencesFromResource(R.xml.about);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final AlertDialogProvider alertDialogProvider = new AlertDialogProvider(getActivity());

        if(preference.getKey().equals("blurView")) {
            alertDialogProvider.createAndShowApacheDialog(getString(R.string.alert_title_apache),getString(R.string.alert_message_apache));
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
