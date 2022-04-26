package fi.sportti.app.constants;

public enum ExerciseType {
    WALKING("Walking"),
    RUNNING("Running"),
    BICYCLING("Bicycling"),
    YOGA("Yoga"),
    GOLF("Golf"),
    WEIGHT_LIFTING("Weight lifting"),
    FOOTBALL("Football"),
    TENNIS("Tennis"),
    BASKETBALL("Basketball"),
    SWIMMING("Swimming"),
    CROSSFIT("Crossfit"),
    DANCING("Dancing"),
    ZUMBA("Zumba"),
    SPINNING("Spinning"),
    OTHER("Other");

    /**
     * MET = https://en.wikipedia.org/wiki/Metabolic_equivalent_of_task
     * Generic MET calculations for given exercises
     */
    private final double metabolicEquivalentOfTask;
    private final String exerciseName;

    private ExerciseType(String exerciseName) {
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
