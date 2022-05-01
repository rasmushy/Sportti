package fi.sportti.app.ui.viewmodels;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.SporttiDatabaseController;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.utilities.TimeConversionUtilities;

/**
 * Main view model to distance our database from the ui.
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends AndroidViewModel {
    public static final String TAG = "TESTI";
    /** Constant variable used to tell how you want exercise times to be summed up in HashMap.*/
    public static final int DAILY_MINUTES = 1;
    /** Constant variable used to tell how you want exercise times to be summed up in HashMap.*/
    public static final int MONTHLY_MINUTES = 2;

    private final SporttiDatabaseController databaseController;
    private final LiveData<List<User>> listAllUsers;
    private final LiveData<List<Exercise>> listAllExercises;
    private boolean exerciseListIsSorted;


    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseController = new SporttiDatabaseController(application);
        listAllUsers = databaseController.getAllUsers();
        listAllExercises = databaseController.getAllExercises();
        exerciseListIsSorted = false;
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
        exerciseListIsSorted = false;
    }

    public void insertExercisesFromList(List<Exercise> exerciseList) {
        databaseController.insertExercisesFromList(exerciseList);
    }

    public void deleteExercise(Exercise uselessExercise) {
        databaseController.deleteExercise(uselessExercise);
    }

    public void deleteAllExercises() {
        databaseController.deleteAllExercises();
    }


    /**
     * Go through all exercises and sum up total exercise time of each day.
     * @param type Use constants in this class.
     *             DAILY_MINUTES if you want to get total exercise time of each day.
     *             MONTHLY_MINUTES if you want to get total exercise time of each month.
     * @return dataMap
     * @author Jukka-Pekka Jaakkola
     */
    public HashMap<ZonedDateTime, Integer> getExerciseTimesForGraph(int type) {
        List<Exercise> list = listAllExercises.getValue();
        HashMap<ZonedDateTime, Integer> dataMap = new HashMap<>();
        if (list != null) {
            int minutes;
            ZonedDateTime keyDate;

            for (Exercise exercise : list) {
                minutes = exercise.getDurationInMinutes();
                keyDate = getKeyDate(exercise, type);
                if (dataMap.containsKey(keyDate)) {
                    int totalTime = dataMap.get(keyDate);
                    totalTime += minutes;
                    dataMap.replace(keyDate, totalTime);
                }
                else {
                    dataMap.put(keyDate, minutes);
                }
            }
        }
        return dataMap;
    }

    /**
     * Returns exercises sorted by date. From most recent to oldest.
     * @return list
     * @author Jukka-Pekka Jaakkola
     */
    public List<Exercise> getSortedExerciseList(){
        if(!exerciseListIsSorted && listAllExercises.getValue() != null){
            listAllExercises.getValue().sort(new Comparator<Exercise>() {
                @Override
                public int compare(Exercise exercise, Exercise t1) {
                    return t1.getStartDate().compareTo(exercise.getStartDate());
                }
            });       
            exerciseListIsSorted = true;
        }
        return listAllExercises.getValue();
    }

    /**
     * Returns total exercise times of each day of current week.
     * @return exerciseTimeInMinutes
     * @author Jukka-Pekka Jaakkola
     */
    public int getExerciseTimeForThisWeek(){
        HashMap<ZonedDateTime, Integer> dataMap = getExerciseTimesForGraph(DAILY_MINUTES);

        ZonedDateTime firstDayOfWeek = TimeConversionUtilities.getFirstDayOfWeek();
        ZonedDateTime keyDate;
        int exerciseTimeInMinutes = 0;
        if (dataMap != null) {
            for (int i = 0; i < 7; i++) {
                keyDate = firstDayOfWeek.plusDays(i);
                if (dataMap.containsKey(keyDate)) {
                    exerciseTimeInMinutes += dataMap.get(keyDate);
                }
            }
        }
        return exerciseTimeInMinutes;
    }

    //Returns ZonedDateTime object with default times which is used as key in HashMaps where exercise times are added.
    private ZonedDateTime getKeyDate(Exercise exercise, int type) {
        ZonedDateTime keyDate = ZonedDateTime.now(); //Just initialize keyDate with some value.
        ZonedDateTime startDate = exercise.getStartDate();
        if (type == DAILY_MINUTES) {
            keyDate = TimeConversionUtilities.getDateWithDefaultTime(startDate);
        }
        else if(type == MONTHLY_MINUTES) {
            //When exercise times are summed up to months, key value for each month is first day of month.
            keyDate = TimeConversionUtilities.getFirstDayOfMonth(startDate);
        }
        return keyDate;
    }


}
