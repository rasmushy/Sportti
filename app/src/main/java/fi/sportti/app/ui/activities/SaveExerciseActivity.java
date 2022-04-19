package fi.sportti.app.ui.activities;

import static fi.sportti.app.ui.utilities.TimeConversionUtilities.getUnixTimeDifference;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.zonedDateTimeToUnix;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.time.ZonedDateTime;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.viewmodels.MainViewModel;

public class SaveExerciseActivity extends AppCompatActivity {

    private ZonedDateTime zonedStartTime;
    private String totalDuration;
    private String totalCalories;
    private String exerciseName;
    private TextView sportType, duration, calories, startDate;
    private MainViewModel mainViewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_exercise);
        mainViewModel = MainActivity.getMainViewModel();
        //Initialize textviews
        sportType = findViewById(R.id.saveexercise_textview_sport_type);
        duration = findViewById(R.id.saveexercise_textview_duration);
        calories = findViewById(R.id.saveexercise_textview_calories_burned);
        startDate = findViewById(R.id.saveexercise_textview_exercise_date);
        getRecordedData();
        user = mainViewModel.getFirstUser();
    }

    private void getRecordedData() {
        Intent intent = getIntent();
        String[] exerciseDataArray = intent.getStringArrayExtra(StartExerciseActivity.REPLY_RECORDED_EXERCISE);
        exerciseName = exerciseDataArray[0];
        String exerciseStartDate = exerciseDataArray[1];
        zonedStartTime = ZonedDateTime.parse(exerciseStartDate);
        String exerciseEndDate = exerciseDataArray[2];
        ZonedDateTime zonedDateEnd = ZonedDateTime.parse(exerciseEndDate);
        Long totalDurationLong = getUnixTimeDifference(zonedDateTimeToUnix(zonedStartTime), zonedDateTimeToUnix(zonedDateEnd));
        totalDuration = timeStringFromLong(totalDurationLong);
        totalCalories = exerciseDataArray[3];
        updateUI();
    }

    private void updateUI() {
        sportType.setText(exerciseName);
        startDate.setText(zonedStartTime.toString());
        duration.setText(totalDuration);
        calories.setText(totalCalories);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit before saving")
                .setMessage("Are you sure you want to exit without saving exercise?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(SaveExerciseActivity.this, MainActivity.class));
                    }
                }).create().show();
    }
}