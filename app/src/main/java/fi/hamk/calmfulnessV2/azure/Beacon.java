package fi.hamk.calmfulnessV2.azure;

/**
 * Class for Beacon table in Azure
 */
public class Beacon {
    /**
     * Unique identifier of a beacon
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns the unique identifier of the beacon
     * @return id unique identifier of the beacon
     */
    public String getBeaconId() {
        return id;
    }

    /**
     * Set the unique identifier of the beacon.
     *
     * @param id Unique identifier of the beacon
     */
    public void setBeaconId(final String id) {
        this.id = id;
    }

    /**
     * Name of the beacon, helping identify different beacons
     */
    @com.google.gson.annotations.SerializedName("beaconName")
    private String beaconName;

    /**
     * @return Name of the beacon
     */
    public String getBeaconName() {
        return beaconName;
    }

    /**
     * Sets the name of the beacon
     * @param beaconName Name for the beacon
     */
    private void setBeaconName(final String beaconName) {
        this.beaconName = beaconName;
    }

    /**
     * MAC address of the beacon
     */
    @com.google.gson.annotations.SerializedName("beaconMac")
    private String beaconMac;

    /**
     * Returns the MAC address of the beacon
     * @return id MAC address of the beacon
     */
    public String getBeaconMac() {
        return beaconMac;
    }

    /**
     * Set the MAC address of the beacon.
     *
     * @param beaconMac MAC address of the beacon
     */
    private void setBeaconMac(final String beaconMac) {
        this.beaconMac = beaconMac;
    }

    /**
     * Class for Beacon table in Azure
     */
    public Beacon(){}

    /**
     * Class for Beacon table in Azure
     * @param beaconMac ID of a new beacon. Use the MAC address of the beacon.
     */
    public Beacon(final String beaconName, final String beaconMac){
        this.setBeaconName(beaconName);
        this.setBeaconMac(beaconMac);
    }
}
