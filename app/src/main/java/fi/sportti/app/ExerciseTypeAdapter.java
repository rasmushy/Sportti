package fi.sportti.app;


import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;


/**
 * Custom adapter for listing exercise types.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

public class ExerciseTypeAdapter extends ArrayAdapter<String> {

    private final int textViewResourceId;

    public ExerciseTypeAdapter(Context context, int textViewResourceId, List<String> exerciseList) {
        super(context, textViewResourceId, exerciseList);
        this.textViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView; // assign the view we are converting to a local variable

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(textViewResourceId, null);
        }

        String exerciseType = getItem(position);

        if (exerciseType != null) {
            TextView sportNameTextView = (TextView) v.findViewById(R.id.recordexercise_listview_textview_exercise_name);
            sportNameTextView.setText(exerciseType);
        }
        return v;
    }
}
