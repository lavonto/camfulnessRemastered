package fi.hamk.calmfulnessV2.helpers;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class PreferenceHandler {

    //Log tag
    private static String TAG = PreferenceHandler.class.getName();

    //Preference tag for notifications
    private static String notificationPreferenceKey = "notificationPreferenceKey";


    public static String getPointId(Context context) {
        return GetStringPreference(notificationPreferenceKey, context);
    }

    public static void setPointId(String id, Context context) {
        SetStringPreference(notificationPreferenceKey, id, context);
    }

    private static String GetStringPreference(String key, Context context) {
        try {
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
            Log.d(TAG, "Returning preference. KEY: " + key + " VALUE: " + value);
            return value;

        } catch (Exception e) {
            Log.d(TAG,"Something went wrong when getting notification preference: " + e.toString());
            return "";
        }
    }

    private static void SetStringPreference(String key, String value, Context context) {
        try {
            final android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final android.content.SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString(key, value).apply();
            Log.d(TAG, "Preference saved. KEY: " + key + " VALUE: " + value);

        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when setting notification preference: " + e.toString());
        }
    }
}
