package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.ui.adapters.ExerciseTypeAdapter;

/*
 * @author Rasmus Hyyppä
 * User wants to record exercise, here we choose what type of exercise we are planning to do
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewRecordedExerciseActivity extends AppCompatActivity {

    private static final String TAG = "RecordExerciseActivity"; // TAG for Log.d
    public static final String REPLY_EXERCISE_TYPE = "fi.sportti.app.REPLY_EXERCISE_POSITION";

    private ListView exerciseListView;
    private List<String> exerciseTypeList;
    private String exerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recorded_exercise);
        Log.d(TAG, "OnCreate()");
        //Find views
        exerciseListView = findViewById(R.id.recordexercise_listview_all_exercises);
        //Exercise list from resources. Best way seems to be translating it from string arr to list.
        exerciseTypeList = Arrays.asList(getResources().getStringArray(R.array.exercise_type_list));
        //Create ArrayAdapter that fills listview
        ExerciseTypeAdapter adapter = new ExerciseTypeAdapter(this, R.layout.recordexercise_listview_item, exerciseTypeList);
        exerciseListView.setAdapter(adapter);
        exerciseListView.setOnItemClickListener((parent, view, position, id) -> {
            //When user clicks one of the list items, it will select them as a sport type.
            Log.d(TAG, "selected item position: " + exerciseListView.getItemAtPosition(position));
            exerciseType = exerciseListView.getItemAtPosition(position).toString();
            Log.d(TAG, "exerciseType: " + exerciseType);
        });
    }

    // Button method to start activity
    public void continuePressed(View caller) {
        //Create Integer variable to check nulls
        Log.d(TAG, "continuePressed(), sportType is: " + exerciseType);
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