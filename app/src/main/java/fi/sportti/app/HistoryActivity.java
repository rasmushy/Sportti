package fi.sportti.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.viewmodels.MainViewModel;

/**
 *@author Jukka-Pekka Jaakkola
 * Activity for displaying user's exercise history. Contains graph where exercise durations are summed up
 * for days/months. Also has all exercises listed so user can open Activity to view all exercise's information.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryActivity extends AppCompatActivity {
    /** Intent extra ID for index of selected exercise which is passed to ExerciseDetailsActivity */
    public static String SELECTED_EXERCISE_INDEX = "fi.sportti.app.index_of_selected_exercise_on_history_activity";

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
        exerciseListView = findViewById(R.id.history_listview_exercises);
        changeTimePeriodSwitch = findViewById(R.id.history_switch_toggle_graph_timeperiod);
        graph = findViewById(R.id.history_customgraph_exercise_hours);
        graph.setGraphType(CustomGraph.BAR_GRAPH);
        graph.setGraphTimePeriod(CustomGraph.DAYS_OF_WEEK);
        mainViewModel = MainActivity.getMainViewModel();
        /*
        Add Observer to exercise list which is wrapped with LiveData.
        This makes sure that listview and graph are updated only when mainViewModel has
        loaded all exercises from database and listView and graph are updated correctly if user deletes exercises.
        onChanged method is also called once everytime Activity opens so this initializes listView and graph with values. */
        mainViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                addExercisesToListView();
                updateGraph();
            }
        });
        setClickListenerOnListView();
        setSwipeListenerOnGraph();
    }

    /**
     * Method to change what data is shown on graph.
     * @param view View object which is required because this method is attached to button in layout.
     */
    public void changeTimePeriod(View view) {
        if (changeTimePeriodSwitch.isChecked()) {
            showMonthlyGraph();
        }
        else {
            showDailyGraph();
        }
    }

    private void showPrevious() {
        graph.showPreviousPeriod();
        graph.postInvalidate();
    }

    private void showNext() {
        graph.showNextPeriod();
        graph.postInvalidate();
    }

    private void showDailyGraph() {
        graph.setGraphTimePeriod(CustomGraph.DAYS_OF_WEEK);
        graph.setDataMap(dailyDataMap);
        graph.postInvalidate();
    }

    private void showMonthlyGraph() {
        graph.setGraphTimePeriod(CustomGraph.MONTHS_OF_YEAR);
        graph.setDataMap(monthlyDataMap);
        graph.postInvalidate();
    }

    private void updateGraph() {
        //Load new data from mainViewModel and set correct data for graph. Then redraw graph.
        dailyDataMap = mainViewModel.getExerciseTimesForGraph(MainViewModel.DAILY_MINUTES);
        monthlyDataMap = mainViewModel.getExerciseTimesForGraph(MainViewModel.MONTHLY_MINUTES);
        int timePeriod = graph.getGraphTimePeriod();
        if (timePeriod == CustomGraph.DAYS_OF_WEEK) {
            graph.setDataMap(dailyDataMap);
        }
        else if (timePeriod == CustomGraph.MONTHS_OF_YEAR) {
            graph.setDataMap(monthlyDataMap);
        }
        graph.postInvalidate();

    }

    private void addExercisesToListView(){
        //Get all exercises in sorted order and pass them to adapter which is set to listView.
        ArrayList<Exercise> list = (ArrayList<Exercise>) mainViewModel.getSortedExerciseList();
        ExerciseAdapter adapter = new ExerciseAdapter(this, R.layout.exercise_on_history_listview, list);
        exerciseListView.setAdapter(adapter);

    }

    private void setSwipeListenerOnGraph(){
        //Basic idea on how to implement swipe listener https://www.youtube.com/watch?v=vNJyU-XW8_Y

        //Swipe listener to change weeks/years on graph.
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
                if (xDelta > 100) { //Check if user swiped enough.
                    if (x1 > x2) { //User swiped left
                        showNext();
                    }
                    else if (x1 < x2) { //User swiped right
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
    }

    private void setClickListenerOnListView(){
        //ClickListener to open exercise details activity.
        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HistoryActivity.this, ExerciseDetailsActivity.class);
                intent.putExtra(SELECTED_EXERCISE_INDEX, i);
                startActivity(intent);
            }
        });
    }
}