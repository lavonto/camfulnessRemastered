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

    public float getNearestPlace(LatLng userLocation, List<LatLng> GpsPoints){

        final float result[] = new float[2];
        float temp = 0;

        try {
            for (int i = 0; i < GpsPoints.size(); i++) {
                result[0] = getDistance(userLocation, GpsPoints.get(i));

                Log.d(TAG, "getNearestPlace() nearest point index: " + i + " distance is " + result[0]);

                if (i == 0) {
                    temp = result[0];
                }

                if (result[0] < temp) {
                    temp = result[0];
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "There was an error while going through GpsPoints list: " + e);
        }
        return  result[0];
    }

    private float getDistance(LatLng userLocation, LatLng targetLocation) {

        final float result[] = new float[2];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, targetLocation.latitude, targetLocation.longitude, result);
        return result[0];
    }

    public boolean isUserNearGpsPoint(float distance) {

        if (distance <= 50/* impact range*/) {
            return true;
        }
        return false;
    }
}
