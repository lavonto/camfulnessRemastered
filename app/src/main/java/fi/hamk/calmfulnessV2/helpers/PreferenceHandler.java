package fi.hamk.calmfulnessV2.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHandler {

    private String soundPreferenceKey = "playSound";

    public void setSoundPreferenceState(Context context, boolean state) {
        setBooleanPreference(context, state);
    }

    public boolean getSoundPreferenceState(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(soundPreferenceKey, false);
    }

    private void setBooleanPreference(Context context, boolean state) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(soundPreferenceKey, state)
                .apply();
    }

}
