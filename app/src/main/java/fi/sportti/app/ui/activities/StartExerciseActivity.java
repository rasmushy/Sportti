package fi.sportti.app.ui.activities;

import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.sharedpreferences.RecordController;

public class StartExerciseActivity extends AppCompatActivity {

    private static final String TAG = "StartExerciseActivity";
    public static final String REPLY_RECORDED_EXERCISE = "fi.sportti.app.REPLY_RECORDED_EXERCISE";

    private static RecordController recordController;
    private static Timer timer;
    private static RecordTask recordTask;

    private TextView totalTimeTextView, exerciseTypeTextView;
    private Button startButton, resetButton;
    private String exerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);

        //Initialize button's and text view's
        totalTimeTextView = findViewById(R.id.recordexercise_textview_time);
        exerciseTypeTextView = findViewById(R.id.recordexercise_textview_sport_name);
        startButton = findViewById(R.id.recordexercise_button_start_exercise);
        resetButton = findViewById(R.id.recordexercise_button_reset_timer);

        Intent intentExerciseType = getIntent();
        exerciseType = intentExerciseType.getStringExtra(NewRecordedExerciseActivity.REPLY_EXERCISE_TYPE);
        Log.d(TAG, "exerciseType: " + exerciseType);
        exerciseTypeTextView.setText(exerciseType);

        startRecordingController();
        timer = new Timer();
        Log.d(TAG, "onCreate() timer: " + timer);
        recordTask = new RecordTask();
        Log.d(TAG, "onCreate() timerTask: " + recordTask);
        timer.scheduleAtFixedRate(recordTask, 0, 500);
    }

    public void startRecordingController() {
        if (recordController == null) {
            synchronized (RecordController.class) {
                if (recordController == null) {
                    recordController = new RecordController(getApplicationContext());
                    if (recordController.getTimerCounting()) {
                        startTimer();
                    } else {
                        stopTimer();
                        if (recordController.getStartTime() != null && recordController.getStopTime() != null) {
                            Long time = Instant.now().toEpochMilli() - calcRestartTime().toInstant().toEpochMilli();
                            totalTimeTextView.setText(timeStringFromLong(time));
                        }
                    }
                }
            }
        }
    }

    // Private class for running TimerTask, even when user has locked screen.
    private class RecordTask extends TimerTask {
        @Override
        public void run() {
            if (recordController.getTimerCounting()) {
                Long timeCounter = Instant.now().toEpochMilli() - recordController.getStartTime().toInstant().toEpochMilli();
                Log.d(TAG, "RecordTask run(): time -> " + timeCounter);
                totalTimeTextView.setText(timeStringFromLong(timeCounter));
            }
        }
    }

    private void resetAction() {
        recordController.setStopTime(null);
        recordController.setStartTime(null);
        stopTimer();
        Long time = 0L;
        totalTimeTextView.setText(timeStringFromLong(time));

        startButton.setText(R.string.button_text_start);
        resetButton.setText(R.string.button_text_reset);
    }

    private void stopTimer() {
        recordController.setTimerCounting(false);
        if (recordController.getTimerStartCount() > 0) {
            startButton.setText(R.string.button_text_resume);
            resetButton.setText(R.string.button_text_end);
        }
    }

    private void startTimer() {
        recordController.setTimerCounting(true);
        startButton.setText(R.string.button_text_stop);
        resetButton.setText(R.string.button_text_reset);
    }

    private void startStopAction() {
        if (recordController.getTimerCounting()) {
            recordController.setStopTime(ZonedDateTime.now());
            stopTimer();
        } else {
            if (recordController.getStopTime() != null) {
                recordController.setStartTime(calcRestartTime());
                recordController.setStopTime(null);
            } else {
                recordController.setStartTime(ZonedDateTime.now());
            }
            startTimer();
        }
    }

    private void resetEndAction() {
        if (recordController.getTimerCounting()) {
            resetAction();
        } else {
            //First we check is there anything to be saved
            if (recordController.getStopTime() == null) {
                Toast toast = Toast.makeText(getBaseContext(), "Nothing to be saved", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            //Ending our exercise and time to move into SaveExerciseActivity
            recordController.setStartTime(calcRestartTime());
            recordController.setStopTime(ZonedDateTime.now());
            stopTimer();

            //We use previously selected exercise type as sportType
            Log.d(TAG, "resetEndAction, user wants to save sport type: " + exerciseType);
            int calorieAmount = 0; //pretty useless training? TODO: calorie calculations
            Log.d(TAG, "calorieAmount after calculations: " + calorieAmount);

            //Create string array of our exercise data
            String[] dataForIntent = {exerciseType, recordController.getStartTime().toString(), recordController.getStopTime().toString(), Integer.toString(calorieAmount)};
            //Time to send all recorded data into SaveExerciseActivity
            Intent intentForSaveActivity = new Intent(StartExerciseActivity.this, SaveExerciseActivity.class);
            intentForSaveActivity.putExtra(REPLY_RECORDED_EXERCISE, dataForIntent);
            startActivity(intentForSaveActivity);
            exitRecordingExercise();
        }
    }

    private ZonedDateTime calcRestartTime() {
        long difference = recordController.getStartTime().toInstant().getEpochSecond() - recordController.getStopTime().toInstant().getEpochSecond();
        return ZonedDateTime.now().plusSeconds(difference);
    }


    public void recordExerciseButtonPressed(View caller) {
        if (caller.getId() == R.id.recordexercise_button_start_exercise) {
            startStopAction();
        } else if (caller.getId() == R.id.recordexercise_button_reset_timer) {
            resetEndAction();
        }
    }

    private void exitRecordingExercise() {
        //Cancel our timer so it doesn't run for nothing.
        resetAction();
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        exitRecordingExercise();
        super.onBackPressed();
    }
}