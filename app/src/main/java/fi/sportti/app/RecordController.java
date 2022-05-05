package fi.sportti.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Controller for keeping up timer for recorded exercise
 * It saves timer data to shared preferences
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

public class RecordController {
    private static final String PREFERENCES = "fi.sportti.app.preferences";
    private static final String START_TIME_KEY = "fi.sportti.app.startKey";
    private static final String STOP_TIME_KEY = "fi.sportti.app.stopKey";
    private static final String COUNTING_KEY = "fi.sportti.app.countingKey";

    private SharedPreferences sharedPreferences;
    private final DateTimeFormatter dateTimeFormatter; //Date format to save time into string key's
    private boolean timerCounting;
    private int timerStartCount;

    private ZonedDateTime startTime;
    private ZonedDateTime stopTime;

    /**
     * RecordController saves timer recorded data into shared preferences.
     *
     * @param context Context from StartExerciseActivity
     */
    public RecordController(Context context) {

        //Initialize
        timerStartCount = 0;

        this.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        //Are we counting?
        timerCounting = sharedPreferences.getBoolean(COUNTING_KEY, false);

        //Get date from string constants (SharedPreferences), so time will be updated
        String startString = sharedPreferences.getString(START_TIME_KEY, null);
        if (startString != null) {
            startTime = ZonedDateTime.parse(startString);
        }

        String stopString = sharedPreferences.getString(STOP_TIME_KEY, null);
        if (stopString != null) {
            stopTime = ZonedDateTime.parse(stopString);
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
        return this.timerCounting;
    }

    public int getTimerStartCount() {
        return timerStartCount;
    }

    public void setTimerCounting(Boolean value) {
        if (value) {
            timerStartCount++; //Counter that makes sure we have activated timer at least once.
        }
        timerCounting = value;
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putBoolean(COUNTING_KEY, value);
        prefEditor.apply();
    }

    public void zeroTimerStartCount() {
        this.timerStartCount = 0;
    }
}
