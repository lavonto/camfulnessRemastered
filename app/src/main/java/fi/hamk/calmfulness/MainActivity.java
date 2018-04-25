package fi.hamk.calmfulness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulness.asyncTasks.AsyncController;
import fi.hamk.calmfulness.helpers.AlertDialogProvider;
import fi.hamk.calmfulness.helpers.DirectorActivity;
import fi.hamk.calmfulness.helpers.PreferenceHandler;
import fi.hamk.calmfulness.helpers.RetainedFragment;
import fi.hamk.calmfulness.settings.AppPreferenceFragment;
import fi.hamk.calmfulness.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {


    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor mAssetFileDescriptor;
    private AsyncTask<Void, Void, Boolean> initAzure;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout
        // Note! Once setContentView() has been called, you will never get a null View when calling FindViewById() provided you are looking in the correct layout and the View exists in that layout.
        setContentView(R.layout.activity_main);
        // Set support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        //Set custom font to title
        final TextView lblTitle = findViewById(R.id.lbl_title);
        lblTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SCRIPTIN.ttf"));

        //Initialize Azure
        initAzure();

        final View decorView = getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        final ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //set background, if your root layout doesn't have one
        final Drawable windowBackground = decorView.getBackground();
        //Setup the BlurView
        final BlurView mBlurView = findViewById(R.id.blur_view);
        mBlurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(this))
                .blurRadius(1.5f);
        //Disable update of the BlurView
        mBlurView.setBlurAutoUpdate(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch sound preference and Set sound state (play or mute)
        setSoundState(new PreferenceHandler().getSoundPreferenceState(this));
    }

    @Override
    protected void onPause() {
        super.onPause();

        setSoundState(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        initAzure.cancel(true);
        initAzure = null;
    }

    private void initAzure() {
        // Start new initAzure task
        initAzure = new AsyncController(this, this).initAzure();
        initAzure.execute();
    }

    /**
     * Retry initialization of Azure
     *
     * @param view View that called this method
     */
    public void retryAzureInit(final View view) {
        findViewById(R.id.btnRetry).setVisibility(View.INVISIBLE);
        initAzure();
        initAzure.execute();
    }

    /**
     * Launches intent to open MapsActivity
     */
    public void openMapsActivity() {

        DirectorActivity.setIsFirstTime(false);
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }


    /**
     * Opens <code>{@link MapsActivity}</code> when user presses map button
     *
     * @param view View that called this method
     */
    public void openMapsActivity(final View view) {
        if (isLocationPermissionGranted()) {
            openMapsActivity();
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Opens <code>{@link AppPreferenceFragment}</code> when user presses preferences button
     *
     * @param view View that called this method
     */
    public void openAppPreferenceFragment(final View view) {

        final Intent intent = new Intent(this, SettingsFragment.class);
        intent.putExtra(SettingsFragment.EXTRA_SHOW_FRAGMENT, AppPreferenceFragment.class.getName());
        intent.putExtra(SettingsFragment.EXTRA_NO_HEADERS, true);
        this.startActivity(intent);
    }

    /**
     * Sets visibility of mProgressBar
     *
     * @param state <tt>True</tt> to show, <tt>False</tt> to hide
     */
    public void setProgressbarState(final boolean state) {
        if (state) {
            findViewById(R.id.loading).setVisibility(ProgressBar.VISIBLE);
        } else {
            findViewById(R.id.loading).setVisibility(ProgressBar.GONE);
        }
    }

    /**
     * Called when user presses play sound button
     * Edits setSoundState preference to match new preference
     *
     * @param view Current view
     */
    public void setSoundState(final View view) {
        // Check which button was pressed
        if (findViewById(R.id.fab_play_sound).isPressed()) {
            // Set sound state to play or stop depending which button was pressed
            setSoundState(true);
            // Set soundPreference to match new preference
            new PreferenceHandler().setSoundPreferenceState(this, true);
            // Set visibility of buttons to match new preference
            findViewById(R.id.fab_play_sound).setVisibility(View.GONE);
            findViewById(R.id.fab_mute_sound).setVisibility(View.VISIBLE);

        } else if (findViewById(R.id.fab_mute_sound).isPressed()) {
            setSoundState(false);
            new PreferenceHandler().setSoundPreferenceState(this, false);
            findViewById(R.id.fab_play_sound).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_mute_sound).setVisibility(View.GONE);
        }
    }

    private void setSoundState(boolean state) {
        if (state) {
            // Get MediaPlayer object
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();

                try {
                    // Fetch mainSound.mp3 from musics folder in assets (\app\src\main\assets\musics). NOTE! In code \ is an escape character so use / instead
                    mAssetFileDescriptor = getAssets().openFd("musics/mainSound.mp3");
                    // Get file descriptor, where asset's data starts, byte length of asset and set them as media player's data source
                    mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());

                } catch (Exception exception) {
                    new AlertDialogProvider(this).createAndShowDialog("Media error", exception.toString());
                }
            }

            try {
                // Prepare media player
                mMediaPlayer.prepare();
            } catch (Exception exception) {
                new AlertDialogProvider(this).createAndShowDialog("Media error", exception.toString());
            }

            // Set file to play on loop
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } else {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                //  When called, stops media player
                mMediaPlayer.stop();

                try {
                    // Close asset file descriptor and releases resources associated with it when done
                    mAssetFileDescriptor.close();
                    mAssetFileDescriptor = null;
                } catch (Exception exception) {
                    new AlertDialogProvider(this).createAndShowDialog("Media error", exception.toString());
                }
                // Reset media player to its uninitialized state and release resources associated with this MediaPlayer object when done
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    /**
     * Used to enable or disable UI buttons based on the state
     *
     * @param state <tt>True</tt> to indicate that connection was successful and to show Map and Settings button,
     *              <tt>False</tt> to show Retry button to retry connecting
     */
    public void setMenuButtonState(final boolean state) {
        // Set map, settings and retry buttons visibility to match state
        if (state) {
            findViewById(R.id.btnRetry).setVisibility(View.GONE);
            findViewById(R.id.btn_map).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_settings).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.btnRetry).setVisibility(View.VISIBLE);
        }
    }

    private boolean isLocationPermissionGranted() {
        // Checks if location access is granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Request permission. The result will be sent to onRequestPermissionsResult()
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            // Permission granted. Do stuff that required the permission
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMapsActivity();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show permission explanation dialog
                    new AlertDialogProvider(this).createAndShowDialog("GPS Error", getString(R.string.alert_gps_required));
                } else {
                    // Never ask again selected, or device policy prohibits the app from having that permission.
                    // So, disable that feature, or fall back to another situation...
                    new AlertDialogProvider(this).createAndShowDialog("GPS Error", getString(R.string.alert_gps_denied));
                }
            }
        }
    }
}