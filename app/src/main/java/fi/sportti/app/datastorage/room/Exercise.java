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

/*
 * @author rasmushy
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

//    @ColumnInfo(name = "distance")
//    private double distance;

    public Exercise(String sportType, int userId, ZonedDateTime startDate, ZonedDateTime endDate, int calories) {
        this.sportType = sportType;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.calories = calories;
//        this.distance = distance;
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

    public void setUserId(int userId) {
        this.userId = userId;
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


    public long getDurationInMinutes(){
        return ChronoUnit.MINUTES.between(startDate, endDate);
    }
}
