package fi.hamk.calmfulnessV2.azure;

/**
 * Class for Exercise table in Azure
 */
public class Exercise {
    /**
     * Unique identifier of a exercise
     */
    @com.google.gson.annotations.SerializedName("id")
    private String id;

    /**
     * Returns the ID of the exercise
     *
     * @return ID of the exercise
     */
    public String getExcerciseId() {
        return id;
    }

    /**
     * Sets the unique identifier of the exercise.
     *
     * @param id Unique identifier of the exercise.
     */
    private void setExerciseId(final String id) {
        this.id = id;
    }

    /**
     * Unique identifier of a exercise
     */
    @com.google.gson.annotations.SerializedName("exerciseTitle")
    private String exerciseTitle;

    /**
     * Returns the title the exercise
     *
     * @return Title of the exercise
     */
    public String getExerciseTitle() {
        return exerciseTitle;
    }

    /**
     * Sets the title of the exercise.
     *
     * @param exerciseTitle Title of the exercise.
     */
    private void setExerciseTitle(final String exerciseTitle) {
        this.exerciseTitle = exerciseTitle;
    }

    /**
     * Content of the exercise
     */
    @com.google.gson.annotations.SerializedName("exerciseContent")
    private String exerciseContent;

    /**
     * Returns the content of an exercise
     *
     * @return Content of an exercise
     */
    public String getExerciseContent() {
        return exerciseContent;
    }

    /**
     * Sets the content of an exercise
     *
     * @param exerciseContent Content of an exercise
     */
    private void setExerciseContent(final String exerciseContent) {
        this.exerciseContent = exerciseContent;
    }

    /**
     * Class for Exercise table in Azure
     * Empty constructor
     */
    Exercise() {
    }

    /**
     * Class for Exercise table in Azure
     *
     * @param id              ID of a new Exercise.
     * @param exerciseContent Content of a new Exercise
     * @param exerciseTitle   Title of a new Exercise
     */
    Exercise(final String id, final String exerciseTitle, final String exerciseContent) {
        this.setExerciseId(id);
        this.setExerciseContent(exerciseContent);
        this.setExerciseTitle(exerciseTitle);
    }
}
