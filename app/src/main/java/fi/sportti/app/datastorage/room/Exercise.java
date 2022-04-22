package fi.sportti.app.datastorage.room;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author rasmushy
 * @version 0.1
 * Entity class for User created Exercises
 */

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(tableName = "exercise_data", foreignKeys = @ForeignKey(entity = User.class, parentColumns = "uid", childColumns = "userId", onDelete = CASCADE, onUpdate = CASCADE))
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exerciseId", index = true)
    protected int exerciseId;

    @ColumnInfo(name = "userId", index = true)
    protected int userId;

    @ColumnInfo(name = "sportType", index = true)
    private String sportType;

    @ColumnInfo(name = "startDate", index = true)
    private ZonedDateTime startDate;

    @ColumnInfo(name = "endDate", index = true)
    private ZonedDateTime endDate;

    @ColumnInfo(name = "calories", index = true)
    private int calories;

    @ColumnInfo(name = "avgHeartRate", index = true)
    private int avgHeartRate;

    @ColumnInfo(name = "route", index = true)
    private String route;

    @ColumnInfo(name = "distance", index = true)
    private double distance;

    @ColumnInfo(name = "comment", index = true)
    private String comment;

    public Exercise(String sportType, int userId, ZonedDateTime startDate, ZonedDateTime endDate, int calories, int avgHeartRate, String route, double distance, String comment) {
        this.sportType = sportType;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.calories = calories;
        this.avgHeartRate = avgHeartRate;
        this.route = route;
        this.distance = distance;
        this.comment = comment;
    }

    //Getters & Setters for Exercise entities
    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getUserId() {
        return userId;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDurationInMinutes() {
        return (int) ChronoUnit.MINUTES.between(startDate, endDate);
    }
}
