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
    @com.google.gson.annotations.SerializedName("title_fi")
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
    @com.google.gson.annotations.SerializedName("title_en")
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
    @com.google.gson.annotations.SerializedName("text_fi")
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
    @com.google.gson.annotations.SerializedName("text_en")
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
     * Unique identifier of a picture
     */
    @com.google.gson.annotations.SerializedName("picture")
    private String picture;

    /**
     * Returns the ID of the picture
     *
     * @return URL of the picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Sets the unique identifier of the picture
     *
     * @param picture URL of the picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Unique identifier of a video
     */
    @com.google.gson.annotations.SerializedName("video")
    private String video;

    /**
     * Returns the ID of the video
     *
     * @return URL of the video
     */
    public String getVideo() {
        return video;
    }

    /**
     * Sets the unique identifier of the video
     *
     * @param video URL of the video
     */
    public void setVideo(String video) {
        this.video = video;
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
     * @param picture URL of a picture
     * @param video   URL of a video
     */
    public Exercise(String id, String titleFI, String titleEN, String textFI, String textEN, String picture, String video) {
        this.id = id;
        this.titleFI = titleFI;
        this.titleEN = titleEN;
        this.textFI = textFI;
        this.textEN = textEN;
        this.picture = picture;
        this.video = video;
    }
}
