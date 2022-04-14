package fi.sportti.app.datastorage.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.ZoneId;
import java.util.Date;

/*
 * @author rasmushy
 * Entity class for user
 */

@Entity(tableName = "user_data")
public class User {

    /*
     * @author Rasmus Hyyppä
     * Default values for user in constructor. This allows user to look around in app without
     * adding any personal information.
     */

    public User() {
        this.userName = "username";
        this.height = 170;
        this.weight = 70;
        this.weeklyGoalMinute = 0;
        this.weeklyGoalHour = 2;
        this.age = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid", index = true)
    protected int uid;

    @ColumnInfo(name = "username", index = true)
    private String userName;

    @ColumnInfo(name = "weight", index = true)
    private int weight;
    @ColumnInfo(name = "height", index = true)
    private int height;
    @ColumnInfo(name = "age", index = true)
    private Date age;
    @ColumnInfo(name = "weeklyGoalMinute", index = true)
    private int weeklyGoalMinute;
    @ColumnInfo(name = "weeklyGoalHour", index = true)
    private int weeklyGoalHour;

    //Setters & Getters for user
    public int getuid() {
        return this.uid;
    }

    public void setuid(int uId) {
        this.uid = uId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public void setWeeklyGoalMinute(int weeklyGoalMinute) {
        this.weeklyGoalMinute = weeklyGoalMinute;
    }

    public void setWeeklyGoalHour(int weeklyGoalHour) {
        this.weeklyGoalHour = weeklyGoalHour;
    }

    public String getUserName() {
        return userName;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public Date getAge() {
        return age;
    }

    public int getWeeklyGoalMinute() {
        return weeklyGoalMinute;
    }

    public int getWeeklyGoalHour() {
        return weeklyGoalHour;
    }

}
