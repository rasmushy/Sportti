package fi.sportti.app.ui.activities;

import static fi.sportti.app.datastorage.room.TypeConversionUtilities.zonedDateToUnixTime;
import static fi.sportti.app.ui.utilities.CalorieConversionUtilities.*;
import static fi.sportti.app.ui.utilities.DialogUtilities.openGiveAveragePulse;
import static fi.sportti.app.ui.utilities.DialogUtilities.openGiveCalories;
import static fi.sportti.app.ui.utilities.TimeConversionUtilities.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
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

import android.widget.SeekBar;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


import fi.sportti.app.App;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.location.RouteContainer;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.adapters.ExerciseSaveAdapter;

import fi.sportti.app.ui.viewmodels.MainViewModel;

/**
 * Activity after StartExerciseActivity.
 * For user to view and save recorded exercise.
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaveExerciseActivity extends AppCompatActivity {
    private static final String TAG = "SaveExerciseActivity";
    private MainViewModel mainViewModel;

    //Dialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    //User
    private User user;
    private ZonedDateTime zonedStartTime;
    private ZonedDateTime zonedDateEnd;

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
        setContentView(R.layout.activity_save_exercise);
        //Initialize
        exerciseListView = findViewById(R.id.saveexercise_listview);
        openMapButton = findViewById(R.id.saveexercise_button_open_map);
        exerciseDataList = new ArrayList<>();
        dialogBuilder = new AlertDialog.Builder(this);

        //Mainviewmodel access
        mainViewModel = MainActivity.getMainViewModel();
        //Our user
        user = mainViewModel.getFirstUser();

        //Method for intent data collection
        getRecordedData();
        //Set openMapButton invisible if user did not want to save route.
        if (!RouteContainer.getInstance().hasRoute()) {
            openMapButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method to gather all Intent data what we recorded on StartExerciseActivity
     *
     * @author Rasmus Hyyppä
     */
    private void getRecordedData() {
        Intent intent = getIntent();
        exerciseDataArray = intent.getStringArrayExtra(StartExerciseActivity.REPLY_RECORDED_EXERCISE);

        //Sport type
        String exerciseName = exerciseDataArray[0];

        //Dates
        String exerciseStartDate = exerciseDataArray[1];
        String exerciseEndDate = exerciseDataArray[2];
        zonedStartTime = ZonedDateTime.parse(exerciseStartDate);
        zonedDateEnd = ZonedDateTime.parse(exerciseEndDate);
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
        initializeListView();
    }


    /**
     * Method for initializing our listview with custom adapter.
     *
     * @author Rasmus Hyyppä
     */
    private void initializeListView() {

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

    /**
     * Method to set on click listeners for listview.
     * Mainly to make sure user can still change Average Heartrate & calories.
     *
     * @author Rasmus Hyyppä
     */
    private void setItemClickListenerForListView() {
        exerciseListView.setOnItemClickListener((adapterView, viewFromListView, position, l) -> {

            //Initialize our textview that has our data value (kcal/bpm)
            TextView textViewForData = (TextView) viewFromListView.findViewById(R.id.saveexercise_listview_textview_data);

            switch (position) { //Switch case for different options (might change this to if)

                case 4: //position == 4 -> Calories
                    final View giveCaloriesPopUp = getLayoutInflater().inflate(R.layout.pop_up_give_calories, null);
                    dialogBuilder.setView(giveCaloriesPopUp);
                    dialog = dialogBuilder.create();
                    //On dismiss dialog updates ui & data arrays
                    dialog.setOnDismissListener(dialog -> {
                        exerciseDataArray[3] = textViewForData.getText().toString();
                    });
                    //Initialize dialog with method DialogUtilities.openGiveCalories
                    openGiveCalories(giveCaloriesPopUp, dialog, textViewForData);
                    break;

                case 5: //position == 5 -> Avg HeartRate
                    final View giveAveragePulsePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_average_pulse, null);
                    dialogBuilder.setView(giveAveragePulsePopUp);
                    dialog = dialogBuilder.create();
                    //On dismiss dialog updates ui & data arrays
                    dialog.setOnDismissListener(dialog -> {
                        //User heart rate
                        int heartRate = Integer.parseInt(textViewForData.getText().toString());
                        //Set heart rate to our data arr
                        exerciseDataArray[4] = textViewForData.getText().toString();
                        //Set calories if heart rate is between 89 or 190
                        if (heartRate > 89 && heartRate < 190) {
                            //Calorie TextView
                            TextView textViewForCalories = exerciseListView
                                    .getChildAt(4)
                                    .findViewById(R.id.saveexercise_listview_textview_data);
                            //Calculate calories for that textview
                            int calories = getCaloriesWithHeartRate(user, heartRate, zonedStartTime, zonedDateEnd);
                            Log.d(TAG, "textViewForCalories.setText " + calories);
                            //Add Finished result to string arr and Textview
                            exerciseDataArray[3] = Integer.toString(calories);
                            textViewForCalories.setText(Integer.toString(calories));
                        }
                    });
                    //Initialize dialog with method DialogUtilities.openGiveAveragePulse
                    openGiveAveragePulse(giveAveragePulsePopUp, dialog, textViewForData);
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

            //Integer to get null check (Currently useless cause its set to 1)
            Integer userId = 1;

            //Create exercise from our data
            Exercise exercise = new Exercise(exerciseDataArray[0], userId,
                    ZonedDateTime.parse(exerciseDataArray[1]),
                    ZonedDateTime.parse(exerciseDataArray[2]),
                    Integer.parseInt(exerciseDataArray[3]),
                    Integer.parseInt(exerciseDataArray[4]),
                    exerciseDataArray[5],
                    Double.parseDouble(exerciseDataArray[6]),
                    exerciseDataArray[7]);

            //Inserting exercise to database
            mainViewModel.insertExercise(exercise);
            RouteContainer.getInstance().resetRoute();

            Log.d(TAG, "savePressed() --> Exercise saved to database" + exercise);

            //Send us back to MainActivity page after saving data.
            Intent intentForMainActivity = new Intent(this, MainActivity.class);

            // FLAG_ACTIVITY_CLEAR_TOP to clear activity stack:  https://developer.android.com/guide/components/activities/tasks-and-back-stack
            intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentForMainActivity);
        }
    }

    /**
     * Override back press to make sure user really wants to leave activity
     * without saving it to database.
     *
     * @Override onBackPressed()
     * @author Rasmus Hyyppä
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        new AlertDialog.Builder(this)
                .setTitle("Exit before saving")
                .setMessage("Are you sure you want to exit without saving exercise?")
                .setNegativeButton(android.R.string.no, null)
                //Generic lambda arg0,arg1 for yes & no alertdialog
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    Intent intentForMainActivity = new Intent(getBaseContext(), MainActivity.class);
                    intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentForMainActivity);
                }).create().show();
    }

    /**
     * Menu for SaveExerciseActivity, it includes trash bin that ask if you want to delete exercise instead of saving
     *
     * @param menu
     * @return
     */
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
        Log.d(TAG, "onPause(): ");
        if (dialog != null) { //See if there is any active dialogs active when going onPause
            dialog.dismiss(); //If there is dismiss it.
        }
    }


    /**
     * @author Jukka-Pekka Jaakkola
     */


    /**
     * Method attached to button in layout. Checks if app has required permissions to display map.
     * If yes, opens map. Otherwise requests required permissions from user.
     * @params view Required parameter for methods that are attached to buttons in layout.
     * */
    public void openMapButtonClicked(View view) {
        //Check if app has READ_PHONE_STATE permission which is required to display map.
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            openMap();
        }
        //If not, request permission and disable openMapButton until permission request process is finished.
        else {
            openMapButton.setClickable(false);
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            requestPermissions(permissions, App.PERMISSION_CODE_READ_PHONE_STATE);
        }
    }

    private void openMap() {
        //Starts Map Activity and passes route as extra.
        Intent mapIntent = new Intent(this, MapActivity.class);
        String route = RouteContainer.getInstance().getRouteAsText();
        mapIntent.putExtra(MapActivity.EXTRA_ROUTE, route);
        startActivity(mapIntent);
    }

    //This method is called by Android when user responds to permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == App.PERMISSION_CODE_READ_PHONE_STATE) {
            if (permissionGranted(grantResults)) {
                openMapButton.setClickable(true);
                openMap();
            } else {
                //Check if app should show informative message to user about why this permission is required.
                //Android System decides if it is required or not.
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    showInformativeDialog();
                } else {
                    openMapButton.setClickable(true);
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
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


    private void showInformativeDialog() {
        //Informative dialog where user can see why permission is required.
        //User can verify to deny this permission or show permission request window again.

        //Create buttons for dialog.
        //With positive button, show permission request window again.
        DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, App.PERMISSION_CODE_READ_PHONE_STATE);
            }
        };
        //Negative button just closes this dialog because user verified not to give permission.
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

    private boolean permissionGranted(int[] grantResults) {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;


    }
}

