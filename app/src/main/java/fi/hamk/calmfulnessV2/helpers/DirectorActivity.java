package fi.hamk.calmfulnessV2.helpers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.MapsActivity;

public class DirectorActivity extends AppCompatActivity {

    private static boolean isFirstTime = true;

    public static void setIsFirstTime(boolean isFirstTime) {
        DirectorActivity.isFirstTime = isFirstTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstTime) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, MapsActivity.class));
            finish();
        }
    }
}
