package fi.hamk.calmfulnessV2.azure;

import android.support.annotation.NonNull;

import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Class for handling table interactions with Azure in the context of BeaconProject
 */
public abstract class AzureTableHandler {

    /**
     * Instance of the AzureServiceAdapter
     */
    private static AzureServiceAdapter mAzure = null;
    /**
     * MobileServiceSyncTable for Exercise
     */
    private static MobileServiceSyncTable<Exercise> mExerciseTable;
    /**
     * MobileServiceSyncTable for Exercise location
     */
    private static MobileServiceSyncTable<LocationExercise> mLocationExerciseTable;
    /**
     * MobileServiceSyncTable for Route
     */
    private static MobileServiceSyncTable<Route> mRouteTable;
    /**
     * MobileServiceSyncTable for Location
     */
    private static MobileServiceSyncTable<Location> mLocationTable;

    // Initialization

    /**
     * Initializes the handler by assigning SyncTables
     *
     * @param mInstance Instance of AzureServiceAdapter
     */
    public static void Initialize(@NonNull final AzureServiceAdapter mInstance) {
        // Assign the AzureServiceAdapter instance for use
        AzureTableHandler.mAzure = mInstance;

        // Initialize MobileServiceSyncTables for use
        if (mExerciseTable == null && mLocationExerciseTable == null && mLocationTable == null && mRouteTable == null) {
            mExerciseTable = mAzure.getClient().getSyncTable(Exercise.class);
            mLocationExerciseTable = mAzure.getClient().getSyncTable(LocationExercise.class);
            mLocationTable = mAzure.getClient().getSyncTable(Location.class);
            mRouteTable = mAzure.getClient().getSyncTable(Route.class);
        } else if (mExerciseTable == null) {
            mExerciseTable = mAzure.getClient().getSyncTable(Exercise.class);
        } else if (mLocationExerciseTable == null) {
            mLocationExerciseTable = mAzure.getClient().getSyncTable(LocationExercise.class);
        } else if (mLocationTable == null) {
            mLocationTable = mAzure.getClient().getSyncTable(Location.class);
        } else if (mRouteTable == null) {
            mRouteTable = mAzure.getClient().getSyncTable(Route.class);
        } else {
            throw new IllegalStateException("MobileServiceTable(s) already initialized");
        }
    }

    /**
     * Initialize local storage with Beacon.class and Exercise.class as basis for the tables
     *
     * @throws ExecutionException               if the computation threw an exception
     * @throws InterruptedException             if the current thread was interrupted while waiting
     * @throws NoSuchFieldException             if used class doesn't have ID field
     * @throws MobileServiceLocalStoreException if there was an exception in the context of the Local Store
     */
    public static void initLocalStorage() throws InterruptedException, ExecutionException, NoSuchFieldException, MobileServiceLocalStoreException {
        mAzure.initLocalStorage(Exercise.class, LocationExercise.class, Location.class, Route.class);
    }

    // Database methods

    /**
     * Pull data from the remote table in Azure to local tables
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static void refreshTables() throws ExecutionException, InterruptedException {
        AzureServiceAdapter.refreshMobileServiceSyncTable(mExerciseTable);
        AzureServiceAdapter.refreshMobileServiceSyncTable(mLocationExerciseTable);
        AzureServiceAdapter.refreshMobileServiceSyncTable(mLocationTable);
        AzureServiceAdapter.refreshMobileServiceSyncTable(mRouteTable);
    }


    public static List<LocationExercise> getLocationFieldInLocationExerciseTableFromDb(String location) throws ExecutionException, InterruptedException {
        return mLocationExerciseTable.read(new ExecutableQuery().select("id", "exercise", "location").field("location").eq(location)).get();
    }

    public static List<LocationExercise> getExerciseFieldInLocationExerciseTableFromDb(String location) throws ExecutionException, InterruptedException {
        return mLocationExerciseTable.read(new ExecutableQuery().select("id", "exercise", "location").field("exercise").eq(location)).get();
    }

    public static Exercise lookUpExerciseFromDb(String id) throws ExecutionException, InterruptedException {
        return mExerciseTable.lookUp(id).get();
    }

    /**
     * Returns Exercise rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<Exercise> getAllExercisesFromDb() throws ExecutionException, InterruptedException {
        return mExerciseTable.read(new ExecutableQuery().select("id", "titleFi", "titleEn", "textFi", "textEn")).get();
    }

    /**
     * Returns Location exercise rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<LocationExercise> getAllLocationExercisesFromDb() throws ExecutionException, InterruptedException {
        return mLocationExerciseTable.read(new ExecutableQuery().select("id", "exercise", "location")).get();
    }

    /**
     * Returns Location rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<Location> getAllLocationsFromDb() throws ExecutionException, InterruptedException {
        return mLocationTable.read(new ExecutableQuery().select("id", "lat", "lon", "impactrange")).get();
    }

    /**
     * Returns Route rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<Route> getAllRoutesFromDb() throws ExecutionException, InterruptedException {
        return mRouteTable.read(new ExecutableQuery().select("id", "nameFi", "nameEn", "file")).get();
    }
}
