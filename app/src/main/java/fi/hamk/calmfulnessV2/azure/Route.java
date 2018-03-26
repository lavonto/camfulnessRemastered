package fi.hamk.calmfulnessV2.azure;


public class Route {

    /**
     * Unique identifier of a route
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns identifier of the route
     * @return Identifier of the route
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the route
     * @param id Identifier of the route
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Unique name of a route in finnish
     */
    @com.google.gson.annotations.SerializedName("name_fi")
    private String nameFI;

    /**
     * Returns name of the route in finnish
     * @return Name of the route in finnish
     */
    public String getNameFI() {
        return nameFI;
    }

    /**
     * Sets name of the route in finnish
     * @param nameFI Name of the route in finnish
     */
    public void setNameFI(String nameFI) {
        this.nameFI = nameFI;
    }

    /**
     * Unique name of the route in english
     */
    @com.google.gson.annotations.SerializedName("name_en")
    private String nameEN;

    /**
     * Returns name of the route in english
     * @return Name of the route in english
     */
    public String getNameEN() {
        return nameEN;
    }

    /**
     * Sets name of the route in english
     * @param nameEN Name of the route in english
     */
    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    /**
     * URL of the route file
     */
    @com.google.gson.annotations.SerializedName("file")
    private String file;

    /**
     * Returns the URL to route file
     * @return URL of the route
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the URL of the route file
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
     * @param id Identifier of a route
     * @param nameFI Name of the route in finnish
     * @param nameEN Name of the route in english
     * @param file Identifier of the route
     */
    public Route(String id, String nameFI, String nameEN, String file) {
        this.id = id;
        this.nameFI = nameFI;
        this.nameEN = nameEN;
        this.file = file;
    }
}
