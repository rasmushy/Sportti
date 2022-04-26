/*
 * Sportti, android application for fitness tracking and more.
 */


package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/*
 * @author rasmushy, lassib
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // TAG for Log.d

    private static MainViewModel mainViewModel;

    private User user;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = new ArrayList<>();
        //Setup our access to database
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getAllUsers().observe(this, users -> userList = users);
        Log.d(TAG, "onCreate() launched");
        initialStartUp();
    }

    /*
     * @author Rasmus Hyyppä
     * Initial startup method to check do we have user that has saved personal information
     * If not, we create blank user, that can visit Application's activities freely.
     */
    private void initialStartUp() {
        Log.d(TAG, "initialStartUp() launched");
        if (mainViewModel.getFirstUser() != null) {
            Log.d(TAG, "initialStartUp() if statement not null");
            Log.d(TAG, "initialStartup() userList value: " + userList.toString());
            //Lets make another user and compare it to our first user in db
            User plainUser = new User();
            //Get our db's first user
            user = mainViewModel.getFirstUser();
            //If they are not equal then we have user that added personal info's
            if (!user.equals(plainUser)) {
                Log.d(TAG, "InitialStartUp() welcomes user, hi!");
                //TODO: Welcome our user
            }
            return;
        }
        // Insert blank user
        user = new User();
        mainViewModel.insertUser(user);
    }

    public static MainViewModel getMainViewModel() {
        return mainViewModel;
    }


    /*
     *@author Lassi Bågman
     * Methods for buttons
     */
    public void openStartExerciseActivity(View view){
        Intent intent = new Intent(this, NewRecordedExerciseActivity.class);
        startActivity(intent);
    }

    public void openSaveExerciseActivity(View view){
        Intent intent = new Intent(this, NewManualExerciseActivity.class);
        startActivity(intent);
    }

    public void openHistoryActivity(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(View view){
       Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /*
     *@author Lassi Bågman
     * Method for pop up
     */
    public void selectExerciseTypePopUp(View view){
        //Variables for exercise pop up
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View selectExercisePopUpView = getLayoutInflater().inflate(R.layout.pop_up_select_new_exercise_type, null);
        dialogBuilder.setView(selectExercisePopUpView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}