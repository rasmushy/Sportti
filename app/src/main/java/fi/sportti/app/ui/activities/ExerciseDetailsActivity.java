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

import java.text.DecimalFormat;
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
    private TextView sportName;
    private TextView startDate;
    private TextView durationTextView;
    private TextView caloriesTextView;
    private TextView pulseTextView;
    private TextView distanceTextView;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_exercise_details);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sportName = findViewById(R.id.exercisedetails_textview_sport_name);
        startDate = findViewById(R.id.exercisedetails_textview_start_date_value);
        durationTextView = findViewById(R.id.exercisedetails_textview_duration_value);
        caloriesTextView = findViewById(R.id.exercisedetails_textview_calories_value);
        pulseTextView = findViewById(R.id.exercisedetails_textview_pulse_value);
        distanceTextView = findViewById(R.id.exercisedetails_textview_distance_value);
        mapView = findViewById(R.id.exercisedetails_mapView_map_for_route);
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
        sportName.setText(exercise.getSportType());
        String start = getDateAndTimeAsText(exercise.getStartDate());
        startDate.setText(start);
        int duration = exercise.getDurationInMinutes();
        String durationAsText = formatDuration(duration);
        durationTextView.setText(durationAsText);
        String calories = exercise.getCalories() + " kcal";
        double distance = exercise.getDistance();
        String distanceAsText = String.format("%.2f", distance) + " km";
        String pulse = exercise.getAvgHeartRate() + " /min";
        caloriesTextView.setText(calories);
        distanceTextView.setText(distanceAsText);
        pulseTextView.setText(pulse);

    }

    private void showRouteOnMap(Exercise exercise){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                String route = exercise.getRoute();
                List<LatLng> coordinates = RouteContainer.getInstance().convertTextRouteToList(route);
                if(coordinates.isEmpty()){
                    coordinates.add(new LatLng(60.2168,24.7104));
                    coordinates.add(new LatLng(60.2144,24.7146));
                    coordinates.add(new LatLng(60.2134,24.7193));
                    coordinates.add(new LatLng(60.2137,24.7214));
                    coordinates.add(new LatLng(60.2130,24.7236));
                }

                mapView.setStreetMode();
                LatLng position = coordinates.get(0);
                CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(position, 12);
                mapboxMap.moveCamera(newPosition);
                MarkerOptions startMarker = new MarkerOptions();
                startMarker.position(coordinates.get(0));
                startMarker.setTitle("Alku");
                mapboxMap.addMarker(startMarker);

                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(coordinates.get(coordinates.size()-1));
                endMarker.setTitle("Loppu");
                mapboxMap.addMarker(endMarker);

                PolylineOptions polyline = new PolylineOptions();
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.BLUE);
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