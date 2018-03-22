package fi.hamk.calmfulnessV2.helpers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.MapsActivity;

public class DirectorActivity extends AppCompatActivity {

    final static String TAG = DirectorActivity.class.getName();

    private static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstTime) {
            startActivity(new Intent(this, MainActivity.class));
            isFirstTime = false;
            Log.d(TAG, "Opening MainActivity");
            finish();
        } else {
            startActivity(new Intent(this, MapsActivity.class));
            Log.d(TAG, "Opening MapsActivity");
            finish();
        }
    }
}
