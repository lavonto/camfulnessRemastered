package fi.hamk.calmfulnessV2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulnessV2.asyncTasks.AsyncController;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.helpers.DirectorActivity;
import fi.hamk.calmfulnessV2.settings.AppPreferenceFragment;
import fi.hamk.calmfulnessV2.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    // Objects
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor mAssetFileDescriptor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout
        setContentView(R.layout.activity_main);
        // Set support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        //Set custom font to title
        final TextView lblTitle = findViewById(R.id.lbl_title);
        lblTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SCRIPTIN.ttf"));

        //Initialize Azure
        initAzure();

        // Location permission check required in SDK (API) > 23
        // Checks if location access is granted in manifest. If not, alert that gps location data is required
        // Requests permission to use device location if permission is not granted
        if (Build.VERSION.SDK_INT >= 23) {
            final int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialogProvider(this).createAndShowDialog("GPS Error", "Permission to get GPS location data is required in order for the application to function");
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }

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

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("playSound", false)) {
            playSound();
        }
        // Makes sure floating action buttons are correctly presented
        checkActionButtons();

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Mutes sound and releases media player, if player is playing
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                muteSound();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void initAzure() {
        new AsyncController(this, this, (Button) findViewById(R.id.btnRetry)).initAzure().execute();
    }

    /**
     * Retry initialization of Azure
     *
     * @param view View that called this function
     */
    public void retryAzureInit(final View view) {
        initAzure();
    }

    /**
     * Used to enable or disable UI buttons based on the state of Azure connection
     *
     * @param state <tt>True</tt> to indicate that connection was successful and to show Map and Settings button,
     *              <tt>False</tt> to show Retry button to retry connecting
     */
    public void setMenuButtonState(final boolean state) {
        final Button btnMap = findViewById(R.id.btn_map);
        final Button btnSettings = findViewById(R.id.btn_settings);
        final Button btnRetry = findViewById(R.id.btnRetry);
        if (state) {
            btnRetry.setVisibility(View.GONE);
            btnMap.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);

        } else {
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Opens <code>{@link MapsActivity}</code> when user presses map button
     */
    public void openMap(final View view) {
        DirectorActivity.setIsFirstTime(false);
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    /**
     * Opens <code>{@link AppPreferenceFragment}</code> when user presses preferences button
     */
    public void openPreferences(final View view) {
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
        final ConstraintLayout mLoadingIndicator = findViewById(R.id.loading);
        if (state) {
            if (mLoadingIndicator != null) mLoadingIndicator.setVisibility(ProgressBar.VISIBLE);
        } else {
            if (mLoadingIndicator != null) mLoadingIndicator.setVisibility(ProgressBar.GONE);
        }
    }

    /**
     * Called when user presses play sound button
     * Edits playSound preference to match new preference
     *
     * @param view Current view
     */
    public void playSound(final View view) {
        playSound();
        final SharedPreferences.Editor mPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        mPreferenceEditor.putBoolean("playSound", true);
        mPreferenceEditor.apply();
        checkActionButtons();
    }

    /**
     * Called when user presses mute sound button
     * Mutes sound and set
     *
     * @param view Current view
     */
    public void muteSound(final View view) {
        muteSound();
        final SharedPreferences.Editor mPreferenceEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        mPreferenceEditor.putBoolean("playSound", false);
        mPreferenceEditor.apply();
        checkActionButtons();
    }

    /**
     * While in main activity, plays an ambient sound from .mp3 file from assets folder
     */
    private void playSound() {

        // Get MediaPlayer object
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            try {
                // Fetch mainSound.mp3 from assets (\app\src\main\assets)
                mAssetFileDescriptor = getAssets().openFd("mainSound.mp3");
            } catch (Exception e) {
                new AlertDialogProvider().createAndShowDialogFromTask("Media error", e);
            }

            try {
                // Get file descriptor, where asset's data starts, byte length of asset and set them as media player's data source
                mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
            } catch (Exception e) {
                new AlertDialogProvider().createAndShowDialogFromTask("Media error", e);
            }
        }

        try {
            // Prepare media player
            mMediaPlayer.prepare();
        } catch (Exception e) {
            new AlertDialogProvider().createAndShowDialogFromTask("Media error", e);
        }

        // Play file on loop
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    /**
     * When called, stops media player and closes asset file descriptor
     */
    private void muteSound() {
        mMediaPlayer.stop();
        try {
            mAssetFileDescriptor.close();
        } catch (Exception e) {
            new AlertDialogProvider().createAndShowDialogFromTask("Media error", e);
        }
    }

    /**
     * Sets visibility of mute and unmute <code>{@link FloatingActionButton}</code> according to state of media player
     */
    private void checkActionButtons() {
        final FloatingActionButton muteSound = findViewById(R.id.fab_mute_sound);
        final FloatingActionButton playSound = findViewById(R.id.fab_play_sound);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                playSound.setVisibility(View.GONE);
                muteSound.setVisibility(View.VISIBLE);
            } else {
                playSound.setVisibility(View.VISIBLE);
                muteSound.setVisibility(View.GONE);
            }
        } else {
            playSound.setVisibility(View.VISIBLE);
            muteSound.setVisibility(View.GONE);
        }
    }
}