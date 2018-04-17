package fi.hamk.calmfulnessV2;

import android.Manifest;
import android.content.BroadcastReceiver;
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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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

    // Log tag
    private static String TAG = MapsActivity.class.getName();

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;
    private LocalService mService;

    private boolean isTrackingLocation = true;
    private boolean isRoutesDrawn;

    private int backPressed = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

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

        // Register Broadcast receiver with Intent filter
        registerReceiver(broadcastReceiver, new IntentFilter("fi.hamk.calmfulnessV2"));

        // Bind to LocalService
        bindService(new Intent(this, LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called to check if there is a notification set
     */
    @Override
    protected void onResume() {
        super.onResume();

        isTrackingLocation = true;

        // If settings were changed. Recreate this activity so that new settings come to effect
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

        // Unbind LocalService
        unregisterReceiver(broadcastReceiver);

        // Cancel all notifications when activity is destroyed
        NotificationProvider.cancelAllNotifications(this);

        // Remove location updates
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

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

        if (backPressed < 1) {
            Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
        }

        backPressed++;

        // Create TimerTask to set backPressed to 0
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                backPressed = 0;
            }
        };

        // Return to home screen (exit application if user presses back key twice)
        if (backPressed > 1) {
            timerTask.cancel();
            super.onBackPressed();
        } else {
            // Create Timer object and set it to run TimerTask after 1500 ms
            new Timer("CountTimet").schedule(timerTask, 1500);
        }
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
            try {
                // Get all routes from database
                routes = AzureTableHandler.getAllRoutesFromDb();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // Fetch all preference keys to a Map
            final Map<String, ?> keys = mSharedPreferences.getAll();

            // Go through all uploaded routes
            if (routes != null) {

                for (Route route : routes) {
                    // Check if preferences contain preference key and what is it's value
                    if (keys.containsKey(route.getId())) {
                        // If preference set true, add it to urls list
                        if (mSharedPreferences.getBoolean(route.getId(), true)) {
                            new AsyncController(this, this).getRoutePoints().execute(route.getFile());
                        }
                    } else {
                        // If preference was not found - user has not yet visited route selection settings - get file as default
                        new AsyncController(this, this).getRoutePoints().execute(route.getFile());
                    }
                }
            }
        } else {
            //User has selected not to draw routes
            this.mGoogleMap.clear();
            isRoutesDrawn = true;
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
                    if (latLng != null) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    }
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

        // Make sure that LocalService is bound to this activity
        if (mService.isBound) {
            // Call service to calculate nearest distance
            fi.hamk.calmfulnessV2.azure.Location nearestLocation = mService.getNearestLocation(new LatLng(location.getLatitude(), location.getLongitude()));

            if (nearestLocation != null) {
                // Call service to check if user is within the impact range of the nearest location
                if (mService.isUserNearGpsPoint(new LatLng(location.getLatitude(), location.getLongitude()), nearestLocation)) {
                    if (isRoutesDrawn) {
                        // Setting latest location as last location prevents new exercises popping up until user has found another location
                        if (mService.getLastLocation() != nearestLocation) {
                            mService.setLastLocation(nearestLocation);
                            // If this application - more specifically this activity - has a focus, launch new exercise immediately, otherwise send a notification to notify user
                            if (hasWindowFocus()) {
                                final Intent intent = new Intent(this, ExerciseActivity.class);
                                intent.putExtra("locationId", nearestLocation.getId());
                                startActivity(intent);
                                NotificationProvider.vibratePhone(this, 500);
                            } else {
                                NotificationProvider.createNotification(this, nearestLocation.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUserInteraction() {
        // TODO Research for better solution
        if (isTrackingLocation) {

            isTrackingLocation = false;

            final TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    isTrackingLocation = true;
                }
            };
            new Timer("TrackingTimer").schedule(timerTask, 5000);
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

        // User chose the "Settings" action
        if (item.getItemId() == R.id.menu_settings) {
            //Open settings activity
            final Intent intent = new Intent(this, SettingsFragment.class);
            intent.putExtra(SettingsFragment.EXTRA_SHOW_FRAGMENT, AppPreferenceFragment.class.getName());
            intent.putExtra(SettingsFragment.EXTRA_NO_HEADERS, true);
            this.startActivity(intent);
        }

        // User's action was not recognized. Invokes the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the current <code>{@link android.view.Window}</code> of the activity gains or loses focus
     * This method is also called when user interacts with notification drawer
     *
     * @param hasFocus Boolean value. Value is <tt>true</tt> if activity has focus and <tt>false</tt> if not
     */
    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            if (NotificationProvider.isNotificationSent()) {
                NotificationProvider.cancelNotification(this, 0);

                final Intent intent = new Intent(this, ExerciseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            // Requests user to activate location if location is/was turned off by user
            if (!Objects.requireNonNull((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new AlertDialogProvider(this).createAndShowLocationDialog(getResources().getString(R.string.alert_title), getResources().getString(R.string.alert_message_gps));
            }
        }
    }

    /**
     * Draws polyline route between LatLng points
     *
     * @param latLngs List containing the LatLng points
     */
    public void drawRouteOnMap(final List<LatLng> latLngs) {

        // Make sure mGoogleMap is not null (i.e user has moved to another activity)
        if (mGoogleMap != null) {

            // Sets polyline options
            final PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            // Goes through Location list and forms new LatLng objects from latitude and longitude pairs
            // Adds map markers to position of first and last index

            // Disable marker navigation
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            // Get first index of latLngs and show it as "start"  marker
            mGoogleMap.addMarker(new MarkerOptions().title(getString(R.string.marker_start))
                    .position(latLngs.get(0))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            // Get last index of latLngs and show it as "end"  marker
            mGoogleMap.addMarker(new MarkerOptions().title(getString(R.string.marker_end))
                    .position(latLngs.get(latLngs.size() - 1))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            // Add latLng points to polyline options
            for (LatLng latLng : latLngs) {
                options.add(latLng);
            }

            // TODO: >>>>>> REMOVE START
            for (fi.hamk.calmfulnessV2.azure.Location item : mService.getLocationsFromDb()) {
                mGoogleMap.addMarker(new MarkerOptions().title(item.getId())
                        .position(new LatLng(item.getLat(), item.getLon()))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            } // TODO: <<<<<< REMOVE END

            // Draws a polyline between LatLng points
            mGoogleMap.addPolyline(options);
            isRoutesDrawn = true;
        }
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

    private LatLng getUserLocation() {

        // Check if fine location and coarse location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Check if location data is available
            if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
                // Create new LatLng from latitude and longitude of user's location and return it
                return new LatLng(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(), LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());
            }
        }
        return null;
    }


    // Broadcast callback for error messages from BluetoothService
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            new AlertDialogProvider(context).createAndShowDialog(intent.getStringExtra("title"), intent.getStringExtra("message"));
        }
    };

    // Defines callbacks for service binding, passed to bindService()
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