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
import fi.sportti.app.datastorage.room.User;

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
        sportName.setText(exercise.getSportType());
        startDate.setText(getDateAsText(exercise));
        duration.setText(String.valueOf(exercise.getDuration()));
        return convertView;
    }


    private String getDateAsText(Exercise exercise){
        ZonedDateTime date = exercise.getStartDate();
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
    }

}
