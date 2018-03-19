package fi.hamk.calmfulnessV2.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class LocalService extends Service {

    private final static String TAG = LocalService.class.getName();

    public static boolean isBound = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return super.onUnbind(intent);
    }

    /**
     * Returns nearest gps point from user's location
     * @param userLocation Latest location of the device
     * @param GpsPoints A list containing GPS points from the route
     * @return An array containing nearest GPS point index [1] and distance in meters [0]
     */
    public float[] getNearestPlace(LatLng userLocation, List<LatLng> GpsPoints){

        final float results[] = new float[2];
        float temp = 0;

        try {
            for (int i = 0; i < GpsPoints.size(); i++) {
                results[0] = getDistance(userLocation, GpsPoints.get(i));

                if (i == 0) {
                    temp = results[0];
                }

                if (results[0] < temp) {
                    temp = results[0];
                } else {
                    results[1] = i;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "There was an error while going through GpsPoints list: " + e);
        }
        Log.d(TAG, "Returning nearest point: INDEX: " + results[1] + " DISTANCE: " + results[0]);
        return  new float[] {results[0], results[1]};
    }

    private float getDistance(LatLng userLocation, LatLng targetLocation) {

        final float result[] = new float[2];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, targetLocation.latitude, targetLocation.longitude, result);
        return result[0];
    }

    /**
     * Check if user is withing impact range of the nearest GPS point
     * @param distance The distance to nearest GPS point
     * @return True if user is within impact range. False if not
     */
    public boolean isUserNearGpsPoint(float distance) {

        if (distance <= 40/* TODO: Replace with impact range of the gps point*/) {
            return true;
        }
        return false;
    }
}
