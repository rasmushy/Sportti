package fi.sportti.app.ui.activities;

import static fi.sportti.app.datastorage.room.TypeConversionUtilities.zonedDateToUnixTime;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.getUnixTimeDifference;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

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


import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.location.RouteContainer;

import java.util.ArrayList;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.adapters.ExerciseSaveAdapter;

import fi.sportti.app.ui.viewmodels.MainViewModel;


/**
 * @author Rasmus Hyyppä
 * @version 0.1
 * Activity for user to save recorded exercise.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaveExerciseActivity extends AppCompatActivity {
    private static final String TAG = "SaveExerciseActivity";

    private MainViewModel mainViewModel;

    private AlertDialog dialog;
    private ListView exerciseListView;
    private List<String> exerciseDataList;
    String[] exerciseDataArray;
    private EditText userComment;
    private User user;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_save_exercise);
        Log.d(TAG, "OnCreate()");
        //Initialize
        exerciseListView = findViewById(R.id.saveexercise_listview);
        exerciseDataList = new ArrayList<>();
        mainViewModel = MainActivity.getMainViewModel();
        getRecordedData();
        user = mainViewModel.getFirstUser();

        mapView = findViewById(R.id.saveexercise_mapView_map_for_route);
        mapView.onCreate(savedInstanceState);

        if(RouteContainer.getInstance().hasRoute()){
            //Check if app has READ_PHONE_STATE permission which is required to display map.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                setRouteOnMap();
            }
        }
        else{
            mapView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    private void getRecordedData() {
        Intent intent = getIntent();
        exerciseDataArray = intent.getStringArrayExtra(StartExerciseActivity.REPLY_RECORDED_EXERCISE);

        //Sport type
        String exerciseName = exerciseDataArray[0];

        //Dates
        String exerciseStartDate = exerciseDataArray[1];
        String exerciseEndDate = exerciseDataArray[2];
        ZonedDateTime zonedStartTime = ZonedDateTime.parse(exerciseStartDate);
        ZonedDateTime zonedDateEnd = ZonedDateTime.parse(exerciseEndDate);

        //Duration
        Long totalDurationLong = getUnixTimeDifference(zonedDateToUnixTime(zonedStartTime), zonedDateToUnixTime(zonedDateEnd));
        String totalDuration = timeStringFromLong(totalDurationLong);

        //Calories
        String totalCalories = exerciseDataArray[3];

        //Average heart rate
        String avgHeartRate = exerciseDataArray[4];

        //Distance traveled
        String distance = exerciseDataArray[6];

        //Add basic data to list for adapter
        exerciseDataList.add(exerciseName);
        exerciseDataList.add(exerciseStartDate);
        exerciseDataList.add(totalDuration);
        exerciseDataList.add(totalCalories);
        exerciseDataList.add(avgHeartRate);
        exerciseDataList.add(distance);
        updateListView();
    }

    private void updateListView() {
        //Create ArrayAdapter that fills listview
        ExerciseSaveAdapter adapter = new ExerciseSaveAdapter(this, R.layout.saveexercise_listview_layout, exerciseDataList);
        //Add Comment section as header view, and set view & adapter to listview
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View headerView = inflater.inflate(R.layout.saveexercise_listview_header_view, null, false);
        exerciseListView.addHeaderView(headerView);
        exerciseListView.setAdapter(adapter);
        //SetOnItemClickListener for avgheartrate
        exerciseListView.setOnItemClickListener((adapterView, view, position, l) -> {
            if (position == 5) {
                //position == 5 -> Avg HeartRate
                final NumberPicker heartRatePicker = new NumberPicker(this);
                heartRatePicker.setMaxValue(200);
                heartRatePicker.setMinValue(0);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setView(heartRatePicker)
                        .setTitle("Average heart rate")
                        .setMessage("Choose estimate:")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setIntegerValueForTextView(view, heartRatePicker.getValue());
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
            }
        });
        //Comment box for user initialized
        userComment = exerciseListView.findViewById(R.id.saveexercise_listview_header_edittext);
    }

    /**
     * Currently used only to set avg heart rate (if user changes it)
     *
     * @param view  View data where we can find textview
     * @param value Value that will be set to textview
     */
    public void setIntegerValueForTextView(View view, int value) {
        String valueToString = Integer.toString(value);
        TextView avgHeartRateView = (TextView) view.findViewById(R.id.saveexercise_listview_textview_data);
        avgHeartRateView.setText(valueToString);
        exerciseDataArray[4] = valueToString;
        Log.d(TAG, "setIntegerValueForTextView() --> Avg heart rate set to: " + exerciseDataArray[4]);
    }

    /**
     * Save button where data goes to database
     *
     * @param caller Save button
     */
    public void savePressed(View caller) {
        //Check for comment value
        if (userComment.getText().toString().length() > 1) {
            exerciseDataArray[7] = userComment.getText().toString();
        }

        if (exerciseDataArray != null) {
            Integer userId = 1; //Integer to get null check (Currently useless cause its set to 1)
            //Create exercise from our data
            Exercise exercise = new Exercise(exerciseDataArray[0], userId,
                    ZonedDateTime.parse(exerciseDataArray[1]),
                    ZonedDateTime.parse(exerciseDataArray[2]),
                    Integer.parseInt(exerciseDataArray[3]),
                    Integer.parseInt(exerciseDataArray[4]),
                    exerciseDataArray[5],
                    Double.parseDouble(exerciseDataArray[6]),
                    exerciseDataArray[7]);
            mainViewModel.insertExercise(exercise); //Inserting exercise to database
            RouteContainer.getInstance().resetRoute();
            Log.d(TAG, "savePressed() --> Exercise saved to database" +
                    "\n   type: " + exerciseDataArray[0] +
                    "\n   user id: " + userId +
                    "\n   start d: " + exerciseDataArray[1] +
                    "\n   end d: " + exerciseDataArray[2] +
                    "\n   calories: " + exerciseDataArray[3] +
                    "\n   avg HR: " + exerciseDataArray[4] +
                    "\n   route: " + exerciseDataArray[5] +
                    "\n   distance: " + exerciseDataArray[6] +
                    "\n   comment: " + exerciseDataArray[7]);
            //Send us back to MainActivity page after saving data.
            Intent intentForMainActivity = new Intent(this, MainActivity.class);
            intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentForMainActivity);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        new AlertDialog.Builder(this)
                .setTitle("Exit before saving")
                .setMessage("Are you sure you want to exit without saving exercise?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intentForMainActivity = new Intent(getBaseContext(), MainActivity.class);
                        intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentForMainActivity);
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_exercise_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveexercise_menu_delete_exercise) {
            onBackPressed(); //Alertdialog for trying to leave before saving
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void setRouteOnMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                List<LatLng> coordinates = RouteContainer.getInstance().getRouteAsList();
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
}