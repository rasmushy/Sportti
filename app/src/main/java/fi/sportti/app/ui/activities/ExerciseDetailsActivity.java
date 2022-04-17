package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.viewmodels.MainViewModel;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExerciseDetailsActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView sportName;
    private TextView startDate;
    private TextView endDate;
    private TextView durationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sportName = findViewById(R.id.exercisedetails_textview_sport_name);
        startDate = findViewById(R.id.exercisedetails_textview_start_date_value);
        endDate = findViewById(R.id.exercisedetails_textview_end_date_value);
        durationTextView = findViewById(R.id.exercisedetails_textview_duration_value);
        Bundle b = getIntent().getExtras();
        int index = b.getInt(HistoryActivity.SELECTED_EXERCISE);

        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                exercises.sort(new Comparator<Exercise>() {
                    @Override
                    public int compare(Exercise exercise, Exercise t1) {
                        return t1.getStartDate().compareTo(exercise.getStartDate());
                    }
                });
                Exercise exercise = exercises.get(index);
                sportName.setText(exercise.getSportType());
                String start = getDateAndTimeAsText(exercise.getStartDate());
                String end = getDateAndTimeAsText(exercise.getEndDate());
                startDate.setText(start);
                endDate.setText(end);
                int duration = exercise.getDurationInMinutes();
                String durationAsText = formatDuration(duration);
                durationTextView.setText(durationAsText);
            }
        });

    }

    private String getDateAndTimeAsText(ZonedDateTime date){
        StringBuilder sb = new StringBuilder();
        sb.append(date.getDayOfMonth() + ".");
        sb.append(date.getMonthValue() + ".");
        sb.append(date.getYear() + " ");
        sb.append(date.getHour() + ":");
        int minute = date.getMinute();
        if(minute >= 10){
            sb.append(minute);
        }
        else {
            sb.append("0" + minute);
        }

        return sb.toString();
    }

    private String formatDuration(int duration){
        String result = "";
        if(duration == 60){
            result = "1h";
        }
        else if(duration >= 60){
            int hours = duration / 60;
            int minutes = duration - (hours*60);
            if(hours == 1){
                result = "1h";
            }
            else {
                result += hours + "h ";
            }
            if(minutes > 0){
                result += minutes + "min";
            }
        }
        else {
            result = duration + " min";
        }
        return result;
    }
}