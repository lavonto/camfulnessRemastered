package fi.hamk.calmfulnessV2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulnessV2.asyncTasks.AsyncController;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.settings.AppPreferenceFragment;
import fi.hamk.calmfulnessV2.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = MainActivity.class.getName();

    // Objects
    private AlertDialogProvider mAlertDialogProvider;
    private MediaPlayer mMediaPlayer = null;
    private AssetFileDescriptor mAssetFileDescriptor;
    private SharedPreferences mSharedPreferences;
//    private IntentFilter intentFilter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets custom toolbar and sets it as support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }

//        // Get default shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mAlertDialogProvider = new AlertDialogProvider(this);

        //Set custom font to title
        final TextView lblTitle = findViewById(R.id.lbl_title);
        lblTitle.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/SCRIPTIN.ttf"));

        //Initialize Azure
        initAzure();

//        //IntentFilter for listening broadcasts only from BluetoothService
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothService.TAG);
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//
//        if (BluetoothHelper.getInstance() != null) {
//            // Initializes bluetooth adapter and service
//            BluetoothHelper.getInstance().initializeBtAdapter(this);
//            BluetoothHelper.getInstance().initializeBtService();
//        }
//
//        // Call a check if device supports Bluetooth & Bluetooth le
//        // Gets false in return if Bluetooth or Bluetooth le is not supported
//        if (!BluetoothHelper.checkBluetoothSupport(this)) {
//            if (Build.VERSION.SDK_INT < 21) {
//                finish();
//            } else {
//                finishAndRemoveTask();
//            }
//        }

        // Location permission check required in SDK (API) > 23
        // Checks if location access is granted in manifest. If not, alert that gps location data is required
        // Requests permission to use device location if permission is not granted
        if (Build.VERSION.SDK_INT >= 23) {
            final int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    mAlertDialogProvider.createAndShowDialog("GPS Error", "Permission to get GPS location data is required in order for the application to function");
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } else {
                Log.v(TAG, "Location permissions already granted");
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

        if (mSharedPreferences.getBoolean("drawRoute", false)) {
            playSound();
        }
        // Makes sure floating action buttons are correctly presented
        checkActionButtons();

//        //Register the receiver from BluetoothService
//        registerReceiver(mReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Mutes sound and releases media player, if player is playing
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                muteSound();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

//        //Unregister the receiver from BluetoothService
//        unregisterReceiver(mReceiver);

        super.onPause();
    }

    // For debugging TODO: Remove before release
    @Override
    protected void onDestroy() {
        Log.w(TAG, "ONDESTROY");
        super.onDestroy();
    }

    // Broadcast listener for service. Currently commented
    //    /**
//     * Broadcast callback for error messages from BluetoothService
//     */
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            //If BluetoothAdapter state has changed
//            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                //If the BT adapter has been turned off
//                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)  == BluetoothAdapter.STATE_OFF) {
//                    BluetoothService.setmScanning(false);
//                    getApplicationContext().stopService(new Intent(getApplicationContext(), BluetoothHelper.getService().getClass()));
//                }
//            }
//            //If error was broadcasted from BT service
//            else if (intent.getAction().equals(BluetoothService.TAG)) {
//                mAlertDialogProvider.createAndShowDialog(intent.getStringExtra(BluetoothService.EXTRA_TITLE), intent.getStringExtra(BluetoothService.EXTRA_MESSAGE));
//            }
//        }
//    };

    private void initAzure() {
        Log.d(TAG, "initAzure()");

        final Button button = findViewById(R.id.btnRetry);
        new AsyncController(new WeakReference<Context>(this), new WeakReference<Activity>(this), button).initAzure();
    }

    /**
     * Retry initialization of Azure
     *
     * @param view View that called this function
     */
    public void retryAzureInit(final View view) {
        Log.i(TAG, "Init Retry");
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
        final Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
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
        final SharedPreferences.Editor mPreferenceEditor = mSharedPreferences.edit();
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
        final SharedPreferences.Editor mPreferenceEditor = mSharedPreferences.edit();
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
                Log.e(TAG, e.toString(), e);
                mAlertDialogProvider.createAndShowDialogFromTask("Media error", e);
            }

            try {
                // Get file descriptor, where asset's data starts, byte length of asset and set them as media player's data source
                mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                mAlertDialogProvider.createAndShowDialogFromTask("Media error", e);
            }
        }

        try {
            // Prepare media player
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            mAlertDialogProvider.createAndShowDialogFromTask("Media error", e);
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
            Log.e(TAG, e.toString(), e);
            mAlertDialogProvider.createAndShowDialogFromTask("Media error", e);
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

//    /**
//     * Called when the current <code>{@link android.view.Window}</code> of the activity gains or loses focus
//     * This method is also called when notification drawer is in front
//     *
//     * @param hasFocus Boolean value. Value is <tt>true</tt> if activity has focus and <tt>false</tt> if not
//     */
//    @Override
//    public void onWindowFocusChanged(final boolean hasFocus) {
//
//        if (hasFocus) {
//
//            isFocused = true;
//
//            // Opens ExerciseActivity if notification were sent and user opens this activity
////            if (NotificationProvider.isNotificationSent()) {
////                final Intent openExercise = new Intent(this, ExerciseActivity.class);
////                startActivity(openExercise);
////
////                NotificationProvider.cancelNotification(this, 0);
////            }
//
////            // Makes sure Bluetooth is still enabled
////            BluetoothHelper.isBluetoothEnabled(this);
//        }
//
//        if (!hasFocus) {
//            isFocused = false;
//        }
//
//        super.onWindowFocusChanged(hasFocus);
//    }
}