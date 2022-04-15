package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.ui.CustomGraph;
import fi.sportti.app.ui.viewmodels.MainViewModel;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryActivity extends AppCompatActivity {
    public static final String TAG = "testailua";

    private MainViewModel mainViewModel;
    private CustomGraph graph;
    private HashMap<LocalDateTime, Long> mDailyDataMap;
    private HashMap<LocalDateTime, Long> mMonthlyDataMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        graph = findViewById(R.id.history_customgraph_exercise_hours);
        //createTestExercises();
        /*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDailyDataMap = exerciseViewModel.getHoursForGraph(ExerciseViewModel.DAILY_HOURS);
                mMonthlyDataMap = exerciseViewModel.getHoursForGraph(ExerciseViewModel.MONTHLY_HOURS);
                mCustomGraph.setGraphType(Graph.BAR_GRAPH);
                mCustomGraph.setGraphStyle(Graph.DAYS_OF_WEEK);
                mCustomGraph.setDataMap(mDailyDataMap);
                mCustomGraph.postInvalidate();
            }
        });
        thread.start();
        */
    }

    public void showPrevious(View view){
        graph.showPreviousPeriod();
        graph.postInvalidate();
    }

    public void showNext(View view){
        graph.showNextPeriod();
        graph.postInvalidate();
    }

    public void showDailyGraph(View view){
        graph.setGraphTimePeriod(CustomGraph.DAYS_OF_WEEK);
        graph.setDataMap(mDailyDataMap);
        graph.postInvalidate();
    }

    public void showMonthlyGraph(View view){
        graph.setGraphTimePeriod(CustomGraph.MONTHS_OF_YEAR);
        graph.setDataMap(mMonthlyDataMap);
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
                for (int i = 1; i <= 300; i++) {
                    hours = rand.nextInt(5);
                    start = today.plusDays(i);
                    end = start.plusHours(hours);
                    exercise1 = new Exercise(running, 1, start, end, 200);
                    hours = rand.nextInt(5);
                    start = today.minusDays(i);
                    end = start.plusHours(hours);
                    exercise2 = new Exercise(running, 1, start, end, 200);
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