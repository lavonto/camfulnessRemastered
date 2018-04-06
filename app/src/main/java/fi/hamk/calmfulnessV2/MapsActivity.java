package fi.hamk.calmfulnessV2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import fi.hamk.calmfulnessV2.asyncTasks.AsyncController;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.azure.Exercise;
import fi.hamk.calmfulnessV2.azure.LocationExercise;
import fi.hamk.calmfulnessV2.azure.Route;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.helpers.NotificationProvider;
import fi.hamk.calmfulnessV2.services.LocalService;
import fi.hamk.calmfulnessV2.settings.AppPreferenceFragment;
import fi.hamk.calmfulnessV2.settings.SettingsFragment;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = MapsActivity.class.getName();

    // Objects
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;
    private IntentFilter mIntentFilter;
    private LocalService mService;

    // Lists
    static List<Integer> visitedPoints = new ArrayList<>();

    // Booleans
    private static boolean isFocused;
    private boolean isTrackingLocation = true;

    // Intergers
    private int backPressed = 0;

    /**
     * @return <tt>True</tt> if activity has focus and <tt>false</tt> if not
     */
    public static boolean isFocused() {
        return isFocused;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

//        // Set filter to listen to messages from BluetoothService
//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(BluetoothService.TAG);
//        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // Fetch shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initializing mGoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        // If map is null, obtain the SupportMapFragment and get notified when the map is ready to be used
        if (mGoogleMap == null) {
            final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        final Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called to check if there is a notification set
     */
    @Override
    protected void onResume() {
        super.onResume();

        isTrackingLocation = true;
        if (SettingsFragment.isSettingsChanged() && mGoogleMap != null) {
            SettingsFragment.setChangedState(false);
            this.recreate();
        }
//        // Register the receiver from BluetoothService
//        registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTrackingLocation = false;

//        // Unregister the receiver from BluetoothService
//        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel all notifications when activity is destroyed
        NotificationProvider.cancelAllNotifications(this);

        // Remove location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        // Disconnect Google API client when activity is destroyed
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // If service is bound, unbind it
        if (mService.isBound) {
            unbindService(mConnection);
        }
    }

    /**
     * Called when back button is pressed. Return to MainActivity top of history stack
     */
    @Override
    public void onBackPressed() {

        backPressed++;

        Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show();

        // Create TimerTask to set backPressed to 0
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                backPressed = 0;
            }
        };

        // Return to home screen (exit application if user presses back key twice)
        if (backPressed >= 2) {
            task.cancel();
            super.onBackPressed();
        } else {
            // Create Timer object and set it to run TimerTask after 1500 ms
            final Timer timer = new Timer("Timer");
            timer.schedule(task, 1500);
        }
    }

    /**
     * Inflates the menu in toolbar
     *
     * @param menu Menu to be used
     * @return <tt>True</tt> to display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

//        if (!BluetoothService.isScanning()) {
        menu.findItem(R.id.menu_stop).setVisible(false);
//            menu.findItem(R.id.menu_scan).setVisible(true);
        menu.findItem(R.id.progress).setVisible(false);
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true);
        menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.progress).setActionView(R.layout.progressbar_menu);
//        }
        return true;
    }

    /**
     * Called when an item in menu is selected
     *
     * @param item Selected MenuItem
     * @return <tt>True</tt> to allow menu processing
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

//            // User chose the "Scan" item
//            case R.id.menu_scan:
//                //Check that Bluetooth is enabled
//                if (BluetoothHelper.getAdapter().isEnabled()) {
//                    //Set isScanning = true, service won't start until invalidateOptionsMenu has finished
//                    BluetoothService.setmScanning(true);
//                    //Start Bluetooth service
//                    startService(new Intent(this, BluetoothHelper.getService().getClass()));
//                    //Refresh menu items to show progress bar
//                    invalidateOptionsMenu();
//                    Toast.makeText(this, getString(R.string.scanning_start), Toast.LENGTH_SHORT).show();
//                } else
//                    BluetoothHelper.isBluetoothEnabled(this);
//
//                break;
//
//            // User chose the "Stop" action
//            case R.id.menu_stop:
//
//                //Set isScanning = false, service won't stop until invalidateOptionsMenu has finished
//                BluetoothService.setmScanning(false);
//                //Stop Bluetooth service
//                stopService(new Intent(this, BluetoothService.class));
//                //Refresh menu items to hide progress bar
//                invalidateOptionsMenu();
//                Toast.makeText(this, getString(R.string.scanning_stop), Toast.LENGTH_SHORT).show();
//
//                break;

            // User chose the "Settings" action
            case R.id.menu_settings:

                //Open settings activity
                final Intent intent = new Intent(this, SettingsFragment.class);
                intent.putExtra(SettingsFragment.EXTRA_SHOW_FRAGMENT, AppPreferenceFragment.class.getName());
                intent.putExtra(SettingsFragment.EXTRA_NO_HEADERS, true);
                this.startActivity(intent);

                break;
        }

        // User's action was not recognized.
        // Invokes the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get Google Map, set map type and enable user location
     *
     * @param googleMap Map to be used
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        //Fetch the height of ActionBar
        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        //Set top padding so My Location button is not under the ActionBar
        this.mGoogleMap.setPadding(0, (int) styledAttributes.getDimension(0, 0), 0, 0);
        //Recycle the attributes
        styledAttributes.recycle();
        // Gets map type from preferences. If preference is not found, uses default constant value  2 = GoogleMap.MAP_TYPE_SATELLITE
        this.mGoogleMap.setMapType(Integer.parseInt(mSharedPreferences.getString("mapType", "2")));

        // Move the map over Hämeenlinna, southern Finland
        this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(60.9928787, 24.4590243), 5.5f));

        // Check if draw route switch equals true
        if (mSharedPreferences.getBoolean("drawRoute", true)) {

            List<Route> routes = null;
            List<String> urls = new ArrayList<>();
            try {
                routes = AzureTableHandler.getRoutesFromDb();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // Fetch all preference keys to a Map
            final Map<String, ?> keys = mSharedPreferences.getAll();

            //Go through all uploaded routes
            if (routes != null) { // TODO Test and Debug
                for (Route route : routes) {
                    // Check if preferences contain preference key and what is it's value
                    if (keys.containsKey(route.getId())) {
                        // If preference set true, add it to urls list
                        if (mSharedPreferences.getBoolean(route.getId(), true)) {
                            urls.add(route.getFile());
                        }
                    } else {
                        urls.add(route.getFile());
                    }
                }
                new AsyncController(this, this).getRoutePoints(urls).execute();
            }
        } else {
            //User has selected not to draw routes
            this.mGoogleMap.clear();
        }
    }

    /**
     * Initialize user Location tracking
     *
     * @param bundle A mapping from String keys to various {@link Parcelable} values.
     */
    @Override
    public void onConnected(final Bundle bundle) {

        final LocationRequest mLocationRequest = new LocationRequest();
        // Gets priority from mSharedPreferences. If preference is not found, uses default constant value 100 = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.setPriority(Integer.parseInt(mSharedPreferences.getString("locationPrecision", "100")));
        mLocationRequest.setInterval(Integer.parseInt(mSharedPreferences.getString("locationInterval", "1")) * 1000);
        mLocationRequest.setFastestInterval(Integer.parseInt(mSharedPreferences.getString("locationInterval", "1")) * 1000);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        mGoogleMap.setMyLocationEnabled(false);

        // Adds myLocation button click listener
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (!isTrackingLocation) {
                    isTrackingLocation = true;
                    // When user presses my location button, get latitude and longitude of the device
                    final LatLng latLng = getUserLocation();
                    // Move camera to location of the device
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }
                return true;
            }
        });
    }

    @Override
    public void onConnectionSuspended(final int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        new AlertDialogProvider(this).createAndShowDialog("Maps connection error", String.valueOf(connectionResult));
    }

    /**
     * Called when device location changes
     *
     * @param location New location
     */
    @Override
    public void onLocationChanged(final Location location) {

        if (isTrackingLocation) {
            // Move camera to new Location
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
        }
        if (!mGoogleMap.isMyLocationEnabled()) {
            // Sets and enables button to locate device, if both fine and coarse Location access are granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                new AlertDialogProvider(this).createAndShowDialog("Location", "Location permission not granted. Unable to set device Location");
            }
        }

        if (mService.isBound) {
            final fi.hamk.calmfulnessV2.azure.Location nearestLocation = mService.getNearestLocation(getUserLocation());

            if (mService.isUserNearGpsPoint(getUserLocation(), nearestLocation)) {
                if (mService.getLastLocation() != nearestLocation) {
                    mService.setLastLocation(nearestLocation);

                    if (isFocused()) {
                        final Intent intent = new Intent(this, ExerciseActivity.class);
                        intent.putExtra("locationId", nearestLocation.getId());
                        Log.d(TAG,"STORED LOCATION ID: " + nearestLocation.getId());
                        startActivity(intent);
                    } else {
                        NotificationProvider.createNotification(this);
                    }
                }
            }
        }
    }

    /**
     * Draws polyline route between LatLng points
     *
     * @param latLngs List containing the LatLng points
     */
    public void drawRouteOnMap(final List<LatLng> latLngs) {

        // Sets polyline options
        final PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        // Goes through Location list and forms new LatLng objects from latitude and longitude pairs
        // Adds map markers to position of first and last index
        for (int i = 0; i < latLngs.size(); i++) {

            //Make sure mGoogleMap is not null (i.e user has moved to another activity)
            if (mGoogleMap != null) {
                // Disable marker navigation
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                if (i == 0) {
                    mGoogleMap.addMarker(new MarkerOptions().title(getString(R.string.marker_start))
                            .position(latLngs.get(i))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                } else if (i == latLngs.size() - 1) {
                    mGoogleMap.addMarker(new MarkerOptions().title(getString(R.string.marker_end))
                            .position(latLngs.get(i))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                options.add(latLngs.get(i));
            } else {
                break;
            }
        }

        for (fi.hamk.calmfulnessV2.azure.Location item : mService.getLocationsFromDb()) {
            mGoogleMap.addMarker(new MarkerOptions().title(item.getId())
                    .position(new LatLng(item.getLat(), item.getLon()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }

        // Draws a polyline between LatLng points
        if (mGoogleMap != null) {
            mGoogleMap.addPolyline(options);
        }
    }

    public void setProgressbarState(final boolean state) {
        final ConstraintLayout mProgressBar = findViewById(R.id.loading);
        if (state) {

            if (mProgressBar != null) {
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
            }

        } else {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(ProgressBar.GONE);
            }
        }
    }

//    // Fires an Intent on itself to clear current task and reload activity with new mSharedPreferences
//    private void updateMap() {
//        final Intent intent = new Intent(this, MapsActivity.class);
//        finish();
//        startActivity(intent);
//    }

    // Called when user touches screen of the device
    @Override
    public void onUserInteraction() {
        // TODO Research for better solution
        if (isTrackingLocation) {
            isTrackingLocation = false;
        }
    }

    /**
     * Called when the current <code>{@link android.view.Window}</code> of the activity gains or loses focus
     * This method is also called when user interacts with notification drawer
     *
     * @param hasFocus Boolean value. Value is <tt>true</tt> if activity has focus and <tt>false</tt> if not
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            isFocused = true;


            if (NotificationProvider.isNotificationSent()) {
                NotificationProvider.cancelNotification(this, "x", 0);

                final Intent intent = new Intent(this, ExerciseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            final LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Requests user to activate location if turned off by user
            if (!Objects.requireNonNull(mLocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new AlertDialogProvider(this).createAndShowLocationDialog(getResources().getString(R.string.alert_title), getResources().getString(R.string.alert_message_gps));
            }

            invalidateOptionsMenu();
        }

        if (!hasFocus) {
            isFocused = false;
        }
    }

    private LatLng getUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
                final LatLng location = new LatLng(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(), LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());
                return location;
            }
        }
        return null;
    }

    //    /**
//     * Broadcast callback for error messages from BluetoothService
//     */
//    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            //If BluetoothAdapter state has changed
//            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                //If the BT adapter has been turned off
//                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_OFF) {
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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // When bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}