package fi.sportti.app.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.Exercise;
import fi.sportti.app.datastorage.room.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    public ExerciseAdapter(Context context, ArrayList<Exercise> list){
        super(context, 0, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Exercise exercise = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_on_history_listview, parent, false);
        }
        // Lookup view for data population
        TextView sportName = (TextView) convertView.findViewById(R.id.exercise_sport_name);
        TextView startDate = (TextView) convertView.findViewById(R.id.exercise_start_date);
        TextView duration = (TextView) convertView.findViewById(R.id.exercise_duration);
        // Populate the data into the template view using the data object
        sportName.setText(exercise.getSportType());
        startDate.setText(exercise.getStartDate().toString());
        duration.setText(String.valueOf(exercise.getDuration()));
        // Return the completed view to render on screen
        return convertView;
    }

}
