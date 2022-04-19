package fi.sportti.app.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;

@RequiresApi(api = Build.VERSION_CODES.O)


//Custom Adapter to display exercises on history page.
public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private int resourceLayout;

    public ExerciseAdapter(Context context, int resource, ArrayList<Exercise> list){
        super(context, resource, list);
        resourceLayout = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceLayout, parent, false);
        }
        TextView sportName = (TextView) convertView.findViewById(R.id.exercise_sport_name);
        TextView startDate = (TextView) convertView.findViewById(R.id.exercise_start_date);
        TextView duration = (TextView) convertView.findViewById(R.id.exercise_duration);
        TextView calories = (TextView) convertView.findViewById(R.id.exercise_calories);
        sportName.setText(exercise.getSportType());
        startDate.setText(getDateAsText(exercise.getStartDate()));
        String durationAsText = formatDuration(exercise.getDurationInMinutes());
        duration.setText(durationAsText);
        calories.setText(exercise.getCalories() + " kcal");
        return convertView;
    }


    private String getDateAsText(ZonedDateTime date){
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
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
