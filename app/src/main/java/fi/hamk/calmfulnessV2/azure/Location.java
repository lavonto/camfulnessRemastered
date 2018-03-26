package fi.hamk.calmfulnessV2.azure;


public class Location {

    /**
     * Unique identifier to a location
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns identifier of the location
     *
     * @return Identifier of the location
     */
    public String getId() {
        return id;
    }

    /**
     * Sets identifier of the location
     *
     * @param id Identifier of the location
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Unique identifier of a latitude of the location
     */
    @com.google.gson.annotations.SerializedName("lat")
    private float lat;

    /**
     * Returns the latitude of the location
     *
     * @return Latitude of the location
     */
    public float getLat() {
        return lat;
    }

    /**
     * Sets a latitude of the location
     *
     * @param lat Latitude of the location
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     * Unique identifier of a longitude of the location
     */
    @com.google.gson.annotations.SerializedName("lon")
    private float lon;

    /**
     * Returns the longitude of the location
     *
     * @return Longitude of the location
     */
    public float getLon() {
        return lon;
    }

    /**
     * Sets a longitude of the location
     *
     * @param lon
     */
    public void setLon(float lon) {
        this.lon = lon;
    }

    /**
     * Unique identifier of a impact range of the location
     */
    @com.google.gson.annotations.SerializedName("impactRange")
    private short impactRange;

    /**
     * Returns the impact range of the location
     *
     * @return Impact range of the location
     */
    public short getImpactRange() {
        return impactRange;
    }

    /**
     * Sets a impact range of the location
     *
     * @param impactRange
     */
    public void setImpactRange(short impactRange) {
        this.impactRange = impactRange;
    }

    /**
     * Default constructor of a Location
     */
    public Location() {
    }

    /**
     * Constructor of a Location
     *
     * @param id          Identifier of the location
     * @param lat         Latitude of the location
     * @param lon         Longitude of the location
     * @param impactRange Impact range of the location
     */
    public Location(String id, float lat, float lon, short impactRange) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.impactRange = impactRange;
    }
}
