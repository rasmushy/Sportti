package fi.sportti.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import fi.sportti.app.App;
import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/**
 *@author Jukka-Pekka Jaakkola
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExerciseDetailsActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView sportNameTv;
    private TextView startDateTv;
    private TextView durationTv;
    private TextView caloriesTv;
    private TextView pulseTv;
    private TextView distanceTv;
    private TextView commentTv;
    private String route;
    private Button openMapButton;
    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);
        sportNameTv = findViewById(R.id.exercisedetails_tv_sport_name);
        startDateTv = findViewById(R.id.exercisedetails_tv_start_date_value);
        durationTv = findViewById(R.id.exercisedetails_tv_duration_value);
        caloriesTv = findViewById(R.id.exercisedetails_tv_calories_value);
        pulseTv = findViewById(R.id.exercisedetails_tv_pulse_value);
        distanceTv = findViewById(R.id.exercisedetails_tv_length_value);
        commentTv = findViewById(R.id.exercisedetails_tv_comment_value);
        openMapButton = findViewById(R.id.exercisedetails_button_open_map);

        Bundle b = getIntent().getExtras();
        int index = b.getInt(HistoryActivity.SELECTED_EXERCISE);

        mainViewModel = MainActivity.getMainViewModel();
        //Get correct Exercise.
        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                exercises.sort(new Comparator<Exercise>() {
                    @Override
                    public int compare(Exercise exercise, Exercise t1) {
                        return t1.getStartDate().compareTo(exercise.getStartDate());
                    }
                });
                exercise = exercises.get(index);
                setInformationOnScreen(exercise);
                if(exercise.hasRoute()){
                    route = exercise.getRoute();
                }
                else {
                    openMapButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void deleteExercise(View view){
        mainViewModel.deleteExercise(exercise);
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void openMapButtonClicked(View view){
        //Check if app has READ_PHONE_STATE permission which is required to display map.
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionState == PackageManager.PERMISSION_GRANTED){
            openMap();
        }
        else {
            openMapButton.setClickable(false);
            String[] permissions = { Manifest.permission.READ_PHONE_STATE };
            requestPermissions(permissions, App.PERMISSION_CODE_READ_PHONE_STATE);
        }
    }

    //This method is called by Android when user responds to permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == App.PERMISSION_CODE_READ_PHONE_STATE){
            if(permissionGranted(grantResults)){
                openMapButton.setClickable(true);
                openMap();
            }
            else {
                //Check if app should show informative message to user about why this permission is required.
                //Android System decides this.
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                    showInformativeDialog();
                }
                else {
                    openMapButton.setClickable(true);
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private boolean permissionGranted(int[] grantResults){
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void openMap(){
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra(MapActivity.EXTRA_ROUTE, route);
        startActivity(mapIntent);
    }


    private void showPermissionDeniedDialog(){
        //Dialog where user is explained that map is not available because required permission was not granted.

        //Create button for dialog.
        DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        //Get texts for dialog
        String title = getResources().getString(R.string.map_not_available);
        String message = getResources().getString(R.string.required_permission_denied_message);

        //Build, create and show dialog.
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", positiveButton)
                .create().show();
    }


    private void showInformativeDialog(){
        //Informative dialog where user can see why permission is required.
        //User can verify to deny this permission or show permission request window again.

        //Create buttons for dialog.
        DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] permissions = { Manifest.permission.READ_PHONE_STATE };
                requestPermissions(permissions, App.PERMISSION_CODE_READ_PHONE_STATE);
            }
        };
        DialogInterface.OnClickListener negativeButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                openMapButton.setClickable(true);
            }
        };
        //Get texts for dialog
        String title = getResources().getString(R.string.permission_denied);
        String message = getResources().getString(R.string.informative_message_for_permissions);

        //Build, create and show dialog.
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ask again", positiveButton)
                .setNegativeButton("I'm sure", negativeButton)
                .create().show();
    }

    private void setInformationOnScreen(Exercise exercise){
        int duration = exercise.getDurationInMinutes();
        double distance = exercise.getDistance();
        String sportName = exercise.getSportType();
        String startDate = getDateAndTimeAsText(exercise.getStartDate());
        String durationAsText = formatDuration(duration);
        String calories = exercise.getCalories() + " kcal";
        String distanceAsText = String.format("%.2f", distance) + " km";
        String pulse = "-";
        if(exercise.getAvgHeartRate() != 0){
            pulse = exercise.getAvgHeartRate() + "/min";
        }
        String comment = exercise.getComment();

        sportNameTv.setText(sportName);
        startDateTv.setText(startDate);
        durationTv.setText(durationAsText);
        caloriesTv.setText(calories);
        distanceTv.setText(distanceAsText);
        pulseTv.setText(pulse);
        commentTv.setText(comment);

    }

    private String getDateAndTimeAsText(ZonedDateTime date){
        StringBuilder sb = new StringBuilder();
        sb.append(date.getDayOfMonth() + ".");
        sb.append(date.getMonthValue() + ".");
        sb.append(date.getYear() + " ");
        sb.append(date.getHour() + ":");
        int minute = date.getMinute();
        if(minute >= 10){
            sb.append(minute);
        }
        else {
            sb.append("0" + minute);
        }
        return sb.toString();
    }


    private String formatDuration(int durationInMinutes){
        //Returns duration in String format. Examples:
        // "34 min" if duration is less than 60 minutes.
        // "1h 15min" if duration is over 60 minutes.
        String result = durationInMinutes + " min";
        if(durationInMinutes >= 60){
            int fullHours = durationInMinutes / 60;
            int minutesLeft = durationInMinutes - fullHours * 60;
            result = fullHours + "h ";
            if(minutesLeft > 0){
                result += minutesLeft + "min";
            }
        }
        return result;
    }
}