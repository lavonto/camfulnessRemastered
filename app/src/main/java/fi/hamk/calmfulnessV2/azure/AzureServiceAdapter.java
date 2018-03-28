package fi.hamk.calmfulnessV2.azure;

import android.content.Context;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service Adapter for handling communication with Azure
 */
public class AzureServiceAdapter {
    /**
     * URL of the Mobile App Backend
     */
    private static final String mMobileBackendUrl = "https://calmfulness-remastered.azurewebsites.net";
    /**
     * Name for the Local Storage database
     */
    private static final String mDatabase = "OfflineStore";
    /**
     * MobileServiceClient used for communicating with Azure
     */
    private MobileServiceClient mClient = null;
    /**
     * Instance of the AzureServiceAdapter
     */
    private static AzureServiceAdapter mInstance = null;
    /**
     * MobileServiceSyncContext used for Local Store
     */
    private static MobileServiceSyncContext syncContext = null;

    //Adapter instance methods

    /**
     * Constructor for AzureServiceAdapter
     * AzureServiceAdapter needs to be initialized with Initialize(context) method with the Application Context
     *
     * @param context Context for the Adapter
     * @throws MalformedURLException if the provided URL is malformed
     */
    private AzureServiceAdapter(final Context context) throws MalformedURLException {
        //Construct a new MobileServiceClient with the Mobile Backend URL and app context
        mClient = new MobileServiceClient(mMobileBackendUrl, context);
        //Assign a new OkHttpClientFactory to set a longer timeout for connection
        mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
            @Override
            public OkHttpClient createOkHttpClient() {
                final OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
                return okHttpClient;
            }
        });
    }

    /**
     * Initialize the AzureServiceAdapter in current context
     *
     * @param context Context for the Adapter
     * @throws MalformedURLException if the provided URL is malformed
     */
    public static void Initialize(final Context context) throws MalformedURLException {
        //Check that AzureServiceAdapter is not already initialized
        if (mInstance == null)
            mInstance = new AzureServiceAdapter(context);
        else
            throw new IllegalStateException("AzureServiceAdapter is already initialized");
    }

    /**
     * @return Instance of AzureServiceAdapter
     */
    public static AzureServiceAdapter getInstance() {
        //Check that AzureServiceAdapter is already initialized
        if (mInstance == null)
            throw new IllegalStateException("AzureServiceAdapter is not initialized");
        return mInstance;
    }

    /**
     * @return <tt>True</tt> if there is an instance of AzureServiceAdapter, else <tt>false</tt>
     */
    public static boolean isInitialized() {
        return mInstance != null;
    }

    //MobileServiceClient

    /**
     * @return MobileServiceClient used for communicating with Azure
     */
    MobileServiceClient getClient() {
        if (mClient == null)
            throw new IllegalStateException("MobileServiceClient not initialized");
        else
            return mClient;
    }

    //Offline storage

    /**
     * Initializes local storage for offline use
     *
     * @param classes Classes that represent the tables in Azure
     * @throws MobileServiceLocalStoreException if there was an exception in the context of the Local Store
     * @throws ExecutionException               if the computation threw an exception
     * @throws InterruptedException             if the current thread was interrupted while waiting
     * @throws NoSuchFieldException             if used class doesn't have ID field
     */
    void initLocalStorage(final Class... classes) throws MobileServiceLocalStoreException, ExecutionException, InterruptedException, NoSuchFieldException {
        //mClient needs to be initialized before this method is called
        if (mClient == null)
            throw new IllegalStateException("MobileServiceClient not initialized");

        //Fetch the syncContext
        syncContext = mClient.getSyncContext();

        //Check if Local Store is already initialized
        if (syncContext.isInitialized())
            throw new IllegalStateException("Local Storage already initialized");

        //Delete the old database file to avoid Azure retaining local non soft-deleted rows
        mClient.getContext().deleteDatabase(mDatabase);

        //Initialize SQLite database to be used by Azure
        final SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), mDatabase, null, 1);

        //Initialize Map where each table and it's columns will be defined
        final Map<String, ColumnDataType> tableDefinition = new HashMap<>();

        //Go through each class given as parameter
        for (final Class cls : classes) {
            //Make sure a class given as parameter isn't null
            if (cls == null)
                throw new NullPointerException("Null class used to initialize Local Store");

            //Make sure that the class given as parameter has fields
            if (cls.getDeclaredFields().length <= 0)
                throw new RuntimeException("Class used to initialize Local Storage must have fields");

            //Throws NoSuchFieldException if ID field is not found
            //Azure requires that at least ID field must be defined in a DTO class
            cls.getDeclaredField("id");

            //Make sure the map is clear
            tableDefinition.clear();

            //Get Fields from the Class
            final Field[] fields = cls.getDeclaredFields();
            //Go through each Field
            for (Field field : fields) {
                //Only check for private fields
                if (Modifier.isPrivate(field.getModifiers())) {
                    //Get the correct column type and put the field name and column type in the tableDefinition
                    if (field.getType().getSimpleName().equals("Boolean"))
                        tableDefinition.put(field.getName(), ColumnDataType.Boolean);
                    else if (field.getType().getSimpleName().equals("Integer"))
                        tableDefinition.put(field.getName(), ColumnDataType.Integer);
                    else if (field.getType().getSimpleName().equals("Double") || field.getType().getSimpleName().equals("Float"))
                        tableDefinition.put(field.getName(), ColumnDataType.Real);
                    else if (field.getType().getSimpleName().equals("String"))
                        tableDefinition.put(field.getName(), ColumnDataType.String);
                    else if (field.getType().getSimpleName().equals("Date"))
                        tableDefinition.put(field.getName(), ColumnDataType.Date);
                    else if (field.getType().getSimpleName().equals("DateTimeOffset"))
                        tableDefinition.put(field.getName(), ColumnDataType.DateTimeOffset);
                    else
                        tableDefinition.put(field.getName(), ColumnDataType.Other);
                }
            }
            //Make sure we don't define an empty table
            if (tableDefinition.isEmpty() || tableDefinition.size() <= 0)
                throw new RuntimeException("Can't initialize Local Storage with empty definition");

            //Add the defined Table to Local Storage
            localStore.defineTable(cls.getSimpleName(), tableDefinition);
        }

        //Create new handler for table operation errors and push completion results
        SimpleSyncHandler handler = new SimpleSyncHandler();

        //Initialize the syncContext with our defined localStore
        syncContext.initialize(localStore, handler).get();
    }

    /**
     * @return <tt>True</tt> if local storage has already been initialized, otherwise <tt>false</tt>
     */
    public static boolean checkLocalStorage() {
        return syncContext != null && syncContext.isInitialized();
    }

    /**
     * Pull data from the remote table in Azure to local MobileServiceSyncTable
     *
     * @param mTable MobileServiceSyncTable where the result is stored
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    static void refreshMobileServiceSyncTable(final MobileServiceSyncTable mTable) throws ExecutionException, InterruptedException {
        if (syncContext != null)
            //Perform a query against the remote table and store the result
            mTable.pull(null).get();
        else
            throw new IllegalStateException("SyncContext is not initialized");
    }

    /**
     * Synchronize local MobileServiceSyncTable data with the remote table in Azure
     *
     * @param mTable MobileServiceSyncTable to be synced
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    static void syncMobileServiceSyncTable(final MobileServiceSyncTable mTable) throws ExecutionException, InterruptedException {
        if (syncContext != null) {
            //Push all pending operations to the remote table
            syncContext.push().get();
            //Perform a query against the remote table and store the result
            mTable.pull(null).get();
        } else
            throw new IllegalStateException("SyncContext is not initialized");
    }
}
