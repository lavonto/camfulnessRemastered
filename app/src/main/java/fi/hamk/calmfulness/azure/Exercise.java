package fi.hamk.calmfulness.azure;

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
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the exercise location.
     *
     * @param id Unique identifier of the exercise location.
     */
    private void setId(final String id) {
        this.id = id;
    }

    /**
     * Unique identifier of a exercise title in finnish
     */
    @com.google.gson.annotations.SerializedName("titleFi")
    private String titleFi;

    /**
     * Returns the ID of the title of the exercise in finnish
     *
     * @return title of the exercise in finnish
     */
    public String getTitleFi() {
        return titleFi;
    }

    /**
     * Sets the unique identifier of the finnish exercise title.
     *
     * @param titleFi title of the exercise in finnish
     */
    private void setTitleFi(final String titleFi) {
        this.titleFi = titleFi;
    }

    /**
     * Unique identifier of a exercise title in english
     */
    @com.google.gson.annotations.SerializedName("titleEn")
    private String titleEn;

    /**
     * Returns the ID of the english exercise title
     *
     * @return title of the exercise in english
     */
    public String getTitleEn() {
        return titleEn;
    }

    /**
     * Sets the unique identifier of the english exercise title.
     *
     * @param titleEn title of the exercise in english
     */
    private void setTitleEn(final String titleEn) {
        this.titleEn = titleEn;
    }

    /**
     * Unique identifier of a exercise text in finnish
     */
    @com.google.gson.annotations.SerializedName("textFi")
    private String textFi;

    /**
     * Returns the ID of the finnish exercise text
     *
     * @return textual content of the exercise in finnish
     */
    public String getTextFi() {
        return textFi;
    }

    /**
     * Sets the unique identifier of the finnish exercise text
     *
     * @param textFi textual content of the exercise in finnish
     */
    private void setTextFi(final String textFi) {
        this.textFi = textFi;
    }

    /**
     * Unique identifier of a exercise text in english
     */
    @com.google.gson.annotations.SerializedName("textEn")
    private String textEn;

    /**
     * Returns the ID of the english exercise text
     *
     * @return textual content of the exercise in english
     */
    public String getTextEn() {
        return textEn;
    }

    /**
     * Sets the unique identifier of the english exercise text
     *
     * @param textEn textual content of the exercise in english
     */
    private void setTextEn(final String textEn) {
        this.textEn = textEn;
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
     * Unique identifier of a videoId
     */
    @com.google.gson.annotations.SerializedName("videoId")
    private String videoId;

    /**
     * Returns the ID of the videoId
     *
     * @return URL of the videoId
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * Sets the unique identifier of the videoId
     *
     * @param videoId URL of the videoId
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
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
     * @param titleFi Title of the exercise
     * @param titleEn Title of the exercise
     * @param textFi  Textual content of the exercise in finnish
     * @param textEn  Textual content of the exercise in english
     * @param pictureUrl URL of a pictureUrl
     * @param videoId   URL of a videoId
     */
    public Exercise(String id, String titleFi, String titleEn, String textFi, String textEn, String pictureUrl, String videoId) {
        this.id = id;
        this.titleFi = titleFi;
        this.titleEn = titleEn;
        this.textFi = textFi;
        this.textEn = textEn;
        this.pictureUrl = pictureUrl;
        this.videoId = videoId;
    }
}
