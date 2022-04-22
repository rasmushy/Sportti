package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.CustomGraph;
import fi.sportti.app.ui.adapters.ExerciseAdapter;
import fi.sportti.app.ui.viewmodels.MainViewModel;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryActivity extends AppCompatActivity {
    public static final String TAG = "testailua";
    public static String SELECTED_EXERCISE = "selected_exercise_on_history_activity";
    private MainViewModel mainViewModel;
    private CustomGraph graph;
    private ListView exerciseListView;
    private Switch changeTimePeriodSwitch;
    private HashMap<ZonedDateTime, Integer> dailyDataMap;
    private HashMap<ZonedDateTime, Integer> monthlyDataMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        exerciseListView = findViewById(R.id.history_listview_exercises);
        changeTimePeriodSwitch = findViewById(R.id.history_switch_toggle_graph_timeperiod);
        graph = findViewById(R.id.history_customgraph_exercise_hours);
        graph.setGraphType(CustomGraph.BAR_GRAPH);
        graph.setGraphTimePeriod(CustomGraph.DAYS_OF_WEEK);

        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                Log.d(TAG, "onChanged: Exercise list changed");
                updateGraph();
                //Sort exercises based on date so they are in correct order to display on listview.
                exercises.sort(new Comparator<Exercise>() {
                    @Override
                    public int compare(Exercise exercise, Exercise t1) {
                        return t1.getStartDate().compareTo(exercise.getStartDate());
                    }
                });
                exerciseListView.setAdapter(new ExerciseAdapter(
                        getApplicationContext(),
                        R.layout.exercise_on_history_listview,
                        (ArrayList) exercises));
            }
        });

        //Swipe listener to change weeks/years on graph.
        //Basic idea on how to implement swipe listener https://www.youtube.com/watch?v=vNJyU-XW8_Y
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //Get two x coordinates from MotionEvents and based on their difference determine if user swiped View and in which direction.
                float x1 = e1.getX();
                float x2 = e2.getX();
                float xDelta = Math.abs(x2 - x1);
                if (xDelta > 100) {
                    if (x1 > x2) {
                        Log.d(TAG, "onFling: swiped left");
                        showNext();
                    } else if (x1 < x2) {
                        Log.d(TAG, "onFling: swiped right");
                        showPrevious();
                    }
                }
                return true;
            }
        });

        //Pass all events to GestureDetector which will handle it.
        graph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        //ClickListener to open exercise details activity.
        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HistoryActivity.this, ExerciseDetailsActivity.class);
                intent.putExtra(SELECTED_EXERCISE, i);
                startActivity(intent);
            }
        });

        // createTestExercises();
        //mainViewModel.deleteAllExercises();

    }

    public void changeTimePeriod(View view) {
        if (changeTimePeriodSwitch.isChecked()) {
            showMonthlyGraph();
        } else {
            showDailyGraph();
        }
    }

    public void showPrevious() {
        graph.showPreviousPeriod();
        graph.postInvalidate();
    }

    public void showNext() {
        graph.showNextPeriod();
        graph.postInvalidate();
    }

    public void showDailyGraph() {
        graph.setGraphTimePeriod(CustomGraph.DAYS_OF_WEEK);
        graph.setDataMap(dailyDataMap);
        graph.postInvalidate();
    }

    public void showMonthlyGraph() {
        graph.setGraphTimePeriod(CustomGraph.MONTHS_OF_YEAR);
        graph.setDataMap(monthlyDataMap);
        graph.postInvalidate();
    }

    private void updateGraph() {
        monthlyDataMap = mainViewModel.getExerciseTimesForGraph(MainViewModel.MONTHLY_HOURS);
        dailyDataMap = mainViewModel.getExerciseTimesForGraph(MainViewModel.DAILY_HOURS);
        if (graph.getGraphTimePeriod() == CustomGraph.DAYS_OF_WEEK) {
            graph.setDataMap(dailyDataMap);
        } else if (graph.getGraphTimePeriod() == CustomGraph.MONTHS_OF_YEAR) {
            graph.setDataMap(monthlyDataMap);
        }
        graph.postInvalidate();
    }

    private void createTestExercises() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String running = "juoksu";
                Exercise exercise1;
                Exercise exercise2;
                ZonedDateTime today = ZonedDateTime.now();
                ZonedDateTime start;
                ZonedDateTime end;
                int hours;
                Random rand = new Random();
                //Create data for 300 previous and 300 future days.
                List<Exercise> list = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                    hours = rand.nextInt(4) + 1;
                    start = today.plusDays(i);
                    end = start.plusHours(hours);
                    exercise1 = new Exercise(running, 1, start, end, 200, 0, "", 0.0, "");
                    hours = rand.nextInt(4) + 1;
                    start = today.minusDays(i);
                    end = start.plusHours(hours);
                    exercise2 = new Exercise(running, 1, start, end, 200, 0, "", 0.0, "");
                    list.add(exercise1);
                    list.add(exercise2);
                }
                mainViewModel.insertExercisesFromList(list);
                Log.d(TAG, "createTestExercises: New exercises added");
            }
        });
        thread.start();
    }
}