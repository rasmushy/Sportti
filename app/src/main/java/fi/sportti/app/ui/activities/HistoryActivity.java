package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.time.LocalDateTime;
import java.util.HashMap;

import fi.sportti.app.R;
import fi.sportti.app.ui.CustomGraph;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HistoryActivity extends AppCompatActivity {
    public static final String TAG = "testailua";

    private CustomGraph graph;
    private HashMap<LocalDateTime, Long> mDailyDataMap;
    private HashMap<LocalDateTime, Long> mMonthlyDataMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        graph = findViewById(R.id.history_exercise_graph);
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
        graph.setGraphStyle(CustomGraph.DAYS_OF_WEEK);
        graph.setDataMap(mDailyDataMap);
        graph.postInvalidate();
    }

    public void showMonthlyGraph(View view){
        graph.setGraphStyle(CustomGraph.MONTHS_OF_YEAR);
        graph.setDataMap(mMonthlyDataMap);
        graph.postInvalidate();
    }
}