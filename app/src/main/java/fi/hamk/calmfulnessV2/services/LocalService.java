package fi.hamk.calmfulnessV2.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fi.hamk.calmfulnessV2.azure.AzureTableHandler;

/**
 * Simple service, when bound can calculate distances between user Location and GPS locations,
 * determine which one is closest to user and if user is within it's impact range.
 */
public class LocalService extends Service {

    // Log tag
    public final String TAG = LocalService.class.getName();

    // Boolean to track if service is bound or not
    public boolean isBound = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private List<fi.hamk.calmfulnessV2.azure.Location> locations;
    private fi.hamk.calmfulnessV2.azure.Location lastLocation;

    public fi.hamk.calmfulnessV2.azure.Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(fi.hamk.calmfulnessV2.azure.Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    /**
     * Class used for the client Binder.  Because this service
     * runs in the same process as its clients, there's no need for IPC - inter-process communication.
     */
    public class LocalBinder extends Binder {
        public LocalService getService() {
            // Return instance of service so clients can access it's public methods
            return LocalService.this;
        }
    }

    // Called when binService() is called. Binds this service by returning object of LocalBinder
    @Override
    public IBinder onBind(Intent intent) {
        getLocationsFromDb();
        isBound = true;
        return mBinder;
    }

    // Called when unbindService() is called. Default implementation only returns false.
    @Override
    public boolean onUnbind(Intent intent) {
        locations = null;
        isBound = false;
        return super.onUnbind(intent);
    }

    /**
     * Returns nearest gps point from user's Location
     *
     * @param userLocation Latest Location of the device
     * @return An array containing nearest GPS point index [1] and distance in meters [0]
     */
    public fi.hamk.calmfulnessV2.azure.Location getNearestLocation(LatLng userLocation) {

        final float results[] = new float[2];
        float maxDistance = 1000;
        float tempDistance = maxDistance;
        fi.hamk.calmfulnessV2.azure.Location tempLocation = null;

        final List<fi.hamk.calmfulnessV2.azure.Location> locations = getLocationsFromDb();

        try {
            for (fi.hamk.calmfulnessV2.azure.Location location : locations) {
                Location.distanceBetween(userLocation.latitude, userLocation.longitude, location.getLat(), location.getLon(), results);
                if (results[0] <= maxDistance) {
                    if (results[0] <= tempDistance) {
                        tempDistance = results[0];
                        tempLocation = location;
                    }
                }
            }
            return tempLocation;

        } catch (Exception exception) {
            broadCastException("List Error", exception.toString());
        }


        return null;
    }

    public boolean isUserNearGpsPoint(LatLng userLocation, fi.hamk.calmfulnessV2.azure.Location nearestLocation) {
        float[] results = new float[2];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, nearestLocation.getLat(), nearestLocation.getLon(), results);

        return results[0] < nearestLocation.getImpactRange();
    }

    public List<fi.hamk.calmfulnessV2.azure.Location> getLocationsFromDb() {

        if (locations == null) {
            try {
                locations = AzureTableHandler.getAllLocationsFromDb();
            } catch (ExecutionException | InterruptedException exception) {
                broadCastException(exception.getCause().toString(), exception.toString());
            }
        }
        return locations;
    }

    private void broadCastException(String title, String message) {
        Intent intent = new Intent("fi.hamk.calmfulnessV2");
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }
}
