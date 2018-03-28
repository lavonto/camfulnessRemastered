package fi.hamk.calmfulnessV2.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;

/**
 * Simple service, when bound can calculate distances between user Location and GPS locations,
 * determine which one is closest to user and if user is within it's impact range.
 */
public class LocalService extends Service {

    // Log tag
    private final static String TAG = LocalService.class.getName();

    // Boolean to track if service is bound or not
    public static boolean isBound = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private static List<fi.hamk.calmfulnessV2.azure.Location> locations;

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
        Log.d(TAG, "Service bound. Returning binder");
        getLocationsFromDb();
        isBound = true;
        return mBinder;
    }

    // Called when unbindService() is called. Default implementation only returns false.
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service unbound");
        locations = null;
        isBound = false;
        return super.onUnbind(intent);
    }

    /**
     * Returns nearest gps point from user's Location
     *
     * @param userLocation Latest Location of the device
     * @param GpsPoints    A list containing GPS points from the route
     * @return An array containing nearest GPS point index [1] and distance in meters [0]
     */
    public float[] getNearestPlace(LatLng userLocation, List<LatLng> GpsPoints) {

        final float results[] = new float[2];
        float maxDistance = 1000;
        float tempDistance = maxDistance;

        if (locations == null) {
            getLocationsFromDb();
        } else {
            try {
                for (int i = 0; i < locations.size(); i++) {
                    Location.distanceBetween(userLocation.latitude, userLocation.longitude, locations.get(i).getLat(), locations.get(i).getLon(), results);

                    if (results[0] > maxDistance) {
                        Log.d(TAG, "No points were found within 1km");
                    } else if (results[0] <= tempDistance) {
                        tempDistance = results[0];
                    } else {
                        results[1] = i;
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "There was an error while going through GpsPoints list: " + e);
                //  TODO Exception broadcast here!
            }
            Log.d(TAG, "Returning nearest point: INDEX: " + results[1] + " DISTANCE: " + results[0]);
            //  TODO Exception broadcast here!
        }
        return new float[]{results[0], results[1]};
    }

    /**
     * Check if user is withing impact range of the nearest GPS point
     *
     * @param distance The distance to nearest GPS point
     * @return True if user is within impact range. False if not
     */
    public boolean isUserNearGpsPoint(int index, float distance) {

        if (distance <= locations.get(index).getImpactRange()) {
            return true;
        }
        return false;
    }

    private void getLocationsFromDb() {
        try {
            locations = AzureTableHandler.getLocationsFromDb();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace(); // TODO Exception broadcast here!
        }
    }
}
