package fi.sportti.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.location.RouteContainer;
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
        //mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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
        MainActivity.getMainViewModel().deleteExercise(exercise);
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void openMapButtonClicked(View view){
        //Check if app has READ_PHONE_STATE permission which is required to display map.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            openMap();
        }
        else {
            openMapButton.setClickable(false);
            requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE },MapActivity.PERMISSION_READ_PHONE_STATE);
        }
    }

    @Override
    //This method is called by Android when user responds to permission request.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MapActivity.PERMISSION_READ_PHONE_STATE){
            if(permissionGranted(grantResults)){
                openMapButton.setClickable(true);
                openMap();
            }
            else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                    showInformativeDialog();
                }
                else {
                    openMapButton.setClickable(true);
                    showPermissionDeniedToast();
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

    private void showPermissionDeniedToast(){
        String message = getResources().getString(R.string.required_permission_denied);
        Toast toast = Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    private void showInformativeDialog(){
        String title = getResources().getString(R.string.permission_denied);
        String message = getResources().getString(R.string.informative_message_for_permissions);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ask again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE },MapActivity.PERMISSION_READ_PHONE_STATE);
                    }
                })
                .setNegativeButton("I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        openMapButton.setClickable(true);
                        showPermissionDeniedToast();
                    }
                });
        dialog.create().show();
    }


    private void setInformationOnScreen(Exercise exercise){
        int duration = exercise.getDurationInMinutes();
        double distance = exercise.getDistance();
        String sportName = exercise.getSportType();
        String startDate = getDateAndTimeAsText(exercise.getStartDate());
        String durationAsText = formatDuration(duration);
        String calories = exercise.getCalories() + " kcal";
        String distanceAsText = String.format("%.2f", distance) + " km";
        String pulse;
        if(exercise.getAvgHeartRate() == 0){
            pulse = "-";
        }
        else {
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


    private String formatDuration(int duration){
        String result = "";
        if(duration == 60){
            result = "1h";
        }
        else if(duration >= 60){
            int hours = duration / 60;
            int minutes = duration - (hours*60);
            if(hours == 1){
                result = "1h";
            }
            else {
                result += hours + "h ";
            }
            if(minutes > 0){
                result += minutes + "min";
            }
        }
        else {
            result = duration + " min";
        }
        return result;
    }
}