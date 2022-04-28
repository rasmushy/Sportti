package fi.sportti.app.ui.activities;

import static fi.sportti.app.datastorage.room.TypeConversionUtilities.zonedDateToUnixTime;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.getUnixTimeDifference;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.timeStringFromLong;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.NumberPicker;

import android.widget.TextView;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


import fi.sportti.app.datastorage.room.User;
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

    private AlertDialog dialog;
    private ListView exerciseListView;
    private List<String> exerciseDataList;
    String[] exerciseDataArray;
    private EditText userComment;
    private Button openMapButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_exercise);
        //Initialize
        exerciseListView = findViewById(R.id.saveexercise_listview);
        openMapButton = findViewById(R.id.saveexercise_button_open_map);
        exerciseDataList = new ArrayList<>();
        mainViewModel = MainActivity.getMainViewModel();
        getRecordedData();
        user = mainViewModel.getFirstUser();

        //Set openMapButton invisible if user did not want to save route or
        //user did not give required permissions to display maps when app started.
//        if(!RouteContainer.getInstance().hasRoute() ||
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
//            openMapButton.setVisibility(View.INVISIBLE);
//        }

        if(!RouteContainer.getInstance().hasRoute()){
            openMapButton.setVisibility(View.INVISIBLE);
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
        exerciseStartDate = getDateAndTimeAsText(zonedStartTime);

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
        exerciseListView.setOnItemClickListener((adapterView, view, position, l) -> {
            switch (position) {
                case 1:
                    //position == 1 -> Exercise Type
                    Log.d(TAG, "exerciseListView clicked exercise type: " + position);
                    break;
                case 2:
                    //position == 2 -> Start Date
                    Log.d(TAG, "exerciseListView clicked start date: " + position);
                    break;
                case 3:
                    //position == 3 -> Duration
                    Log.d(TAG, "exerciseListView clicked duration: " + position);
                    break;
                case 4:
                    //position == 4 -> Calories
                    Log.d(TAG, "exerciseListView clicked calories: " + position);
                    break;
                case 5:
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
                    break;
                case 6:
                    //position == 5 -> Distance
                    Log.d(TAG, "exerciseListView clicked distance: " + position);
                    break;
                default:
                    Log.d(TAG, "exerciseListView clicked position: " + position);
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
        if (dialog != null) {
            dialog.dismiss();
        }
    }


/**
 * @author Jukka-Pekka Jaakkola
 * */

    public void openMapButtonClicked(View view){
        //Check if app has READ_PHONE_STATE permission which is required to display map.
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionState == PackageManager.PERMISSION_GRANTED){
            openMap();
        }
        //If not, request permission and disable openMapButton until permission request process is finished.
        else {
            openMapButton.setClickable(false);
            String[] permissions = { Manifest.permission.READ_PHONE_STATE };
            requestPermissions(permissions, MapActivity.READ_PHONE_STATE_PERMISSION_CODE);
        }
    }

    private void openMap(){
        //Starts Map Activity and passes route as extra.
        Intent mapIntent = new Intent(this, MapActivity.class);
        String route = RouteContainer.getInstance().getRouteAsText();
        mapIntent.putExtra(MapActivity.EXTRA_ROUTE, route);
        startActivity(mapIntent);
    }

    @Override
    //This method is called by Android when user responds to permission request.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MapActivity.READ_PHONE_STATE_PERMISSION_CODE){
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

    //Dialog where user is explained that map is not available because required permission was not granted.
    private void showPermissionDeniedDialog(){
        String title = getResources().getString(R.string.map_not_available);
        String message = getResources().getString(R.string.required_permission_denied_message);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        dialog.create().show();
    }

    //Informative dialog where user can see why permission is required.
    //User can verify to deny this permission or show permission request window again.
    private void showInformativeDialog(){
        String title = getResources().getString(R.string.permission_denied);
        String message = getResources().getString(R.string.informative_message_for_permissions);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ask again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] permissions = { Manifest.permission.READ_PHONE_STATE };
                        requestPermissions(permissions, MapActivity.READ_PHONE_STATE_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        openMapButton.setClickable(true);
                    }
                });
        dialog.create().show();
    }

    private boolean permissionGranted(int[] grantResults){
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private String getDateAndTimeAsText(ZonedDateTime date) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.getDayOfMonth() + ".");
        sb.append(date.getMonthValue() + ".");
        sb.append(date.getYear() + " ");
        sb.append(date.getHour() + ":");
        int minute = date.getMinute();
        if (minute >= 10) {
            sb.append(minute);
        } else {
            sb.append("0" + minute);
        }

        return sb.toString();
    }
}