/*
 * Sportti, android application for fitness tracking and more.
 * Copyright Rasmus Hyyppä, Jukka-Pekka Jaakkola, Lassi Bågman, Yana Krylova
 */


package fi.sportti.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/**
 * @author Lassi Bågman
 * @version 1.0.0
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TESTI"; // TAG for Log.d

    private static MainViewModel mainViewModel;
    private CustomProgressBar progressBar;
    private TextView weeklyGoalValueTv;
    private TextView weeklyGoalInfoTv;
    private User user;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setup our access to database
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        progressBar = findViewById(R.id.customProgressBar);
        weeklyGoalValueTv = findViewById(R.id.main_tv_weekly_goal_value);
        weeklyGoalInfoTv = findViewById(R.id.main_tv_weekly_goal_info);

        initializeProgressBar();
        initialStartUp();
    }

    /**
     * Initial startup method to check do we have user that has saved personal information
     * If not, we create blank user, that can visit Application's activities freely.
     * This is to make sure we always have user in Room Database to use.
     *
     * @author Rasmus Hyyppä
     */
    private void initialStartUp() {
        if (mainViewModel.getFirstUser() != null) {
            //Get our db's first user
            user = mainViewModel.getFirstUser();
            return;
        }
        // Insert blank user
        user = new User();
        mainViewModel.insertUser(user);
    }

    public static MainViewModel getMainViewModel() {
        return mainViewModel;
    }


    /**
     * onClick methods for moving to wanted activity
     *
     * @author Lassi Bågman
     */
    public void openStartExerciseActivity(View view) {
        Intent intent = new Intent(this, NewRecordedExerciseActivity.class);
        startActivity(intent);
    }

    public void openSaveExerciseActivity(View view) {
        Intent intent = new Intent(this, NewManualExerciseActivity.class);
        startActivity(intent);
    }

    public void openHistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }

    /**
     * Method for new exercise pop up
     *
     * @author Lassi Bågman
     */
    public void selectExerciseTypePopUp(View view) {
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

    /**
     * @author Jukka-Pekka Jaakkola
     */
    private void initializeProgressBar() {
        //Update weekly goal bar once MainViewModel has loaded exercises from database or if there is
        //changes in exercise list.
        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                updateWeeklyGoalBar();
            }
        });
    }

    /**
     * @author Jukka-Pekka Jaakkola
     */
    private void updateWeeklyGoalBar() {
        //Calculate how many percentages user's total exercise time during current week is of weekly goal.
        //Set that information on screen and draw progress bar again with correct value.
        int exerciseTime = mainViewModel.getExerciseTimeForThisWeek();
        int weeklyGoal = user.getWeeklyGoalHour() * 60 + user.getWeeklyGoalMinute();
        float multiplier = 1;
        // If user has exercised less than weekly goal, calculate how many % that is of weekly goal.
        // Other wise keep default value 1, meaning 100%
        if (exerciseTime < weeklyGoal) {
            multiplier = 1.0f * exerciseTime / weeklyGoal;
        }

        int valueOnScreen = Math.round(multiplier * 100);
        weeklyGoalValueTv.setText(valueOnScreen + "%");
        weeklyGoalInfoTv.setText("You have completed " + valueOnScreen + "% of your weekly exercise goal!");
        progressBar.setMultiplier(multiplier);
        progressBar.postInvalidate();
    }
}