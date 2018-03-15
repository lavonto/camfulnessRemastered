package fi.hamk.calmfulnessV2.helpers;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class SharedPreferences {
    //Log tag for degub
    static String TAG = "SharedPref";
    //Preference tag for attractions
    private static String lastGpsPoint = "lastGpsPoint";


    public static String getLastVisitedPoint(Context context) {
        return GetStringPreference(lastGpsPoint, context);
    }

    public static void setLastVisitedPoint(String gpsPointId, Context context) {
        SetStringPreference(lastGpsPoint, gpsPointId, context);
    }


    private static String GetStringPreference(String key, Context context) {
        try {
            String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
            Log.d(TAG, "Returnin preference: KEY: " + key + " VALUE: " + value);
            return value;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return "";
        }
    }


    private static void SetStringPreference(String key, String value, Context context) {

        try {
            final android.content.SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            final android.content.SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString(key, value).apply();
            Log.e(TAG, "Preference saved: KEY: " + key + " VALUE: " + value);

        } catch (Exception e) {
            Log.e(TAG, "Something went wrong when setting string preference: " + e.toString());
        }
    }
}
