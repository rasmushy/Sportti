package fi.sportti.app.ui.activities;

import static fi.sportti.app.datastorage.room.TypeConversionUtilities.zonedDateToUnixTime;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.getUnixTimeDifference;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.location.RouteContainer;
import fi.sportti.app.ui.viewmodels.MainViewModel;


/*
 * @author Rasmus Hyyppä
 * Activity for user to save recorded exercise.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaveExerciseActivity extends AppCompatActivity {

    private ZonedDateTime zonedStartTime;
    private String totalDuration;
    private String totalCalories;
    private String exerciseName;
    private TextView sportType, duration, calories, startDate;
    private MainViewModel mainViewModel;
    private User user; //Current user

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_save_exercise);
        mainViewModel = MainActivity.getMainViewModel();
        //Initialize textviews
        sportType = findViewById(R.id.saveexercise_textview_sport_type);
        duration = findViewById(R.id.saveexercise_textview_duration);
        calories = findViewById(R.id.saveexercise_textview_calories_burned);
        startDate = findViewById(R.id.saveexercise_textview_exercise_date);
        getRecordedData();
        user = mainViewModel.getFirstUser();

        mMapView = (MapView) findViewById(R.id.map);

        mMapView.setVisibility(View.VISIBLE);
        if(RouteContainer.getInstance().hasRoute()){
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    Log.d("´TESTI", "onMapReady: MAP IS READY");

                    List<LatLng> coordinates = RouteContainer.getInstance().getRouteAsList();
                    if(coordinates.isEmpty()){
                        coordinates.add(new LatLng(60.2168,24.7104));
                        coordinates.add(new LatLng(60.2144,24.7146));
                        coordinates.add(new LatLng(60.2134,24.7193));
                        coordinates.add(new LatLng(60.2137,24.7214));
                        coordinates.add(new LatLng(60.2130,24.7236));
                    }

                    mMapView.setStreetMode();
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
        else{
            mMapView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void getRecordedData() {
        Intent intent = getIntent();
        String[] exerciseDataArray = intent.getStringArrayExtra(StartExerciseActivity.REPLY_RECORDED_EXERCISE);
        exerciseName = exerciseDataArray[0];
        String exerciseStartDate = exerciseDataArray[1];
        zonedStartTime = ZonedDateTime.parse(exerciseStartDate);
        String exerciseEndDate = exerciseDataArray[2];
        ZonedDateTime zonedDateEnd = ZonedDateTime.parse(exerciseEndDate);
        Long totalDurationLong = getUnixTimeDifference(zonedDateToUnixTime(zonedStartTime), zonedDateToUnixTime(zonedDateEnd));
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