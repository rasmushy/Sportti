package fi.sportti.app.datastorage.room;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Database controller with commands from our DAO's
 * Android Developer: https://github.com/googlecodelabs/android-room-with-a-view/tree/master/app/src/main/java/com/example/android/roomwordssample
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class SporttiDatabaseController {
    private UserDao userDao;
    private ExerciseDao exerciseDao;
    private LiveData<List<Exercise>> listAllExercises;


    public SporttiDatabaseController(Application application) {
        SporttiDatabase db = SporttiDatabase.getDatabase(application);
        userDao = db.userDao();
        exerciseDao = db.exerciseDao();
        listAllExercises = exerciseDao.getAllExercises();
    }

    public void updateUser(User user) {
        SporttiDatabase.executor.execute(() -> userDao.updateUser(user));
    }

    public void insertUser(User newUser) {
        SporttiDatabase.executor.execute(() -> userDao.insertUser(newUser));
    }

    //Query for first user of the database (used for initial setup)
    public User getFirstUser() {
        User user = new User();
        try {
            user = (User) SporttiDatabase.executor.submit(() -> userDao.getFirstUser()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }

    //Query to get all exercises
    public LiveData<List<Exercise>> getAllExercises() {
        return this.listAllExercises;
    }

    public void insertExercise(Exercise newExercise) {
        SporttiDatabase.executor.execute(() -> exerciseDao.insertExercise(newExercise));
    }

    public void deleteExercise(Exercise uselessExercise) {
        SporttiDatabase.executor.execute(() -> exerciseDao.deleteExercise(uselessExercise));
    }

}




