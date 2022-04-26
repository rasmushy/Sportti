package fi.sportti.app.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fi.sportti.app.R;

public class NewManualExerciseActivity extends AppCompatActivity {

    private TextView textViewStartTime, textViewDuration, textViewDistance, textViewCalories, textViewPulse;
    private EditText editTextComment;
    private int startTimeHour, startTimeMinute, distance, duration, calories, pulse;
    private long startDateLong;
    private String startDate;

    private AlertDialog.Builder dialogBuilder;
    private Spinner spinnerSelectExercise;
    private ArrayAdapter<CharSequence> adapter;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateAndTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_manual_exercise);

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
        textViewDuration.setText("0h0min");
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
                        startTimeMinute = materialTimePicker.getMinute();

                        startDate = dateFormatter.format(startDateLong);

                        textViewStartTime.setText(startDate + " " + startTimeHour + ":" + startTimeMinute);
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

        Button buttonSaveDurationPopUp = selectDurationPopUp.findViewById(R.id.buttonSaveDurationPopUp);
        SeekBar seekerBarHoursPopUp = selectDurationPopUp.findViewById(R.id.seekBarHourPopUp);
        SeekBar seekerBarMinutesPopUp = selectDurationPopUp.findViewById(R.id.seekBarMinPopUp);
        TextView textViewDurationPopUp = selectDurationPopUp.findViewById(R.id.textViewTimePopUp);
        textViewDurationPopUp.setText(seekerBarHoursPopUp.getProgress() + "h " + (seekerBarMinutesPopUp.getProgress() * 2 + "min"));

        seekerBarHoursPopUp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewDurationPopUp.setText(seekerBarHoursPopUp.getProgress() + "h " + (seekerBarMinutesPopUp.getProgress() * 2 + "min"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                duration = (seekerBarHoursPopUp.getProgress() * 60) + (seekerBarMinutesPopUp.getProgress() * 2);
            }
        });

        seekerBarMinutesPopUp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewDurationPopUp.setText(seekerBarHoursPopUp.getProgress() + "h " + (seekerBarMinutesPopUp.getProgress() * 2 + "min"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                duration = (seekerBarHoursPopUp.getProgress() * 60) + (seekerBarMinutesPopUp.getProgress() * 2);
            }
        });

        buttonSaveDurationPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                textViewDuration.setText(seekerBarHoursPopUp.getProgress() + "h " + (seekerBarMinutesPopUp.getProgress() * 2 + "min"));
            }
        });
    }

    public void openGiveDistance(View view){
        final  View giveDistancePopUp = getLayoutInflater().inflate(R.layout.pop_up_give_distance, null);
        dialogBuilder.setView(giveDistancePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button buttonSaveDistance = giveDistancePopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
        SeekBar seekBarDistance = giveDistancePopUp.findViewById(R.id.seekBarDistance);
        TextView textViewSeekBarDistanceValue = giveDistancePopUp.findViewById(R.id.textViewSeekBarDistanceValue);
        textViewSeekBarDistanceValue.setText(seekBarDistance.getProgress() * 500 + " meters");

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //TODO Auto-generated method
                textViewSeekBarDistanceValue.setText(seekBarDistance.getProgress() * 500 + " meters");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO Auto-generated method
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distance = seekBarDistance.getProgress() * 500;
            }
        });

        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                textViewDistance.setText(distance + " meters");
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

    }

    private void saveExerciseData(){

    }
}