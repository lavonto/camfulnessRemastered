package fi.hamk.calmfulnessV2.azure;

/**
 * Class for Route table in Azure
 */
public class Route {

    /**
     * Unique identifier of a route
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns identifier of the route
     *
     * @return Identifier of the route
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the route
     *
     * @param id Identifier of the route
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Unique name of a route in finnish
     */
    @com.google.gson.annotations.SerializedName("nameFi")
    private String nameFi;

    /**
     * Returns name of the route in finnish
     *
     * @return Name of the route in finnish
     */
    public String getNameFi() {
        return nameFi;
    }

    /**
     * Sets name of the route in finnish
     *
     * @param nameFi Name of the route in finnish
     */
    public void setNameFi(String nameFi) {
        this.nameFi = nameFi;
    }

    /**
     * Unique name of the route in english
     */
    @com.google.gson.annotations.SerializedName("nameEn")
    private String nameEn;

    /**
     * Returns name of the route in english
     *
     * @return Name of the route in english
     */
    public String getNameEn() {
        return nameEn;
    }

    /**
     * Sets name of the route in english
     *
     * @param nameEn Name of the route in english
     */
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    /**
     * URL of the route file
     */
    @com.google.gson.annotations.SerializedName("file")
    private String file;

    /**
     * Returns the URL to route file
     *
     * @return URL of the route
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the URL of the route file
     *
     * @param file URL of the route file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Default constructor of a Route
     */
    public Route() {
    }

    /**
     * Constructor of a route
     *
     * @param id     Identifier of a route
     * @param nameFi Name of the route in finnish
     * @param nameEn Name of the route in english
     * @param file   Identifier of the route
     */
    public Route(String id, String nameFi, String nameEn, String file) {
        this.id = id;
        this.nameFi = nameFi;
        this.nameEn = nameEn;
        this.file = file;
    }
}
