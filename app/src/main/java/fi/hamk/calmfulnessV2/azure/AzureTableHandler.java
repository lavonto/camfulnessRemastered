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
     * MobileServiceSyncTable for Beacon
     */
    private static MobileServiceSyncTable<Beacon> mBeaconTable;

    // Initialization

    /**
     * Initializes the handler by assigning SyncTables
     *
     * @param mInstance Instance of AzureServiceAdapter
     */
    public static void Initialize(@NonNull final AzureServiceAdapter mInstance) {
        //Assign the AzureServiceAdapter instance for use
        AzureTableHandler.mAzure = mInstance;

        //Initialize MobileServiceSyncTables for use
        if (mBeaconTable == null && mExerciseTable == null) {
            mBeaconTable = mAzure.getClient().getSyncTable(Beacon.class);
            mExerciseTable = mAzure.getClient().getSyncTable(Exercise.class);
        } else if (mBeaconTable == null)
            mBeaconTable = mAzure.getClient().getSyncTable(Beacon.class);
        else if (mExerciseTable == null)
            mExerciseTable = mAzure.getClient().getSyncTable(Exercise.class);
        else
            throw new IllegalStateException("MobileServiceTable(s) already initialized");
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
        mAzure.initLocalStorage(Beacon.class, Exercise.class);
    }

    //Database methods

    /**
     * Pull data from the remote table in Azure to local tables
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static void refreshTables() throws ExecutionException, InterruptedException {
        AzureServiceAdapter.refreshMobileServiceSyncTable(mBeaconTable);
        AzureServiceAdapter.refreshMobileServiceSyncTable(mExerciseTable);
    }

    /**
     * Returns Exercise rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<Exercise> getExercisesFromDb() throws ExecutionException, InterruptedException {
        return mExerciseTable.read(new ExecutableQuery().select("exerciseTitle", "exerciseContent")).get();
    }

    /**
     * Returns Beacon rows from table
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    public static List<Beacon> getBeaconsFromDb() throws ExecutionException, InterruptedException {
        return mBeaconTable.read(new ExecutableQuery().select("beaconMac")).get();
    }
}
