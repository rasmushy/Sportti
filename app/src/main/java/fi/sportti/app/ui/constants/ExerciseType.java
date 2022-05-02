package fi.sportti.app.ui.constants;

/**
 * Enum for our Exercise type constants with MET values to use in calorie calculations
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */
public enum ExerciseType {
    BASKETBALL("Basketball"),
    BICYCLING("Bicycling"),
    CROSSFIT("Crossfit"),
    DANCING("Dancing"),
    FOOTBALL("Football"),
    GOLF("Golf"),
    OTHER("Other"),
    RUNNING("Running"),
    SPINNING("Spinning"),
    SWIMMING("Swimming"),
    TENNIS("Tennis"),
    WALKING("Walking"),
    WEIGHT_LIFTING("Weight lifting"),
    YOGA("Yoga"),
    ZUMBA("Zumba");

    /**
     * MET = https://en.wikipedia.org/wiki/Metabolic_equivalent_of_task
     * Generic MET calculations for given exercises
     */
    private final double metabolicEquivalentOfTask;
    private final String exerciseName;

    ExerciseType(String exerciseName) {
        this.exerciseName = exerciseName;
        switch (exerciseName) {
            case "Walking":
            case "Other":
                this.metabolicEquivalentOfTask = 3.0;
                break;
            case "Running":
            case "Zumba":
                this.metabolicEquivalentOfTask = 8.8;
                break;
            case "Bicycling":
            case "Dancing":
                this.metabolicEquivalentOfTask = 6.0;
                break;
            case "Yoga":
                this.metabolicEquivalentOfTask = 3.3;
                break;
            case "Golf":
                this.metabolicEquivalentOfTask = 2.0;
                break;
            case "Weight lifting":
            case "Tennis":
                this.metabolicEquivalentOfTask = 5.0;
                break;
            case "Football":
                this.metabolicEquivalentOfTask = 10.3;
                break;
            case "Basketball":
                this.metabolicEquivalentOfTask = 8.0;
                break;
            case "Swimming":
                this.metabolicEquivalentOfTask = 9.0;
                break;
            case "Crossfit":
                this.metabolicEquivalentOfTask = 5.6;
                break;
            case "Spinning":
                this.metabolicEquivalentOfTask = 8.9;
                break;
            default:
                this.metabolicEquivalentOfTask = 2.5;
        }
    }

    public double getMetabolicEquivalentOfTask() {
        return metabolicEquivalentOfTask;
    }

    public String getExerciseName() {
        return exerciseName;
    }
}
