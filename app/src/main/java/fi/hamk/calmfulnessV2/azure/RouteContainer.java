package fi.hamk.calmfulnessV2.azure;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobInputStream;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing container reference
 */
public abstract class RouteContainer {
    /**
     * Uri to Azure Storage Container
     */
    private static final String containerUri = "https://digisportbeaconstorage.blob.core.windows.net/routestorage";
    /**
     * Instance of RouteContainer
     */
    private static CloudBlobContainer routeContainer = null;

    //RouteContainer methods

    /**
     * Initialize the container
     *
     * @throws StorageException Represents an exception for the Microsoft Azure storage service.
     */
    public static void Initialize() throws StorageException {
        if (routeContainer == null) {
            //Get a new container reference
            routeContainer = new CloudBlobContainer(URI.create(containerUri));
            //Set timeouts for server requests
            routeContainer.getServiceClient().getDefaultRequestOptions().setMaximumExecutionTimeInMs(40000);
            routeContainer.getServiceClient().getDefaultRequestOptions().setTimeoutIntervalInMs(40000);
            //Create the container if it doesn't exist
            routeContainer.createIfNotExists();
        } else
            throw new IllegalStateException("RouteContainer already initialized");
    }

    /**
     * @return <tt>True</tt> if RouteContainer has been initialized, else <tt>false</tt>
     */
    public static boolean isInitialized() {
        return routeContainer != null;
    }

    /**
     * @return Instance of RouteContainer
     */
    public static CloudBlobContainer getRouteContainer() {
        if (routeContainer == null)
            throw new IllegalStateException("RouteContainer is not initialized");
        return routeContainer;
    }

    //Blobs

    /**
     * @return List that contains names of all the blobs within the container
     */
    public static List<String> getBlobNames() {
        if (routeContainer == null)
            throw new IllegalStateException("RouteContainer is not initialized");

        final List<String> blockBlobs = new ArrayList<>();
        for (ListBlobItem listBlobItem : routeContainer.listBlobs()) {
            final CloudBlockBlob cloudBlockBlob = (CloudBlockBlob) listBlobItem;
            blockBlobs.add(cloudBlockBlob.getName());
        }
        return blockBlobs;
    }

    /**
     * Open and return a new input stream for reading a BlockBlob from Azure Storage
     *
     * @param blobName Name of a BlockBlob
     * @return BlobInputStream for reading a BlockBlob
     * @throws URISyntaxException to indicate that a string could not be parsed as a URI reference.
     * @throws StorageException   to indicate an exception for the Microsoft Azure storage service.
     */
    public static BlobInputStream getInputStream(final String blobName) throws URISyntaxException, StorageException {
        if (routeContainer == null)
            throw new IllegalStateException("RouteContainer is not initialized");
        else
            return routeContainer.getBlockBlobReference(blobName).openInputStream();
    }
}
