package fi.sportti.app.datastorage.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class for user
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@Entity(tableName = "user_data")
public class User {
    /**
     * Default values for user in constructor.
     * These values are purely magic numbers that are not based on anything.
     * This allows user to look around in app without adding any personal information.
     */
    public User() {
        this.userName = "";
        this.gender = "Male";
        this.height = 170;
        this.weight = 70;
        this.weeklyGoalMinute = 0;
        this.weeklyGoalHour = 2;
        this.age = 52;
        this.restHeartRate = 0;
        this.maxHeartRate = 172; // Max HeartRate calculated from age 208-(0,7 * 52) = 171,6
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid", index = true)
    protected int uid;

    @ColumnInfo(name = "username", index = true)
    private String userName;

    @ColumnInfo(name = "maxHeartRate")
    private int maxHeartRate;
    @ColumnInfo(name = "restHeartRate")
    private int restHeartRate;
    @ColumnInfo(name = "gender", index = true)
    private String gender;
    @ColumnInfo(name = "weight", index = true)
    private int weight;
    @ColumnInfo(name = "height", index = true)
    private int height;
    @ColumnInfo(name = "age", index = true)
    private int age;
    @ColumnInfo(name = "weeklyGoalMinute", index = true)
    private int weeklyGoalMinute;
    @ColumnInfo(name = "weeklyGoalHour", index = true)
    private int weeklyGoalHour;

    //Setters & Getters for user
    public int getuid() {
        return this.uid;
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

    public void setAge(int age) {
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

    public int getAge() {
        return age;
    }

    public int getWeeklyGoalMinute() {
        return weeklyGoalMinute;
    }

    public int getWeeklyGoalHour() {
        return weeklyGoalHour;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRestHeartRate() {
        return restHeartRate;
    }

    public void setRestHeartRate(int restHeartRate) {
        this.restHeartRate = restHeartRate;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", userName='" + userName + '\'' +
                ", maxHeartRate=" + maxHeartRate +
                ", restHeartRate=" + restHeartRate +
                ", gender='" + gender + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", age=" + age +
                ", weeklyGoalMinute=" + weeklyGoalMinute +
                ", weeklyGoalHour=" + weeklyGoalHour +
                '}';
    }
}
