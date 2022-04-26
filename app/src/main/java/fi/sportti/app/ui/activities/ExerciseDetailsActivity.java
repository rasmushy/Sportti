package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_exercise_details);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sportNameTv = findViewById(R.id.exercisedetails_tv_sport_name);
        startDateTv = findViewById(R.id.exercisedetails_tv_start_date_value);
        durationTv = findViewById(R.id.exercisedetails_tv_duration_value);
        caloriesTv = findViewById(R.id.exercisedetails_tv_calories_value);
        pulseTv = findViewById(R.id.exercisedetails_tv_pulse_value);
        distanceTv = findViewById(R.id.exercisedetails_tv_length_value);
        mapView = findViewById(R.id.exercisedetails_mapView_map_for_route);
        commentTv = findViewById(R.id.exercisedetails_tv_comment_value);
        mapView.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        int index = b.getInt(HistoryActivity.SELECTED_EXERCISE);

        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                exercises.sort(new Comparator<Exercise>() {
                    @Override
                    public int compare(Exercise exercise, Exercise t1) {
                        return t1.getStartDate().compareTo(exercise.getStartDate());
                    }
                });
                Exercise exercise = exercises.get(index);
                setInformationOnScreen(exercise);
                if(exercise.hasRoute()){
                    showRouteOnMap(exercise);
                }
                else {
                    mapView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setInformationOnScreen(Exercise exercise){
        int duration = exercise.getDurationInMinutes();
        double distance = exercise.getDistance();
        String sportName = exercise.getSportType();
        String startDate = getDateAndTimeAsText(exercise.getStartDate());
        String durationAsText = formatDuration(duration);
        String calories = exercise.getCalories() + " kcal";
        String distanceAsText = String.format("%.2f", distance) + " km";
        String pulse = exercise.getAvgHeartRate() + " /min";
        String comment = exercise.getComment();
        sportNameTv.setText(sportName);
        startDateTv.setText(startDate);
        durationTv.setText(durationAsText);
        caloriesTv.setText(calories);
        distanceTv.setText(distanceAsText);
        pulseTv.setText(pulse);
        commentTv.setText(comment);

    }

    private void showRouteOnMap(Exercise exercise){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                String route = exercise.getRoute();
                List<LatLng> coordinates = RouteContainer.getInstance().convertTextRouteToList(route);
                mapView.setStreetMode();

                //Center camera at start location.
                LatLng startPosition = coordinates.get(0);
                LatLng endPosition = coordinates.get(coordinates.size()-1);
                CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(startPosition, 12);
                mapboxMap.moveCamera(newPosition);

                //Add markers
                MarkerOptions startMarker = new MarkerOptions();
                startMarker.position(startPosition);
                String startMarkerText = getResources().getString(R.string.map_start_marker);
                String endMarkerText = getResources().getString(R.string.map_end_marker);
                startMarker.setTitle(startMarkerText);
                mapboxMap.addMarker(startMarker);
                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(endPosition);
                endMarker.setTitle(endMarkerText);
                mapboxMap.addMarker(endMarker);

                //Add route as polyline.
                PolylineOptions polyline = new PolylineOptions()
                        .addAll(coordinates)
                        .width(3)
                        .color(Color.BLUE);
                mapboxMap.addPolyline(polyline);
            }
        });
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