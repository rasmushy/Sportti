package fi.sportti.app.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;


/**
 *@author Jukka-Pekka Jaakkola
 * Custom Adapter to display exercises on history page.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private int resourceLayout;
    private Context context;

    public ExerciseAdapter(Context context, int resource, ArrayList<Exercise> list){
        super(context, resource, list);
        resourceLayout = resource;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);

        // Create View from XML file if its null.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resourceLayout, parent, false);
        }

        //Get all required Views, set correct values to them from Exercise and return View.
        TextView sportNameTV = convertView.findViewById(R.id.exercise_sport_name);
        TextView startDateTV = convertView.findViewById(R.id.exercise_start_date);
        TextView durationTV = convertView.findViewById(R.id.exercise_duration);
        TextView caloriesTV = convertView.findViewById(R.id.exercise_calories);

        String sport = exercise.getSportType();
        String date = getDateAsText(exercise.getStartDate());
        int minutes = exercise.getDurationInMinutes();
        String duration = formatDuration(minutes);
        String calories = exercise.getCalories() + " kcal";

        sportNameTV.setText(sport);
        startDateTV.setText(date);
        durationTV.setText(duration);
        caloriesTV.setText(calories);

        return convertView;
    }


    private String getDateAsText(ZonedDateTime date){
        // Returns date as String in DD.MM.YYYY format.
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
    }


    private String formatDuration(int duration){
        // Format duration in minutes to String.
        // Less than 60 minutes is returned as (X min)
        // Equal or higher than 60 minutes is returned as (Xh Ymin)
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
