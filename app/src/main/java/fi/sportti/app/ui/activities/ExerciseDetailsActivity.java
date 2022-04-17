package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

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
    private TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sportName = findViewById(R.id.exercisedetails_textview_sport_name);
        startDate = findViewById(R.id.exercisedetails_textview_start_date);
        endDate = findViewById(R.id.exercisedetails_textview_end_date);
        duration = findViewById(R.id.exercisedetails_textview_duration);
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
                startDate.setText(exercise.getStartDate().toString());
                endDate.setText(exercise.getEndDate().toString());
                duration.setText(String.valueOf(exercise.getDurationInMinutes()));
            }
        });

    }
}