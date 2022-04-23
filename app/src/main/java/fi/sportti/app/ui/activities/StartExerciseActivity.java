package fi.sportti.app.ui.activities;

import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.sharedpreferences.RecordController;

/**
 * @author Rasmus Hyyppä
 * @version 0.1
 * Application running while you exercise to collect data from it.
 * It provides user a timer count up with reset possibility, it also sends notification to user
 * This activity includes private class TimerTask: "RecordTask"
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class StartExerciseActivity extends AppCompatActivity {

    public static final String REPLY_RECORDED_EXERCISE = "fi.sportti.app.REPLY_RECORDED_EXERCISE";
    public static final String CHANNEL_ID = "Sportti";

    private static final String TAG = "StartExerciseActivity";

    private static volatile RecordController recordController;

    private TextView totalTimeTextView, exerciseTypeTextView;
    private Button startButton, resetButton;

    private Timer timer;

    private String exerciseType;
    private int notificationID = 1;
    private boolean sendNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);
        createNotificationChannel();
        //Initialize button's and text view's
        totalTimeTextView = findViewById(R.id.recordexercise_textview_time);
        exerciseTypeTextView = findViewById(R.id.recordexercise_textview_sport_name);
        startButton = findViewById(R.id.recordexercise_button_start_exercise);
        resetButton = findViewById(R.id.recordexercise_button_reset_timer);

        // Lets start up our recording controller
        startRecordingController();

        //Set up our sport type to textview
        Intent intentExerciseType = getIntent();
        exerciseType = intentExerciseType.getStringExtra(NewRecordedExerciseActivity.REPLY_EXERCISE_TYPE);
        exerciseTypeTextView.setText(exerciseType);

    }


    public void startRecordingController() {
        //First we check is there another recordController, if there is we just use that else, create new one.
        if (recordController == null) {
            synchronized (RecordController.class) {
                if (recordController == null) {
                    recordController = new RecordController(getApplicationContext());
                }
            }
            //Check if we have old exercise still running
            if (recordController.getTimerStartCount() > 0) {
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

    // Private class for running TimerTask, even when user has locked screen.
    private class RecordTask extends TimerTask {
        @Override
        public void run() {
            //Send notification for user
            if (sendNotification) {
                setNotification();
            }

            //Get time if timer is running
            if (recordController.getTimerCounting()) {
                Long timeCounter = Instant.now().toEpochMilli() - recordController.getStartTime().toInstant().toEpochMilli();
                Log.d(TAG, "RecordTask run(): time in ms -> " + timeCounter);
                totalTimeTextView.setText(timeStringFromLong(timeCounter));
            }
        }
    }

    //Resets timer
    private void resetAction() {
        recordController.setStopTime(null);
        recordController.setStartTime(null);
        recordController.zeroTimerStartCount();
        stopTimer();
        totalTimeTextView.setText(timeStringFromLong(0L));
        startButton.setText(R.string.button_text_start);
        resetButton.setText(R.string.button_text_reset);
    }

    //Stops timer
    private void stopTimer() {
        recordController.setTimerCounting(false);
        if (recordController.getTimerStartCount() > 0) {
            startButton.setText(R.string.button_text_resume);
            resetButton.setText(R.string.button_text_end);
        }
    }

    //Starts timer
    private void startTimer() {
        //If our timer is not yet active, create it.
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RecordTask(), 0, 500);
        }
        recordController.setTimerCounting(true);
        startButton.setText(R.string.button_text_stop);
        resetButton.setText(R.string.button_text_reset);
    }

    //Method to calculate time difference after app returns active
    private ZonedDateTime calcRestartTime() {
        long difference = recordController.getStartTime().toInstant().toEpochMilli() - recordController.getStopTime().toInstant().toEpochMilli();
        return ZonedDateTime.ofInstant(Instant.now().plusMillis(difference), ZoneId.systemDefault());
    }

    //Method for start(resume)/stop button
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

    //Method for reset/end button
    private void resetEndAction() {
        if (recordController.getTimerCounting()) {
            resetAction();
        } else {
            //First we check is there anything to be saved
            if (recordController.getStopTime() == null) {
                return;
            }
            //Ending our exercise and get our time for SaveExerciseActivity
            recordController.setStartTime(calcRestartTime());
            recordController.setStopTime(ZonedDateTime.now());
            stopTimer();

            //We use previously selected exercise type as sportType
            //Variable types are currently set as they are in Exercise class
            int calorieAmount = 0; //pretty useless training? TODO: calorie calculations
            Log.d(TAG, "calorieAmount after calculations: " + calorieAmount);
            int avgHeartRate = 0;
            String route = ""; //TODO: route related stuff
            double distance = 0.0; // TODO: distance related stuff
            String comment = "";

            //Create string array of our exercise data, str exercisetype, zdt startdate, zdt stoptime, int calories
            String[] dataForIntent = {exerciseType, recordController.getStartTime().toString(), recordController.getStopTime().toString(), Integer.toString(calorieAmount), Integer.toString(avgHeartRate), route, Double.toString(distance), comment};
            //Time to send all recorded data into SaveExerciseActivity
            Intent intentForSaveActivity = new Intent(this, SaveExerciseActivity.class);
            intentForSaveActivity.putExtra(REPLY_RECORDED_EXERCISE, dataForIntent);
            startActivity(intentForSaveActivity);
            exitRecordingExercise();
        }
    }

    private void exitRecordingExercise() {
        //Cancel our timer to prevent it running infinite amount of time
        resetAction();
        if (timer != null) {
            timer.cancel();
        }
    }

    //Method to see which button view is clicked.
    public void recordExerciseButtonPressed(View caller) {
        if (caller.getId() == R.id.recordexercise_button_start_exercise) {
            startStopAction();
        } else if (caller.getId() == R.id.recordexercise_button_reset_timer) {
            resetEndAction();
        }
    }

    //If timer has started ask user do they really want to exit else stop timer and exit
    @Override
    public void onBackPressed() {
        if (recordController.getTimerStartCount() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit from exercise?")
                    .setMessage("Exit only, if you do not wish to save this exercise.")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Log.d(TAG, "onBackPressed() -> Alertdialog -> OnClick(): user wants to end exercise");
                            startActivity(new Intent(StartExerciseActivity.this, NewRecordedExerciseActivity.class));
                            exitRecordingExercise();
                        }
                    }).create().show();
        } else {
            exitRecordingExercise();
            super.onBackPressed();
        }
    }

    //On pause send notification that we are still running timer
    @Override
    public void onPause() {
        super.onPause();
        //If timer has not started we wont send notifications
        if (recordController.getTimerStartCount() > 0) {
            sendNotification = true;
        }
    }

    // Copypasta: https://developer.android.com/training/notify-user/build-notification#java
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Notification: https://developer.android.com/training/notify-user/build-notification#java
    private void setNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(StartExerciseActivity.this, CHANNEL_ID)
                .setSmallIcon(com.google.android.material.R.drawable.notification_icon_background)
                .setContentTitle(exerciseType)
                .setContentText("Sportti is running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StartExerciseActivity.this);
        notificationManager.notify(notificationID, builder.build());
        sendNotification = false; //boolean set to false so it will not spam notifications.
    }

}