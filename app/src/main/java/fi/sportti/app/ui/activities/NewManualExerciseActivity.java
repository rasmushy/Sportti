package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.utilities.CounterUtility;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/**
 * User wants to manually add exercise or activity he has done in past
 *
 * @author Lassi Bågman
 * @version 0.5
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewManualExerciseActivity extends AppCompatActivity {

    //Variables needed to share data between methods
    private static final String TAG = "SaveManualExerciseAct";

    private MainViewModel mainViewModel;
    private AlertDialog.Builder dialogBuilder;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateAndTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Spinner spinnerSelectExercise;
    private TextView textViewStartTime, textViewDuration, textViewDistance, textViewCalories, textViewPulse;
    private EditText editTextComment;

    private String[] exerciseDataArray;
    private int startYear, startMonth, startDay, startTimeHour, startTimeMinute, distance,
            durationHours, durationMinutes, calories, pulse;
    private double distanceDouble;
    private long startDateLong;
    private boolean dateSelected;
    private String minutesString, hoursString, distanceString, caloriesString, pulseString,
            startDate, comment;
    private ZonedDateTime startTimeData, endTimeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_manual_exercise);

        //Define string array for storing the user inputs in string format
        exerciseDataArray = new String[8];

        //Set view model for saving the data to the database
        mainViewModel = MainActivity.getMainViewModel();
        User user = mainViewModel.getFirstUser();

        //Set up spinner for this activity
        spinnerSelectExercise = findViewById(R.id.spinnerSelectActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectExercise.setAdapter(adapter);

        //Define dialog builder for pop ups
        dialogBuilder = new AlertDialog.Builder(this);

        //Get views for this activity
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewPulse = findViewById(R.id.textViewPulse);
        editTextComment = findViewById(R.id.editTextTextComment);

        //Get time and save it
        startTimeData = ZonedDateTime.now();
        exerciseDataArray[1] = startTimeData.toString();

        //Sets up defaults for text views
        textViewStartTime.setText(dateAndTimeFormatter.format(new Date()));
        textViewDuration.setText("0h 0min");
        textViewDistance.setText("0 meters");
        textViewCalories.setText("0 calories");
        textViewPulse.setText("0 bpm");

    }

    /**
     * Method for opening and handling start time selection pop ups
     *
     * @param view View that called method
     */
    public void openSelectStartTime(View view) {

        //Sets up pop up and shows it
        final View selectStartDatePopUp = getLayoutInflater().inflate(R.layout.pop_up_select_start_date, null);
        dialogBuilder.setView(selectStartDatePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        //Boolean to prevent crashing
        dateSelected = false;

        //Finds views in pop up
        CalendarView calendarView = selectStartDatePopUp.findViewById(R.id.calendarViewPopUp);
        Button buttonSaveTime = selectStartDatePopUp.findViewById(R.id.buttonNextPopUp);

        //Listener for when user if user changes the date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                startDateLong = calendar.getTimeInMillis();
                startYear = year;
                startMonth = month + 1;
                startDay = day;
                dateSelected = true;
            }
        });

        //Listener for when user wants to advance to time picker
        buttonSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateSelected) {
                    MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(12)
                            .setMinute(10)
                            .build();
                    materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");

                    //Listener to save when user dismisses time picker
                    materialTimePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            startTimeHour = materialTimePicker.getHour();
                            startTimeMinute = 0;
                            startTimeMinute = materialTimePicker.getMinute();

                            startDate = dateFormatter.format(startDateLong);

                            //Updates text view with user inputs
                            textViewStartTime.setText(startDate + " " + startTimeHour + ":" + startTimeMinute);
                            startTimeData = ZonedDateTime.of(startYear, startMonth, startDay,
                                    startTimeHour, startTimeMinute, 0, 0, ZoneId.systemDefault());
                            exerciseDataArray[1] = startTimeData.toString();
                            dialog.dismiss();
                        }
                    });
                } else {
                    //Toast if user has not selected date and tries to advance to time picker
                    Toast.makeText(getApplicationContext(), "Select date!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Method for opening and handling duration pop up
     *
     * @param view View that called method
     */
    public void openSelectDuration(View view) {
        //Sets up pop up and shows it
        final View selectDurationPopUp = getLayoutInflater().inflate(R.layout.pop_up_select_duration, null);
        dialogBuilder.setView(selectDurationPopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        //Finds views in pop up
        Button buttonSaveDistance = selectDurationPopUp.findViewById(R.id.buttonSaveDurationPopUp);
        ImageButton buttonMinutesPlus = selectDurationPopUp.findViewById(R.id.imageButtonMinutesPlus);
        ImageButton buttonMinutesMinus = selectDurationPopUp.findViewById(R.id.imageButtonMinutesMinus);
        ImageButton buttonHoursPlus = selectDurationPopUp.findViewById(R.id.imageButtonHoursPlus);
        ImageButton buttonHoursMinus = selectDurationPopUp.findViewById(R.id.imageButtonHoursMinus);
        TextView textViewDurationPopUp = selectDurationPopUp.findViewById(R.id.textViewDistancePopUp);

        //Sets default text for pop up text view
        textViewDurationPopUp.setText("00h 00m");

        CounterUtility counterUtilityMinutes = new CounterUtility(0, 59, 0, 1, true);
        CounterUtility counterUtilityHours = new CounterUtility(0, 99, 0, 1, true);

        //Listener for minutes plus button
        buttonMinutesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityMinutes.addToCounter(5);

                /*This is section of code is same for all the following onClick methods. Java does not support
                making methods inside methods so for time being easiest way was to copy it to all corresponding
                onClick methods. Code blocs responsibility is to update the pop ups text view with user inputs
                */

                //Get updated values
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                //Check if we want to add zeros to the front of the number shown
                timeToString();

                //Updates text view with corresponding value
                textViewDurationPopUp.setText(hoursString + "h " + minutesString + "m");
            }
        });

        // Listener for minutes minus button
        buttonMinutesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityMinutes.minusToCounter(5);

                //See comment at line 217
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();
                timeToString();
                textViewDurationPopUp.setText(hoursString + "h " + minutesString + "m");
            }
        });

        // -//-
        buttonHoursPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityHours.addToCounter();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();
                timeToString();
                textViewDurationPopUp.setText(hoursString + "h " + minutesString + "m");
            }
        });

        // -//-
        buttonHoursMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityHours.minusToCounter();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();
                timeToString();
                textViewDurationPopUp.setText(hoursString + "h " + minutesString + "m");
            }
        });

        // -//-
        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                durationHours = counterUtilityHours.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                endTimeData = startTimeData;
                endTimeData = endTimeData.plusHours(durationHours).plusMinutes(durationMinutes);
                exerciseDataArray[2] = endTimeData.toString();
                textViewDuration.setText(counterUtilityHours.returnCounter() + "h " +
                        counterUtilityMinutes.returnCounter() + "min");
            }
        });
    }

    /**
     * Method for opening and handling distance pop up. Comments from above count here as well
     *
     * @param view View that called method
     */
    public void openGiveDistance(View view) {
        final View giveDistancePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_distance, null);
        dialogBuilder.setView(giveDistancePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        ImageButton imageButtonPlus10 = giveDistancePopUp.findViewById(R.id.imageButtonPlus10);
        ImageButton imageButtonPlus100 = giveDistancePopUp.findViewById(R.id.imageButtonPlus100);
        ImageButton imageButtonPlus1000 = giveDistancePopUp.findViewById(R.id.imageButtonPlus1000);
        ImageButton imageButtonMinus10 = giveDistancePopUp.findViewById(R.id.imageButtonMinus10);
        ImageButton imageButtonMinus100 = giveDistancePopUp.findViewById(R.id.imageButtonMinus100);
        ImageButton imageButtonMinus1000 = giveDistancePopUp.findViewById(R.id.imageButtonMinus1000);
        TextView textViewDistanceValue = giveDistancePopUp.findViewById(R.id.textViewDistanceValue);
        Button buttonSaveDistance = giveDistancePopUp.findViewById(R.id.buttonSaveCaloriesPopUp);

        textViewDistanceValue.setText("00000m");

        CounterUtility counterUtilityDistance = new CounterUtility(0, 99999, 0, 1);


        imageButtonPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(10);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(10);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();
                distanceToString();
                textViewDistanceValue.setText(distanceString);
            }
        });

        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                distance = counterUtilityDistance.returnCounterInt();
                distanceDouble = distance;
                distanceDouble = distanceDouble / 1000;
                exerciseDataArray[6] = String.valueOf(distanceDouble);
                textViewDistance.setText(counterUtilityDistance.returnCounter() + " meters");
            }
        });
    }

    /**
     * Method for opening and handling calories pop up. Comments from above count here as well
     *
     * @param view View that called method
     */
    public void openGiveCalories(View view) {
        final View giveCaloriesPopUp = getLayoutInflater().inflate(R.layout.pop_up_give_calories, null);
        dialogBuilder.setView(giveCaloriesPopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSaveCalories = giveCaloriesPopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
        ImageButton imageButtonCaloriesPlus = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus);
        ImageButton imageButtonCaloriesPlus10 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus10);
        ImageButton imageButtonCaloriesPlus100 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus100);
        ImageButton imageButtonCaloriesMinus = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus);
        ImageButton imageButtonCaloriesMinus10 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus10);
        ImageButton imageButtonCaloriesMinus100 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus100);
        TextView textViewCaloriesPopUp = giveCaloriesPopUp.findViewById(R.id.textViewCaloriesPopUpValue);

        textViewCaloriesPopUp.setText("0000kcal");

        CounterUtility counterUtilityCalories = new CounterUtility(0, 9999, 0, 1, true);

        imageButtonCaloriesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.addToCounter();
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.addToCounter(10);
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesPlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.addToCounter(100);
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter();
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter(10);
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter(100);
                calories = counterUtilityCalories.returnCounterInt();
                caloriesToString();
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        buttonSaveCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                calories = counterUtilityCalories.returnCounterInt();
                exerciseDataArray[3] = String.valueOf(calories);
                textViewCalories.setText(calories + " kcal");
            }
        });
    }

    /**
     * Method for opening and handling pulse pop up. Comments from above count here as well
     *
     * @param view View that called method
     */
    public void openGiveAveragePulse(View view) {
        final View giveAveragePulsePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_average_pulse, null);
        dialogBuilder.setView(giveAveragePulsePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSavePulse = giveAveragePulsePopUp.findViewById(R.id.buttonSavePulsePopUp);
        ImageButton imageButtonPulsePlus = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus);
        ImageButton imageButtonPulsePlus10 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus10);
        ImageButton imageButtonPulsePlus100 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus100);
        ImageButton imageButtonPulseMinus = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus);
        ImageButton imageButtonPulseMinus10 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus10);
        ImageButton imageButtonPulseMinus100 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus100);
        TextView textViewPulsePopUp = giveAveragePulsePopUp.findViewById(R.id.textViewPulseValuePopUp);

        textViewPulsePopUp.setText("060bpm");

        CounterUtility counterUtilityPulse = new CounterUtility(40, 240, 60, 1);

        imageButtonPulsePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.addToCounter();
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulsePlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.addToCounter(10);
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulsePlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.addToCounter(100);
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter();
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter(10);
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter(100);
                pulse = counterUtilityPulse.returnCounterInt();
                pulseToString();
                textViewPulsePopUp.setText(pulseString);
            }
        });

        buttonSavePulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pulse = counterUtilityPulse.returnCounterInt();
                exerciseDataArray[4] = String.valueOf(pulse);
                textViewPulse.setText(pulse + " bpm");
            }
        });
    }

    private void timeToString(){
        if (durationMinutes < 10) {
            minutesString = "0" + durationMinutes;
        } else {
            minutesString = String.valueOf(durationMinutes);
        }
        if (durationHours < 10) {
            hoursString = "0" + durationHours;
        } else {
            hoursString = String.valueOf(durationHours);
        }
    }

    private void distanceToString(){
        if (distance < 10) {
            distanceString = "0000" + distance + "m";
        } else if (distance < 100) {
            distanceString = "000" + distance + "m";
        } else if (distance < 1000) {
            distanceString = "00" + distance + "m";
        } else if (distance < 10000) {
            distanceString = "0" + distance + "m";
        } else {
            distanceString = distance + "m";
        }
    }

    private void caloriesToString(){
        if (calories < 10) {
            caloriesString = "000" + calories + "kcal";
        } else if (calories < 100) {
            caloriesString = "00" + calories + "kcal";
        } else if (calories < 1000) {
            caloriesString = "0" + calories + "kcal";
        } else {
            caloriesString = calories + "kcal";
        }
    }

    private void pulseToString(){
        if (pulse < 10) {
            pulseString = "00" + pulse + "bpm";
        } else if (pulse < 100) {
            pulseString = "0" + pulse + "bpm";
        } else {
            pulseString = pulse + "bpm";
        }
    }

    /**
     * Method for "save" button click
     *
     * @param view View that called method
     */
    public void onClickSaveExercise(View view) {
        saveExerciseData();
    }

    /**
     * Method for converting some of the user inputs and sending them to the database
     *
     */
    private void saveExerciseData() {

        //Get user selection from spinner
        String exerciseType = spinnerSelectExercise.getSelectedItem().toString();
        exerciseDataArray[0] = exerciseType;

        //Check if user has made a comment
        if (editTextComment.getText().toString().length() > 0) {
            comment = editTextComment.getText().toString();
            exerciseDataArray[7] = comment;
        }

        //Check if there is user input data in the array
        if (exerciseDataArray != null) {

            //Check if user has at least selected start date and time, and duration of exercise
            if (startTimeData == null || endTimeData == null) {
                //Toast to warn user
                Toast.makeText(getApplicationContext(), "At least select start date and duration!",
                        Toast.LENGTH_LONG).show();
            } else {
                Integer userId = 1; //Integer to get null check (Currently useless cause its set to 1)

                //Create exercise from our data and send it to our database
                Exercise exercise = new Exercise(exerciseType, userId, startTimeData, endTimeData,
                        calories, pulse, "", distanceDouble, comment);
                mainViewModel.insertExercise(exercise);
                Log.d(TAG, "savePressed() --> Exercise saved to database" + exercise);

                //Intent for moving back to main page
                Intent intentForMainActivity = new Intent(this, MainActivity.class);
                // Clears activity history to prevent moving back
                intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intentForMainActivity);
            }
        }
    }
}