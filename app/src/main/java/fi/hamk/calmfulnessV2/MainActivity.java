package fi.hamk.calmfulnessV2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import fi.hamk.calmfulnessV2.helpers.PreferenceHandler;
import fi.hamk.calmfulnessV2.helpers.RetainedFragment;
import fi.hamk.calmfulnessV2.settings.AppPreferenceFragment;
import fi.hamk.calmfulnessV2.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    private RetainedFragment retainedFragment;
    private MediaPlayer mMediaPlayer;
    private AssetFileDescriptor mAssetFileDescriptor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout
        // Note! Once setContentView() has been called, you will never get a null View when calling FindViewById() provided you are looking in the correct layout and the View exists in that layout.
        setContentView(R.layout.activity_main);
        // Set support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        // Find the retained fragment on activity restarts
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        // Create the fragment and data the first time
        if (retainedFragment == null) {
            // Add the fragment
            retainedFragment = new RetainedFragment();
            fragmentManager.beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
            // Load data from a data source or perform any calculation
            retainedFragment.setRetainedActivity(this);
            retainedFragment.setRetainedContext(this);
        }

        //Set custom font to title
        final TextView lblTitle = findViewById(R.id.lbl_title);
        lblTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SCRIPTIN.ttf"));

        //Initialize Azure
        initAzure();

        // Checks if location access is granted in manifest. If not, alert that gps location data is required and requests permission to use device location if not granted
        final int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialogProvider(this).createAndShowDialog("GPS Error", "Permission to get GPS location data is required in order for the application to function");
            } else if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        super.onResume();

        setSoundState(new PreferenceHandler().getSoundPreferenceState(this));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Mutes sound and releases media player, if player is playing
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                setSoundState(false);
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        AsyncController.setActivity(retainedFragment.getActivity());
        AsyncController.setContext(retainedFragment.getRetainedContext());
    }

    private void initAzure() {
        new AsyncController(this, this).initAzure().execute();
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
     * Opens <code>{@link MapsActivity}</code> when user presses map button
     */
    public void openMapsActivity(final View view) {
        DirectorActivity.setIsFirstTime(false);
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    /**
     * Opens <code>{@link AppPreferenceFragment}</code> when user presses preferences button
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
                    // close asset file descriptor when done
                    mAssetFileDescriptor.close();
                } catch (Exception exception) {
                    new AlertDialogProvider(this).createAndShowDialog("Media error", exception.toString());
                }
            }
        }
    }

    /**
     * Used to enable or disable UI buttons based on the state of Azure connection
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
}