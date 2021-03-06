package fi.hamk.calmfulness.settings;

import android.preference.PreferenceActivity;

import java.util.List;

import fi.hamk.calmfulness.R;

/**
 * SettingsFragment show preference headers and validates AppPreferencesFragment's fragment
 */
public class SettingsFragment extends PreferenceActivity {

    /**
     * boolean to mark state of changes made to shared preferences
     */
    private static boolean settingsChanged;

    /**
     * Returns boolean state of shared preferences
     *
     * @return <tt>true</tt> if preferences were settingsChanged and <tt>false</tt> if not
     */
    public static boolean isSettingsChanged() {
        return settingsChanged;
    }

    /**
     * Sets new state of shared preferences
     * @param state <tt>true</tt> if preferences were settingsChanged and <tt>false</tt> when changes are checked
     */
    public static void setChangedState(final boolean state) {
        SettingsFragment.settingsChanged = state;
    }

    /**
     * Loads preference headers
     *
     * @param target single Header item that the user can select
     */
    @Override
    public void onBuildHeaders(final List<Header> target) {
        // Loads preference headers from .xml
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    /**
     * Validates preference fragment before it opens
     *
     * @param fragmentName Name of the preference fragment
     * @return <tt>true</tt> if preference fragment is valid or <tt>false</tt> if fragment is not valid
     */
    @Override
    public boolean isValidFragment(final String fragmentName) {
        if (AppPreferenceFragment.class.getName().equals(fragmentName)) {
            return AppPreferenceFragment.class.getName().equals(fragmentName);
        } else if (RoutePreferenceFragment.class.getName().equals(fragmentName)) {
            return RoutePreferenceFragment.class.getName().equals(fragmentName);
        } else if (AboutPreferenceFragment.class.getName().equals(fragmentName)) {
            return AboutPreferenceFragment.class.getName().equals(fragmentName);
        }

        return false;
    }
}
