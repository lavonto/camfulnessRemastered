package fi.hamk.calmfulnessV2.azure;


/**
 * Class for LocationExercise table in Azure
 */
public class LocationExercise {
    /**
     * Unique identifier of a exercise location
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns identifier of the exercise location
     *
     * @return Identifier of the exercise location
     */
    public String getId() {
        return id;
    }

    /**
     * Sets identifier of the exercise location
     *
     * @param id Identifier of the exercise location
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Unique identifier of a exercise
     */
    @com.google.gson.annotations.SerializedName("exercise")
    private String exercise;

    /**
     * Returns identifier of the exercise
     *
     * @return Identifier of the exercise
     */
    public String getExercise() {
        return exercise;
    }

    /**
     * Sets identifier of the exercise
     *
     * @param exercise Identifier of the exercise
     */
    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    /**
     * Uniquer identifier of a location
     */
    @com.google.gson.annotations.SerializedName("location")
    private String location;

    /**
     * Returns identifier of the location
     *
     * @return Identifier of the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets identifier of the location
     *
     * @param location Identifier of the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Default constructor of a ExerciseLocation
     */
    public LocationExercise() {
    }

    /**
     * Constructor of a ExerciseLocation
     *
     * @param id       Id of a exercise location
     * @param exercise Id of a exercise
     * @param location Id of a location
     */
    public LocationExercise(String id, String exercise, String location) {
        this.id = id;
        this.exercise = exercise;
        this.location = location;
    }
}
