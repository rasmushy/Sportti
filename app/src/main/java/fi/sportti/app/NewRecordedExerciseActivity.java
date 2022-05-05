package fi.sportti.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import fi.sportti.app.ui.constants.ExerciseType;

/**
 * Activity where user selects what exercise will be recorded.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewRecordedExerciseActivity extends AppCompatActivity {

    public static final String REPLY_EXERCISE_TYPE = "fi.sportti.app.REPLY_EXERCISE_POSITION";

    private ListView exerciseListView;
    private List<String> exerciseTypeList;
    private String exerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recorded_exercise);

        //Find views
        exerciseListView = findViewById(R.id.recordexercise_listview_all_exercises);

        //Exercise list from resources. Best way seems to be translating it from string arr to list.
        exerciseTypeList = Arrays.asList(getResources().getStringArray(R.array.exercise_type_list));

        //Create ArrayAdapter that fills listview
        ExerciseTypeAdapter adapter = new ExerciseTypeAdapter(this, R.layout.recordexercise_listview_layout, exerciseTypeList);

        //Set our adapter to listview
        exerciseListView.setAdapter(adapter);

        //When user clicks one of the list items, it will select them as a sport type.
        exerciseListView.setOnItemClickListener((parent, view, position, id) -> {
            exerciseType = ExerciseType.values()[position].getExerciseName().toUpperCase();
        });
    }

    // Button method to start activity
    public void continuePressed(View caller) {
        //Create Integer variable to check nulls
        if (exerciseType != null) {
            Intent startExerciseActivity = new Intent(NewRecordedExerciseActivity.this, StartExerciseActivity.class);
            startExerciseActivity.putExtra(REPLY_EXERCISE_TYPE, exerciseType);
            startActivity(startExerciseActivity);
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "Select exercise to continue", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}