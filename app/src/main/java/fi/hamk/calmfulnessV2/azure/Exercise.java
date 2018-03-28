package fi.hamk.calmfulnessV2.azure;

/**
 * Class for Exercise table in Azure
 */
public class Exercise {

    /**
     * Unique identifier of a exercise location
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns the ID of the exercise location
     *
     * @return ID of the exercise location
     */
    public String getExcerciseId() {
        return id;
    }

    /**
     * Sets the unique identifier of the exercise location.
     *
     * @param id Unique identifier of the exercise location.
     */
    private void setExerciseId(final String id) {
        this.id = id;
    }

    /**
     * Unique identifier of a exercise title in finnish
     */
    @com.google.gson.annotations.SerializedName("titleFi")
    private String titleFI;

    /**
     * Returns the ID of the title of the exercise in finnish
     *
     * @return title of the exercise in finnish
     */
    public String getTitleFI() {
        return titleFI;
    }

    /**
     * Sets the unique identifier of the finnish exercise title.
     *
     * @param titleFI title of the exercise in finnish
     */
    private void setTitleFI(final String titleFI) {
        this.titleFI = titleFI;
    }

    /**
     * Unique identifier of a exercise title in english
     */
    @com.google.gson.annotations.SerializedName("titleEn")
    private String titleEN;

    /**
     * Returns the ID of the english exercise title
     *
     * @return title of the exercise in english
     */
    public String getTitleEN() {
        return titleEN;
    }

    /**
     * Sets the unique identifier of the english exercise title.
     *
     * @param titleEN title of the exercise in english
     */
    private void setTitleEN(final String titleEN) {
        this.titleEN = titleEN;
    }

    /**
     * Unique identifier of a exercise text in finnish
     */
    @com.google.gson.annotations.SerializedName("textFi")
    private String textFI;

    /**
     * Returns the ID of the finnish exercise text
     *
     * @return textual content of the exercise in finnish
     */
    public String getTextFI() {
        return textFI;
    }

    /**
     * Sets the unique identifier of the finnish exercise text
     *
     * @param textFI textual content of the exercise in finnish
     */
    private void setTextFI(final String textFI) {
        this.textFI = textFI;
    }

    /**
     * Unique identifier of a exercise text in english
     */
    @com.google.gson.annotations.SerializedName("textEn")
    private String textEN;

    /**
     * Returns the ID of the english exercise text
     *
     * @return textual content of the exercise in english
     */
    public String getTextEN() {
        return textEN;
    }

    /**
     * Sets the unique identifier of the english exercise text
     *
     * @param textEN textual content of the exercise in english
     */
    private void setTextEN(final String textEN) {
        this.textEN = textEN;
    }

    /**
     * Unique identifier of a pictureUrl
     */
    @com.google.gson.annotations.SerializedName("pictureUrl")
    private String pictureUrl;

    /**
     * Returns the ID of the pictureUrl
     *
     * @return URL of the pictureUrl
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * Sets the unique identifier of the pictureUrl
     *
     * @param pictureUrl URL of the pictureUrl
     */
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    /**
     * Unique identifier of a videoUrl
     */
    @com.google.gson.annotations.SerializedName("videoUrl")
    private String videoUrl;

    /**
     * Returns the ID of the videoUrl
     *
     * @return URL of the videoUrl
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Sets the unique identifier of the videoUrl
     *
     * @param videoUrl URL of the videoUrl
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * Default constructor of a exercise
     */
    Exercise() {
    }

    /**
     * Constructor of Exercise
     *
     * @param id      Unique identifier of a exercise
     * @param titleFI Title of the exercise
     * @param titleEN Title of the exercise
     * @param textFI  Textual content of the exercise in finnish
     * @param textEN  Textual content of the exercise in english
     * @param pictureUrl URL of a pictureUrl
     * @param videoUrl   URL of a videoUrl
     */
    public Exercise(String id, String titleFI, String titleEN, String textFI, String textEN, String pictureUrl, String videoUrl) {
        this.id = id;
        this.titleFI = titleFI;
        this.titleEN = titleEN;
        this.textFI = textFI;
        this.textEN = textEN;
        this.pictureUrl = pictureUrl;
        this.videoUrl = videoUrl;
    }
}
