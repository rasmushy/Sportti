package fi.sportti.app.datastorage.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;


public class RecordController {

    private static final String PREFERENCES = "fi.sportti.app.preferences";
    private static final String START_TIME_KEY = "fi.sportti.app.startKey";
    private static final String STOP_TIME_KEY = "fi.sportti.app.stopKey";
    private static final String COUNTING_KEY = "fi.sportti.app.countingKey";
    private static final String TAG = "RecordController";

    private SharedPreferences sharedPreferences;
    private final DateTimeFormatter dateTimeFormatter; //Date format to save time into string key's
    private boolean timerCounting;
    private int timerStartCount;
    private ZonedDateTime startTime;
    private ZonedDateTime stopTime;

    public RecordController(Context context) {
        startTime = ZonedDateTime.now();
        stopTime = ZonedDateTime.now();
        timerStartCount = 0;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        //Are we counting?
        timerCounting = sharedPreferences.getBoolean(COUNTING_KEY, false);

        //Get date from string constants
        String startString = sharedPreferences.getString(START_TIME_KEY, null);
        if (startString != null) {
            try {
                startTime = ZonedDateTime.parse(startString); // try&catch probably not needed
            } catch (DateTimeParseException e) {
                Log.d(TAG, "DateTimeParseException on startTime: " + e);
                e.printStackTrace();
            }
        }

        String stopString = sharedPreferences.getString(STOP_TIME_KEY, null);
        if (stopString != null) {
            try {
                stopTime = ZonedDateTime.parse(stopString);
            } catch (DateTimeParseException e) {
                Log.d(TAG, "DateTimeParseException on stopTime: " + e);
                e.printStackTrace();
            }
        }

    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(ZonedDateTime date) {
        startTime = date;
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        String stringDate;
        if (date == null) {
            stringDate = null;
        } else {
            stringDate = dateTimeFormatter.format(date);
        }
        prefEditor.putString(START_TIME_KEY, stringDate);
        prefEditor.apply();
    }

    public ZonedDateTime getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(ZonedDateTime date) {
        stopTime = date;
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        String stringDate;
        if (date == null) {
            stringDate = null;
        } else {
            stringDate = dateTimeFormatter.format(date);
        }
        prefEditor.putString(STOP_TIME_KEY, stringDate);
        prefEditor.apply();
    }


    public boolean getTimerCounting() {
        Log.d(TAG, ", is timer running: " + timerCounting);
        return this.timerCounting;
    }

    public int getTimerStartCount() {
        return timerStartCount;
    }

    public void setTimerCounting(Boolean value) {
        if (value) {
            timerStartCount++;
        }
        timerCounting = value;
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean(COUNTING_KEY, value);
        prefEditor.apply();
    }
}
