/*
 * Sportti, android application for fitness tracking and more.
 */


package fi.sportti.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.customViews.CustomProgressBar;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/*
 * @author rasmushy, lassib
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_READ_PHONE_STATE = 101;
    private static final String TAG = "TESTI"; // TAG for Log.d

    private static MainViewModel mainViewModel;
    private CustomProgressBar progressBar;
    private TextView weeklyGoalValueTv;
    private TextView weeklyGoalInfoTv;
    private User user;
    private AlertDialog dialog;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = new ArrayList<>();
        //Setup our access to database
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        progressBar = findViewById(R.id.customProgressBar);
        weeklyGoalValueTv = findViewById(R.id.main_tv_weekly_goal_value);
        weeklyGoalInfoTv = findViewById(R.id.main_tv_weekly_goal_info);
        mainViewModel.getAllUsers().observe(this, users -> userList = users);

        //Update weekly goal bar once MainViewModel has loaded exercises from database.
        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                updateWeeklyGoalBar();
            }
        });

        initialStartUp();
        checkAppPermissions();
    }

    /*
     * @author Rasmus Hyyppä
     * Initial startup method to check do we have user that has saved personal information
     * If not, we create blank user, that can visit Application's activities freely.
     */
    private void initialStartUp() {
//        Log.d(TAG, "initialStartUp() launched");
        if (mainViewModel.getFirstUser() != null) {
//            Log.d(TAG, "initialStartUp() if statement not null");
//            Log.d(TAG, "initialStartup() userList value: " + userList.toString());
            //Lets make another user and compare it to our first user in db
            User plainUser = new User();
            //Get our db's first user
            user = mainViewModel.getFirstUser();
            //If they are not equal then we have user that added personal info's
            if (!user.equals(plainUser)) {
//                Log.d(TAG, "InitialStartUp() welcomes user, hi!");
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
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: Main Activity destroyed");
    }


    /*
     *@author Jukka-Pekka Jaakkola
     */

    private void updateWeeklyGoalBar(){
        HashMap<ZonedDateTime, Integer> dataMap = mainViewModel.getExerciseTimesForGraph(MainViewModel.DAILY_MINUTES);
        ZonedDateTime today = ZonedDateTime.now();
        int year = today.getYear();
        int monthOfYear = today.getMonthValue();
        int currentDayOfWeek = today.getDayOfWeek().getValue();
        ZoneId zone = ZoneId.systemDefault();
        today = ZonedDateTime.of(year, monthOfYear, today.getDayOfMonth(), 12, 0, 0, 0, zone);
        //Set date to first day of week.
        ZonedDateTime firstDayOfWeek = today.minusDays(currentDayOfWeek-1);
        ZonedDateTime keyDate;
        int minutes = 0;
        if(dataMap != null){
            for(int i = 0; i < 7; i++){
                keyDate = firstDayOfWeek.plusDays(i);
                if(dataMap.containsKey(keyDate)){
                    minutes += dataMap.get(keyDate);
                }
            }
        }
        int weeklyGoalInMinutes = user.getWeeklyGoalHour() * 60 + user.getWeeklyGoalMinute();
        //Calculate how many percentages user's total exercise time during current week is of weekly goal.
        //Set that information on screen and draw progress bar again with correct value.
        float multiplier = 1.0f * minutes / weeklyGoalInMinutes;
        int valueOnScreen = Math.round(multiplier * 100);
        weeklyGoalValueTv.setText(valueOnScreen + "%");
        weeklyGoalInfoTv.setText("You have completed " + valueOnScreen + "% of your weekly exercise goal!");
        progressBar.setMultiplier(multiplier);
        progressBar.postInvalidate();
    }

    private void checkAppPermissions(){
        //At App startup check if app has READ_PHONE_STATE permission which is required to display maps.
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE },PERMISSION_READ_PHONE_STATE);
        }
    }
    @Override
    //This method is called by Android when user responds to permission request.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_READ_PHONE_STATE){
            if(!permissionGranted(grantResults)){
                String message = getResources().getString(R.string.toast_maps_not_available);
                Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean permissionGranted(int[] grantResults){
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}