package fi.sportti.app.ui.activities;

import static fi.sportti.app.ui.utilities.CalorieConversionUtilities.getCalories;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import fi.sportti.app.App;
import fi.sportti.app.R;
import fi.sportti.app.constants.ExerciseType;
import fi.sportti.app.datastorage.sharedpreferences.RecordController;
import fi.sportti.app.ui.viewmodels.MainViewModel;
import fi.sportti.app.location.LocationTracking;
import fi.sportti.app.location.RouteContainer;

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
    //public static final String CHANNEL_ID = "Sportti";
    private static final int PERMISSION_FINE_LOCATION = 100;
    private static final int PERMISSION_READ_PHONE_STATE = 101;
    private static final int ENABLE_LOCATION_SERVICES = 102;
    private static final String TAG = "TESTI";

    private static volatile RecordController recordController;

    private MainViewModel mainViewModel;

    private TextView totalTimeTextView, exerciseTypeTextView;
    private Button startButton, resetButton;
    private Switch trackLocationSwitch;

    private Timer timer;

    private int exerciseType;
    private int notificationID = 1;
    private boolean sendNotification;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);
        mainViewModel = MainActivity.getMainViewModel();
        createNotificationChannel();
        //createNotificationChannel();
        //Initialize button's and text view's
        totalTimeTextView = findViewById(R.id.recordexercise_textview_time);
        exerciseTypeTextView = findViewById(R.id.recordexercise_textview_sport_name);
        startButton = findViewById(R.id.recordexercise_button_start_exercise);
        resetButton = findViewById(R.id.recordexercise_button_reset_timer);
        resetButton.setClickable(false);
        trackLocationSwitch = findViewById(R.id.recordexercise_switch_track_location);

        // Lets start up our recording controller
        startRecordingController();

        //Set up our sport type to textview
        Intent intentExerciseType = getIntent();
        exerciseType = intentExerciseType.getIntExtra(NewRecordedExerciseActivity.REPLY_EXERCISE_TYPE, 0);
        exerciseTypeTextView.setText(ExerciseType.values()[exerciseType].getExerciseName());

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopTrackingLocation();
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
            //Send notification for user. If location tracking is turned on,
            // then location tracking service will show notification.
            if (sendNotification && !trackLocationSwitch.isChecked()) {
               setNotification();
            }

            //Get time if timer is running
            if (recordController.getTimerCounting()) {
                Long timeCounter = Instant.now().toEpochMilli() - recordController.getStartTime().toInstant().toEpochMilli();
                //Log.d(TAG, "RecordTask run(): time in ms -> " + timeCounter);
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
            if(trackLocationSwitch.isChecked()){
                stopTrackingLocation();
            }
            stopTimer();
        } else {
            if (recordController.getStopTime() != null) {
                recordController.setStartTime(calcRestartTime());
                recordController.setStopTime(null);
            } else {
                recordController.setStartTime(ZonedDateTime.now());
            }

            if(trackLocationSwitch.isChecked()){
                startTrackingLocation();
            }
            startTimer();
            resetButton.setClickable(true);
            trackLocationSwitch.setClickable(false);
        }
    }

    //Method for reset/end button
    private void resetEndAction() {
        trackLocationSwitch.setClickable(true);
        if(trackLocationSwitch.isChecked()){
            stopTrackingLocation();
        }

        if (recordController.getTimerCounting()) {
            resetAction();
            if(trackLocationSwitch.isChecked()){
                RouteContainer.getInstance().resetRoute();
            }
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
            //getCalories(User user, String sportType, ZonedDateTime startDate, ZonedDateTime endDate)
            int calorieAmount = getCalories(mainViewModel.getFirstUser(), exerciseType, recordController.getStartTime(), recordController.getStopTime());
            int avgHeartRate = 0;
            RouteContainer routeContainer = RouteContainer.getInstance();
            String route = routeContainer.getRouteAsText();
            double distance = routeContainer.getRouteLength();

            String comment = "";

            //Create string array of our exercise data, str exercisetype, zdt startdate, zdt stoptime, int calories
            String[] dataForIntent = {ExerciseType.values()[exerciseType].getExerciseName(), recordController.getStartTime().toString(), recordController.getStopTime().toString(), Integer.toString(calorieAmount), Integer.toString(avgHeartRate), route, Double.toString(distance), comment};
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

//    // Copypasta: https://developer.android.com/training/notify-user/build-notification#java
//    private void createNotificationChannel() {
//        Log.d(TAG, "createNotificationChannel: called");
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    // Notification: https://developer.android.com/training/notify-user/build-notification#java
    private void setNotification() {
        Log.d(TAG, "setNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(StartExerciseActivity.this, App.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(com.google.android.material.R.drawable.notification_icon_background)
                .setContentTitle(ExerciseType.values()[exerciseType].getExerciseName())
                .setContentText("Sportti is running in the background")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STOPWATCH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StartExerciseActivity.this);
        notificationManager.notify(notificationID, builder.build());
        sendNotification = false; //boolean set to false so it will not spam notifications.
    }


    /*
     *@author Jukka-Pekka Jaakkola
     */

    public void toggleLocationTracking(View view){
        if(trackLocationSwitch.isChecked()){
            //Check if app has permission to use device location.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //If it has, make sure that location services are enabled so location can be tracked.
                enableLocationServices();
            }
            else {
                //Request result will be handled in onRequestPermissionsResult which is defined below.
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    //This method is called by Android system when user responds to permission request.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //If user gave app permission to Location services, make sure that Location services are enabled.
        if(requestCode == PERMISSION_FINE_LOCATION){
            if(permissionGranted(grantResults)){
                enableLocationServices();
            }
            //Else simply set locationTrackingSwitch off so app doesn't try to track route.
            else {
                trackLocationSwitch.setChecked(false);
            }
        }
    }

    private void startTrackingLocation(){
        Intent locationTrackingService = new Intent(this, LocationTracking.class);
        Context context = this;
        context.startForegroundService(locationTrackingService);
    }

    private void stopTrackingLocation(){
        if(LocationTracking.serviceRunning){
            Intent locationTrackingService = new Intent(this, LocationTracking.class);
            stopService(locationTrackingService);
        }
    }

    //Code to check if location service is enabled is from Android developer documentation.
    private void enableLocationServices(){
        //Check if location services are enabled.
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Location services are disabled. Asking user to turn them on.");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(StartExerciseActivity.this, ENABLE_LOCATION_SERVICES);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENABLE_LOCATION_SERVICES){
            // If user did not enable Location services on device
            // turn trackLocationSwitch off and tell user that route cannot be saved.
            if(resultCode != Activity.RESULT_OK){
                trackLocationSwitch.setChecked(false);
                String message = getResources().getString(R.string.toast_location_services_not_enabled);
                Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private boolean permissionGranted(int[] grantResults){
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }



}