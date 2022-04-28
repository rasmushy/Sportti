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

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewManualExerciseActivity extends AppCompatActivity {
    private static final String TAG = "SaveManualExerciseAct";

    private MainViewModel mainViewModel;
    private User user;
    String[] exerciseDataArray;

    private TextView textViewStartTime, textViewDuration, textViewDistance, textViewCalories, textViewPulse;
    private EditText editTextComment;
    private int startYear, startMonth, startDay, startTimeHour, startTimeMinute, distance,
            durationHours, durationMinutes, durationSeconds, calories, pulse;
    private double distanceDouble;
    private long startDateLong;
    private boolean dateSelected;
    private String secondsString, minutesString, hoursString, distanceString, caloriesString,
            pulseString, exerciseType, startDate, comment;
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
        adapter = ArrayAdapter.createFromResource(this, R.array.exercise_type_list,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectExercise.setAdapter(adapter);

        dialogBuilder = new AlertDialog.Builder(this);

        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewPulse = findViewById(R.id.textViewPulse);
        editTextComment = findViewById(R.id.editTextTextComment);

        startTimeData = ZonedDateTime.now();
        exerciseDataArray[1] = startTimeData.toString();

        textViewStartTime.setText(dateAndTimeFormatter.format(new Date()));
        textViewDuration.setText("0h 0min 0sec");
        textViewDistance.setText("0 meters");
        textViewCalories.setText("0 calories");
        textViewPulse.setText("0 bpm");

    }


    public void openSelectStartTime(View view) {
        final View selectStartDatePopUp = getLayoutInflater().inflate(R.layout.pop_up_select_start_date, null);
        dialogBuilder.setView(selectStartDatePopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        dateSelected = false;

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
                dateSelected = true;
            }
        });

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

                    materialTimePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            startTimeHour = materialTimePicker.getHour();
                            startTimeMinute = 0;
                            startTimeMinute = materialTimePicker.getMinute();

                            startDate = dateFormatter.format(startDateLong);

                            textViewStartTime.setText(startDate + " " + startTimeHour + ":" + startTimeMinute);
                            startTimeData = ZonedDateTime.of(startYear, startMonth, startDay,
                                    startTimeHour, startTimeMinute, 0, 0, ZoneId.systemDefault());
                            exerciseDataArray[1] = startTimeData.toString();
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Select date!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void openSelectDuration(View view) {
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

        CounterUtility counterUtilitySeconds = new CounterUtility(0, 59, 0, 1, true);
        CounterUtility counterUtilityMinutes = new CounterUtility(0, 59, 0, 1, true);
        CounterUtility counterUtilityHours = new CounterUtility(0, 99, 0, 1, true);

        buttonSecondsPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilitySeconds.addToCounter();

                durationSeconds = counterUtilitySeconds.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationHours = counterUtilityHours.returnCounterInt();

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                if (durationSeconds < 10) {
                    secondsString = "0" + durationSeconds;
                } else {
                    secondsString = String.valueOf(durationSeconds);
                }
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

                textViewDurationPopUp.setText(hoursString + "h" + minutesString + "m" + secondsString + "s");
            }
        });

        buttonSaveDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                durationHours = counterUtilityHours.returnCounterInt();
                durationMinutes = counterUtilityMinutes.returnCounterInt();
                durationSeconds = counterUtilitySeconds.returnCounterInt();
                endTimeData = startTimeData;
                endTimeData = endTimeData.plusHours(durationHours).plusMinutes(durationMinutes)
                        .plusSeconds(durationSeconds);
                exerciseDataArray[2] = endTimeData.toString();
                textViewDuration.setText(counterUtilityHours.returnCounter() + "h " +
                        counterUtilityMinutes.returnCounter() + "min " +
                        counterUtilitySeconds.returnCounter() + "sec");
            }
        });
    }

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
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();

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
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonPlus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.addToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();

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
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(10);
                distance = counterUtilityDistance.returnCounterInt();

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
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(100);
                distance = counterUtilityDistance.returnCounterInt();

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
                textViewDistanceValue.setText(distanceString);
            }
        });

        imageButtonMinus1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityDistance.minusToCounter(1000);
                distance = counterUtilityDistance.returnCounterInt();

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
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.addToCounter(10);
                calories = counterUtilityCalories.returnCounterInt();
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesPlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.addToCounter(100);
                calories = counterUtilityCalories.returnCounterInt();
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter();
                calories = counterUtilityCalories.returnCounterInt();
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter(10);
                calories = counterUtilityCalories.returnCounterInt();
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
                textViewCaloriesPopUp.setText(caloriesString);
            }
        });

        imageButtonCaloriesMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityCalories.minusToCounter(100);
                calories = counterUtilityCalories.returnCounterInt();
                if (calories < 10) {
                    caloriesString = "000" + calories + "kcal";
                } else if (calories < 100) {
                    caloriesString = "00" + calories + "kcal";
                } else if (calories < 1000) {
                    caloriesString = "0" + calories + "kcal";
                } else {
                    caloriesString = calories + "kcal";
                }
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
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulsePlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.addToCounter(10);
                pulse = counterUtilityPulse.returnCounterInt();
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulsePlus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.addToCounter(100);
                pulse = counterUtilityPulse.returnCounterInt();
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter();
                pulse = counterUtilityPulse.returnCounterInt();
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter(10);
                pulse = counterUtilityPulse.returnCounterInt();
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
                textViewPulsePopUp.setText(pulseString);
            }
        });

        imageButtonPulseMinus100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterUtilityPulse.minusToCounter(100);
                pulse = counterUtilityPulse.returnCounterInt();
                if (pulse < 10) {
                    pulseString = "00" + pulse + "bpm";
                } else if (pulse < 100) {
                    pulseString = "0" + pulse + "bpm";
                } else {
                    pulseString = pulse + "bpm";
                }
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

    public void onClickSaveExercise(View view) {
        saveExerciseData();
    }

    private void saveExerciseData() {
        exerciseType = spinnerSelectExercise.getSelectedItem().toString();
        exerciseDataArray[0] = exerciseType;

        if (editTextComment.getText().toString().length() > 0) {
            comment = editTextComment.getText().toString();
            exerciseDataArray[7] = comment;
        }

        if (exerciseDataArray != null) {
            if (startTimeData == null || endTimeData == null) {
                Toast.makeText(getApplicationContext(), "At least select start date and duration!",
                        Toast.LENGTH_LONG).show();
            } else {
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
                intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentForMainActivity);
            }
        }
    }
}