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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewManualExerciseActivity extends AppCompatActivity {
    private static final String TAG = "SaveManualExerciseAct";

    private MainViewModel mainViewModel;
    private User user;
    String[] exerciseDataArray;

    private TextView textViewStartTime, textViewDuration, textViewDistance, textViewCalories, textViewPulse;
    private EditText editTextComment;
    private int startYear, startMonth, startDay, startTimeHour, startTimeMinute, distance, duration, durationHours, durationMinutes, durationSeconds, calories, pulse;
    private double distanceDouble;
    private long startDateLong;
    private String secondsString,minutesString,hoursString, distanceString, exerciseType, startDate, startDateAndTime, comment;
    private ZonedDateTime startTimeData, endTimeData;

    private AlertDialog.Builder dialogBuilder;
    private Spinner spinnerSelectExercise;
    private ArrayAdapter<CharSequence> adapter;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateAndTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_manual_exercise);

        exerciseDataArray = new String[8];

        mainViewModel = MainActivity.getMainViewModel();
        user = mainViewModel.getFirstUser();

        spinnerSelectExercise = findViewById(R.id.spinnerSelectActivity);
        adapter = ArrayAdapter.createFromResource(this, R.array.exercise_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectExercise.setAdapter(adapter);

        dialogBuilder = new AlertDialog.Builder(this);

        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewPulse = findViewById(R.id.textViewPulse);
        editTextComment = findViewById(R.id.editTextTextComment);

        textViewStartTime.setText(dateAndTimeFormatter.format(new Date()));
        textViewDuration.setText("0h 0min 0sec");
        textViewDistance.setText("0 meters");
        textViewCalories.setText("0 calories");
        textViewPulse.setText("0 bpm");
    }



    public void openSelectStartTime(View view){
        final View selectStartDatePopUp = getLayoutInflater().inflate(R.layout.pop_up_select_start_date, null);
        dialogBuilder.setView(selectStartDatePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        CalendarView calendarView = selectStartDatePopUp.findViewById(R.id.calendarViewPopUp);
        Button buttonSaveTime = selectStartDatePopUp.findViewById(R.id.buttonNextPopUp);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                startDateLong = calendar.getTimeInMillis();
                startYear = year;
                startMonth = month;
                startDay = day;
            }
        });

        buttonSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");

                materialTimePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        startTimeHour = materialTimePicker.getHour();
                        startTimeMinute = 0;
                        startTimeMinute = materialTimePicker.getMinute();

                        startDate = dateFormatter.format(startDateLong);

                        textViewStartTime.setText(startDate + " " + startTimeHour + ":" + startTimeMinute);
                        startTimeData = ZonedDateTime.of(startYear, startMonth, startDay, startTimeHour, startTimeMinute, 0,0, ZoneId.systemDefault());
                        exerciseDataArray[1] = startTimeData.toString();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public  void openSelectDuration(View view){
        final View selectDurationPopUp = getLayoutInflater().inflate(R.layout.pop_up_select_duration, null);
        dialogBuilder.setView(selectDurationPopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSaveDistance = selectDurationPopUp.findViewById(R.id.buttonSaveDurationPopUp);
        ImageButton buttonSecondsPlus = selectDurationPopUp.findViewById(R.id.imageButtonSecondsPlus);
        ImageButton buttonSecondsMinus = selectDurationPopUp.findViewById(R.id.imageButtonSecondsMinus);
        ImageButton buttonMinutesPlus = selectDurationPopUp.findViewById(R.id.imageButtonMinutesPlus);
        ImageButton buttonMinutesMinus = selectDurationPopUp.findViewById(R.id.imageButtonMinutesMinus);
        ImageButton buttonHoursPlus = selectDurationPopUp.findViewById(R.id.imageButtonHoursPlus);
        ImageButton buttonHoursMinus = selectDurationPopUp.findViewById(R.id.imageButtonHoursMinus);
        TextView textViewDurationPopUp = selectDurationPopUp.findViewById(R.id.textViewDistancePopUp);

        textViewDurationPopUp.setText("00h00m00s");

        CounterUtility counterUtilitySeconds = new CounterUtility(0,59,0,1,true);
        CounterUtility counterUtilityMinutes = new CounterUtility(0,59,0,1,true);
        CounterUtility counterUtilityHours = new CounterUtility(0,99,0,1,true);

        buttonSecondsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilitySeconds.addToCounter();

                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonSecondsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilitySeconds.minusToCounter();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonMinutesPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityMinutes.addToCounter();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonMinutesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityMinutes.minusToCounter();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonHoursPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityHours.addToCounter();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonHoursMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityHours.minusToCounter();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if(durationSeconds < 10){
                    secondsString = "0" + durationSeconds;
                }else {
                    secondsString = String.valueOf(durationSeconds);
                }
                if(durationMinutes < 10){
                    minutesString = "0" + durationMinutes;
                }else {
                    minutesString = String.valueOf(durationMinutes);
                }
                if(durationHours < 10){
                    hoursString = "0" + durationHours;
                }else {
                    hoursString = String.valueOf(durationHours);
                }

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                endTimeData = startTimeData.plusHours(durationHours);
                endTimeData.plusMinutes(durationMinutes);
                endTimeData.plusSeconds(durationSeconds);
                exerciseDataArray[2] = endTimeData.toString();
                textViewDuration.setText(counterUtilityHours.returnCounter() + "h " + counterUtilityMinutes.returnCounter() + "min " + counterUtilitySeconds.returnCounter() + "sec");
            }
        });
    }

    public void openGiveDistance(View view){
        final  View giveDistancePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_distance, null);
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

        CounterUtility counterUtilityDistance = new CounterUtility(0,99999,0,1);



        imageButtonPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(10);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(10);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();

                if(distance < 10){
                    distanceString = "0000" +distance + "m";
                }else if (distance < 100){
                    distanceString = "000" +distance + "m";
                }else if (distance < 1000){
                    distanceString = "00" +distance + "m";
                }else if (distance < 10000){
                    distanceString = "0" +distance + "m";
                }else {
                    distanceString = distance + "m";
                }
                textViewDistanceValue.setText(distanceString);
            }
        });

        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                distanceDouble = distance;
                exerciseDataArray[6] = String.valueOf(distanceDouble);
                textViewDistance.setText(counterUtilityDistance.returnCounter() + " meters");
            }
        });
    }

    public  void openGiveCalories(View view){
        final View giveCaloriesPopUp = getLayoutInflater().inflate(R.layout.pop_up_give_calories, null);
        dialogBuilder.setView(giveCaloriesPopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSaveCalories = giveCaloriesPopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
        SeekBar seekBarCalories = giveCaloriesPopUp.findViewById(R.id.seekBarCalories);
        TextView textViewSeekBarCaloriesValue = giveCaloriesPopUp.findViewById(R.id.textViewSeekBarCaloriesValue);
        textViewSeekBarCaloriesValue.setText(seekBarCalories.getProgress() * 10 + " calories");

        seekBarCalories.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewSeekBarCaloriesValue.setText(seekBarCalories.getProgress() * 10 + " calories");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calories = seekBarCalories.getProgress() * 10;
                exerciseDataArray[3] = String.valueOf(calories);
            }
        });

        buttonSaveCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                textViewCalories.setText(calories+ " calories");
            }
        });
    }

    public void openGiveAveragePulse(View view){
        final View giveAveragePulsePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_average_pulse, null);
        dialogBuilder.setView(giveAveragePulsePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSavePulse = giveAveragePulsePopUp.findViewById(R.id.buttonSavePulsePopUp);
        SeekBar seekBarPulse = giveAveragePulsePopUp.findViewById(R.id.seekBarPulse);
        TextView textViewSeekBarPulseValue = giveAveragePulsePopUp.findViewById(R.id.textViewSeekBarPulseValue);
        textViewSeekBarPulseValue.setText((seekBarPulse.getProgress() * 5 + 50) + "bpm");

        seekBarPulse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewSeekBarPulseValue.setText((seekBarPulse.getProgress() * 5 + 50) + " bpm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pulse = seekBarPulse.getProgress() * 5 + 50;
                exerciseDataArray[4] = String.valueOf(pulse);
            }
        });

        buttonSavePulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                textViewPulse.setText(pulse + " bpm");
            }
        });
    }

    public void onClickSaveExercise(View view){
        saveExerciseData();
        Log.d(TAG, "Save exercise pressed");
    }

    private void saveExerciseData(){
        exerciseType = spinnerSelectExercise.getSelectedItem().toString();
        exerciseDataArray[0] = exerciseType;

        if(editTextComment.getText().toString().length() > 0){
            comment = editTextComment.getText().toString();
        }

        if(exerciseDataArray != null){
            Integer userId = 1; //Integer to get null check (Currently useless cause its set to 1)
            //Create exercise from our data
            Exercise exercise = new Exercise(exerciseType, userId, startTimeData, endTimeData,
                    calories, pulse, "", distanceDouble, comment);
            mainViewModel.insertExercise(exercise);
            Log.d(TAG, "savePressed() --> Exercise saved to database" +
                    "\n   type: " + exerciseDataArray[0] +
                    "\n   user id: " + userId +
                    "\n   start d: " + exerciseDataArray[1] +
                    "\n   end d: " + exerciseDataArray[2] +
                    "\n   calories: " + exerciseDataArray[3] +
                    "\n   avg HR: " + exerciseDataArray[4] +
                    "\n   distance: " + exerciseDataArray[6] +
                    "\n   comment: " + exerciseDataArray[7]);

            Intent intentForMainActivity = new Intent(this, MainActivity.class);
            startActivity(intentForMainActivity);
        }

    }
}