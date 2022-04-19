package fi.sportti.app.ui.adapters;


import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

import fi.sportti.app.R;

/*
 * @author Rasmus Hyyppä
 * Custom adapter for listing exercise types.
 */

public class ExerciseTypeAdapter extends ArrayAdapter<String> {

    private final List<String> exerciseList;

    public ExerciseTypeAdapter(Context context, int textViewResourceId, List<String> exerciseList) {
        super(context, textViewResourceId, exerciseList);
        this.exerciseList = exerciseList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView; // assign the view we are converting to a local variable

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.recordexercise_listview_item, null);
        }

        String exerciseType = exerciseList.get(position);

        if (exerciseType != null) {
            TextView sportNameTextView = (TextView) v.findViewById(R.id.recordexercise_listview_textview_exercise_name);
            sportNameTextView.setText(exerciseType);
        }
        return v;
    }
}
