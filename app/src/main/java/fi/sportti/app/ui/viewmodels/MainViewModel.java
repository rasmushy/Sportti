package fi.sportti.app.ui.viewmodels;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.SporttiDatabaseController;
import fi.sportti.app.datastorage.room.User;

/**
 * Main view model to distance our database from the ui.
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends AndroidViewModel {
    public static final String TAG = "TESTI";
    public static final int DAILY_MINUTES = 1;
    public static final int MONTHLY_MINUTES = 2;
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

    public void deleteAllExercises() {
        databaseController.deleteAllExercises();
    }


    /**
     * @author Jukka-Pekka Jaakkola
     */
    //Go through all exercises and sum up total exercise time of each day.
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
                } else {
                    dataMap.put(keyDate, minutes);
                }
            }

        }
        return dataMap;
    }

    public List<Exercise> getSortedExerciseList(){
        List<Exercise> listToReturn = listAllExercises.getValue();
        listToReturn.sort(new Comparator<Exercise>() {
            @Override
            public int compare(Exercise exercise, Exercise t1) {
                return t1.getStartDate().compareTo(exercise.getStartDate());
            }
        });
        return listToReturn;
    }

    private ZonedDateTime getKeyDate(Exercise exercise, int type) {
        ZonedDateTime keyDate;

        ZonedDateTime startDate = exercise.getStartDate();
        int day = startDate.getDayOfMonth();
        int month = startDate.getMonthValue();
        int year = startDate.getYear();
        ZoneId zone = ZoneId.systemDefault();
        //Set time to 12:00:00 always so these dates can be found from HashMap.
        if (type == DAILY_MINUTES) {
            keyDate = ZonedDateTime.of(year, month, day, 12, 0, 0, 0, zone);
        } else {
            keyDate = ZonedDateTime.of(year, month, 1, 12, 0, 0, 0, zone);
        }
        return keyDate;
    }


}
