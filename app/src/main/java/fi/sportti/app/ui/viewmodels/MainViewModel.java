package fi.sportti.app.ui.viewmodels;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.SporttiDatabaseController;
import fi.sportti.app.datastorage.room.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends AndroidViewModel {
    public static final String TAG = "testailua";
    public static final int DAILY_HOURS = 1;
    public static final int MONTHLY_HOURS = 2;
    private final SporttiDatabaseController databaseController;
    private final LiveData<List<User>> listAllUsers;
    private final LiveData<List<Exercise>> listAllExercises;


    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseController = new SporttiDatabaseController(application);
        listAllUsers = databaseController.getAllUsers();
        listAllExercises = databaseController.getAllExercises();
        Log.d(TAG, "MainViewModel created.");
    }

    //Query where we get LiveData<List<User> from database
    public LiveData<List<User>> getAllUsers() {
        return this.listAllUsers;
    }

    //Query with userId for one LiveData<User>
    public void findByUserId(int userId) {
        databaseController.findByUserId(userId);
    }

    //Query for first user of the database (used for initial setup)
    public User getFirstUser() {
        return databaseController.getFirstUser();
    }

    //Query for single user by their username in a form of Future<User>
    public Future<User> findByName(String userName) {
        return databaseController.findByName(userName);
    }

    public void updateUser(User user) {
        databaseController.updateUser(user);
    }

    public void insertUser(User newUser) {
        databaseController.insertUser(newUser);
    }


    public void deleteUser(User user) {
        databaseController.deleteUser(user);
    }

    public void deleteListOfUsers(List<User> userList) {
        databaseController.deleteListOfUsers(userList);
    }

    //Query to get all exercises in LiveData
    public LiveData<List<Exercise>> getAllExercises() {
        return this.listAllExercises;
    }

    //Query from where we take user id into account and return LiveData<List<Exercises>>
    public void getAllExercisesById(int currentUser) {
        databaseController.getAllExercisesById(currentUser);
    }

    // Query between two times, this can be useful if user wants to look exercises between certain dates
    public Future<List<Exercise>> getExercisesByTimeFrame(long fromDate, long toDate) {
        return databaseController.getExercisesByTimeFrame(fromDate, toDate);
    }

    public void updateExercise(Exercise updatedExercise) {
        databaseController.updateExercise(updatedExercise);
    }

    public void updateAllExercises(List<Exercise> exerciseList) {
        databaseController.updateAllExercises(exerciseList);
    }

    public void insertExercise(Exercise newExercise) {
        databaseController.insertExercise(newExercise);
    }

    public void insertExercisesFromList(List<Exercise> exerciseList) {
        databaseController.insertExercisesFromList(exerciseList);
    }

    public void deleteExercise(Exercise uselessExercise) {
        databaseController.deleteExercise(uselessExercise);
    }

    public void deleteAllExercises(){
        databaseController.deleteAllExercises();
    }



    public HashMap<ZonedDateTime, Long> getHoursForGraph(int type){
        List<Exercise> list = listAllExercises.getValue();
        HashMap<ZonedDateTime, Long> result = new HashMap<>();
        if(list != null){
            long hours;
            ZonedDateTime newDate;
            int day = 0;
            int month = 0;
            int year = 0;
            ZoneId zone = ZoneId.systemDefault();
            for(Exercise e : list){
                hours = e.getDuration();
                day = e.getStartDate().getDayOfMonth();
                month = e.getStartDate().getMonthValue();
                year = e.getStartDate().getYear();
                if(type == DAILY_HOURS){
                    newDate = ZonedDateTime.of(year, month, day, 12, 0, 0, 0, zone);
                }
                else {
                    newDate = ZonedDateTime.of(year, month, 1, 12, 0, 0, 0, zone);
                }
                if (result.containsKey(newDate)) {
                    long value = result.get(newDate);
                    value += hours;
                    result.replace(newDate, value);
                } else {
                    result.put(newDate, hours);
                }
            }
        }
        return result;
    }



}
