package fi.hamk.calmfulnessV2.helpers;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferences {
    //Log tag
    static String TAG = "SharedPref";
    //Preference tag for notifications
    private static String notificationPreferenceKey = "notificationPreferenceKey";


    public static String getLastVisitedPoint(Context context) {
        return GetNotificationPreference(notificationPreferenceKey, context);
    }

    public static void setLastVisitedPoint(String id, Context context) {
        SetNotificationPreference(notificationPreferenceKey, id, context);
    }

    public static void removePreference(String key, Context context) {
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();
        Log.d(TAG, "Removed preference. KEY: " + key);
    }

    private static String GetNotificationPreference(String key, Context context) {
        try {
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
            Log.d(TAG, "Returning preference. KEY: " + key + " VALUE: " + value);
            return value;
        } catch (Exception e) {
            Log.d(TAG,"Something went wrong when getting notification preference: " + e.toString());
            return "";
        }
    }

    private static void SetNotificationPreference(String key, String value, Context context) {

        try {
            final android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final android.content.SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString(key, value).apply();
            Log.e(TAG, "Preference saved. KEY: " + key + " VALUE: " + value);

        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when setting notification preference: " + e.toString());
        }
    }
}
