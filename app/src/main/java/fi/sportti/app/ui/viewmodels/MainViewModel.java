package fi.sportti.app.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Future;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.SporttiDatabaseController;
import fi.sportti.app.datastorage.room.User;

public class MainViewModel extends AndroidViewModel {

    private final SporttiDatabaseController databaseController;
    private final LiveData<List<User>> listAllUsers;
    private final LiveData<List<Exercise>> listAllExercises;

    public MainViewModel(@NonNull Application application) {
        super(application);
        databaseController = new SporttiDatabaseController(application);
        listAllUsers = databaseController.getAllUsers();
        listAllExercises = databaseController.getAllExercises();
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



}
