package fi.sportti.app.datastorage.room;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Rasmus Hyyppä
 * Database controller with Dao commands
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class SporttiDatabaseController {
    private UserDao userDao;
    private ExerciseDao exerciseDao;
    private LiveData<List<User>> listAllUsers;
    private LiveData<List<Exercise>> listAllExercises;


    public SporttiDatabaseController(Application application) {
        SporttiDatabase db = SporttiDatabase.getDatabase(application);
        userDao = db.userDao();
        exerciseDao = db.exerciseDao();
        listAllUsers = userDao.getAllUsers();
        listAllExercises = exerciseDao.getAllExercises();
    }

    //Query where we get every single user from room database
    public LiveData<List<User>> getAllUsers() {
        return this.listAllUsers;
    }

    public void updateUser(User user) {
        SporttiDatabase.executor.execute(() -> userDao.updateUser(user));
    }

    public void insertUser(User newUser) {
        SporttiDatabase.executor.execute(() -> userDao.insertUser(newUser));
    }

    public void deleteUser(User user) {
        SporttiDatabase.executor.execute(() -> userDao.deleteUser(user));
    }

    public void deleteListOfUsers(List<User> userList) {
        SporttiDatabase.executor.execute(() -> userDao.deleteListOfUsers(userList));
    }

    //Query for single user by their username
    public Future<User> findByName(String userName) {
        return SporttiDatabase.executor.submit(() -> userDao.findByName(userName));
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

    //Query with userId for one LiveData<User>
    public void findByUserId(int userId) {
        SporttiDatabase.executor.execute(() -> userDao.findByUserId(userId));
    }

    //Query to get all exercises
    public LiveData<List<Exercise>> getAllExercises() {
        return this.listAllExercises;
    }

    //Query from where we take user id into account
    public void getAllExercisesById(int currentUser) {
        SporttiDatabase.executor.execute(() -> exerciseDao.getAllExercisesById(currentUser));
    }

    // Query between two times, this can be useful if user wants to look exercises between certain dates
    public Future<List<Exercise>> getExercisesByTimeFrame(long fromDate, long toDate) {
        return SporttiDatabase.executor.submit(() -> exerciseDao.getExercisesByTimeFrame(fromDate, toDate));
    }

    public void updateExercise(Exercise updatedExercise) {
        SporttiDatabase.executor.execute(() -> exerciseDao.updateExercise(updatedExercise));
    }

    public void updateAllExercises(List<Exercise> exerciseList) {
        SporttiDatabase.executor.execute(() -> exerciseDao.updateAllExercises(exerciseList));
    }

    public void insertExercise(Exercise newExercise) {
        SporttiDatabase.executor.execute(() -> exerciseDao.insertExercise(newExercise));
    }

    public void insertExercisesFromList(List<Exercise> exerciseList) {
        SporttiDatabase.executor.execute(() -> exerciseDao.insertExercisesFromList(exerciseList));
    }

    public void deleteExercise(Exercise uselessExercise) {
        SporttiDatabase.executor.execute(() -> exerciseDao.deleteExercise(uselessExercise));
    }

    public void deleteAllExercises() {
        SporttiDatabase.executor.execute(() -> exerciseDao.deleteAllExercises());
    }

}




