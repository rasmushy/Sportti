package fi.sportti.app.ui.viewmodels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.SporttiDatabaseController;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.utilities.TimeConversionUtilities;

/**
 * Main view model to distance our database from the ui.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends AndroidViewModel {
    /**
     * Constant variable used to tell how you want exercise times to be summed up in HashMap.
     */
    public static final int DAILY_MINUTES = 1;
    /**
     * Constant variable used to tell how you want exercise times to be summed up in HashMap.
     */
    public static final int MONTHLY_MINUTES = 2;

    private final SporttiDatabaseController databaseController;
    private final LiveData<List<Exercise>> listAllExercises;
    private boolean exerciseListIsSorted;


    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseController = new SporttiDatabaseController(application);
        listAllExercises = databaseController.getAllExercises();
        exerciseListIsSorted = false;
    }

    //Query for first user of the database (used for initial setup)
    public User getFirstUser() {
        return databaseController.getFirstUser();
    }

    public void updateUser(User user) {
        databaseController.updateUser(user);
    }

    public void insertUser(User newUser) {
        databaseController.insertUser(newUser);
    }

    //Query to get all exercises in LiveData
    public LiveData<List<Exercise>> getAllExercises() {
        return this.listAllExercises;
    }


    public void insertExercise(Exercise newExercise) {
        databaseController.insertExercise(newExercise);
        exerciseListIsSorted = false;
    }

    public void deleteExercise(Exercise uselessExercise) {
        databaseController.deleteExercise(uselessExercise);
    }


    /**
     * Go through all exercises and sum up total exercise time of each day.
     *
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
                } else {
                    dataMap.put(keyDate, minutes);
                }
            }
        }
        return dataMap;
    }

    /**
     * Returns exercises sorted by date. From most recent to oldest.
     *
     * @return list
     * @author Jukka-Pekka Jaakkola
     */
    public List<Exercise> getSortedExerciseList() {
        if (!exerciseListIsSorted && listAllExercises.getValue() != null) {
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
     *
     * @return exerciseTimeInMinutes
     * @author Jukka-Pekka Jaakkola
     */
    public int getExerciseTimeForThisWeek() {
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
        } else if (type == MONTHLY_MINUTES) {
            //When exercise times are summed up to months, key value for each month is first day of month.
            keyDate = TimeConversionUtilities.getFirstDayOfMonth(startDate);
        }
        return keyDate;
    }


}
