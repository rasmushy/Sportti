package fi.sportti.app.ui.activities;

import static fi.sportti.app.datastorage.room.TypeConversionUtilities.zonedDateToUnixTime;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.*;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.SeekBar;
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
import java.util.ArrayList;
import java.util.List;

import fi.sportti.app.location.RouteContainer;

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

    //Dialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    //Exercises
    private ListView exerciseListView;
    private List<String> exerciseDataList;
    String[] exerciseDataArray;
    private EditText userComment;
    private Button openMapButton;
//    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_save_exercise);
        Log.d(TAG, "OnCreate()");

        //Initialize
        exerciseListView = findViewById(R.id.saveexercise_listview);
        openMapButton = findViewById(R.id.saveexercise_button_open_map);
        exerciseDataList = new ArrayList<>();
        mainViewModel = MainActivity.getMainViewModel();
        dialogBuilder = new AlertDialog.Builder(this);

        getRecordedData();

        if (!RouteContainer.getInstance().hasRoute()) {
            openMapButton.setVisibility(View.INVISIBLE);
        }
    }

    public void openMap(View view) {
        if (RouteContainer.getInstance().hasRoute()) {
            //Check if app has READ_PHONE_STATE permission which is required to display map.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                String route = RouteContainer.getInstance().getRouteAsText();
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_ROUTE, route);
                startActivity(intent);
            }
        }
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
        //Method to format date into prettier form
        exerciseStartDate = getDateAndTimeAsString(zonedStartTime);

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

        //Comment box for user initialized
        userComment = exerciseListView.findViewById(R.id.saveexercise_listview_header_edittext);

        //SetOnItemClickListener for Listview
        setItemClickListenerForListView();
    }

    private void setItemClickListenerForListView() {
        exerciseListView.setOnItemClickListener((adapterView, viewFromListView, position, l) -> {
            TextView textViewForData = (TextView) viewFromListView.findViewById(R.id.saveexercise_listview_textview_data);
            switch (position) {
                case 4:
                    //position == 4 -> Calories
                    final View giveCaloriesPopUp = getLayoutInflater().inflate(R.layout.pop_up_give_calories, null);
                    Button buttonSaveCalories = giveCaloriesPopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
                    SeekBar seekBarCalories = giveCaloriesPopUp.findViewById(R.id.seekBarCalories);
                    TextView textViewSeekBarCaloriesValue = giveCaloriesPopUp.findViewById(R.id.textViewSeekBarCaloriesValue);
                    seekBarCalories.setMax(350);
                    textViewSeekBarCaloriesValue.setText(exerciseDataArray[3] + " calories");
                    seekBarCalories.setProgress(Integer.parseInt(exerciseDataArray[3]));
                    dialogBuilder.setView(giveCaloriesPopUp);
                    seekBarCalories.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            textViewSeekBarCaloriesValue.setText(seekBarCalories.getProgress() * 10 + " calories");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            textViewSeekBarCaloriesValue.setText(seekBarCalories.getProgress() * 10 + " calories");
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            textViewSeekBarCaloriesValue.setText(seekBarCalories.getProgress() * 10 + " calories");
                        }
                    });
                    buttonSaveCalories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textViewForData.setText(Integer.toString(seekBarCalories.getProgress() * 10));
                            exerciseDataArray[3] = Integer.toString(seekBarCalories.getProgress() * 10);
                            dialog.dismiss();
                        }
                    });
                    dialog = dialogBuilder.create();
                    dialog.show();
                    break;
                case 5:
                    //position == 5 -> Avg HeartRate
                    final View giveAveragePulsePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_average_pulse, null);
                    Button buttonSavePulse = giveAveragePulsePopUp.findViewById(R.id.buttonSavePulsePopUp);
                    SeekBar seekBarPulse = giveAveragePulsePopUp.findViewById(R.id.seekBarPulse);
                    TextView textViewSeekBarPulseValue = giveAveragePulsePopUp.findViewById(R.id.textViewSeekBarPulseValue);
                    textViewSeekBarPulseValue.setText(exerciseDataArray[4] + " bpm");
                    seekBarPulse.setMax(28);
                    seekBarPulse.setMin(6);
                    seekBarPulse.setProgress(Integer.parseInt(exerciseDataArray[4]));
                    dialogBuilder.setView(giveAveragePulsePopUp);
                    seekBarPulse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            textViewSeekBarPulseValue.setText(seekBarPulse.getProgress() * 5 + 50 + " bpm");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            textViewSeekBarPulseValue.setText((seekBarPulse.getProgress() * 5 + 50) + " bpm");
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            textViewSeekBarPulseValue.setText((seekBarPulse.getProgress() * 5 + 50) + " bpm");
                        }
                    });
                    buttonSavePulse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textViewForData.setText(Integer.toString(seekBarPulse.getProgress() * 5 + 50));
                            exerciseDataArray[4] = Integer.toString(seekBarPulse.getProgress() * 5 + 50);
                            dialog.dismiss();
                        }
                    });
                    dialog = dialogBuilder.create();
                    dialog.show();
                    break;
                default:
                    Log.d(TAG, "exerciseListView clicked position: " + position);
            }
        });
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
        if (dialog != null) {
            dialog.dismiss();
        }
    }

//    private void setRouteOnMap(){
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(MapboxMap mapboxMap) {
//                List<LatLng> coordinates = RouteContainer.getInstance().getRouteAsList();
//                mapView.setStreetMode();
//
//                //Center camera at start location.
//                LatLng startPosition = coordinates.get(0);
//                LatLng endPosition = coordinates.get(coordinates.size()-1);
//                CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(startPosition, 12);
//                mapboxMap.moveCamera(newPosition);
//
//                //Add markers
//                String startMarkerText = getResources().getString(R.string.map_start_marker);
//                String endMarkerText = getResources().getString(R.string.map_end_marker);
//                MarkerOptions startMarker = new MarkerOptions();
//                startMarker.position(startPosition);
//                startMarker.setTitle(startMarkerText);
//                mapboxMap.addMarker(startMarker);
//                MarkerOptions endMarker = new MarkerOptions();
//                endMarker.position(endPosition);
//                endMarker.setTitle(endMarkerText);
//                mapboxMap.addMarker(endMarker);
//
//                //Add route as polyline.
//                PolylineOptions polyline = new PolylineOptions()
//                    .addAll(coordinates)
//                    .width(3)
//                    .color(Color.BLUE);
//                mapboxMap.addPolyline(polyline);
//            }
//        });
//    }
}