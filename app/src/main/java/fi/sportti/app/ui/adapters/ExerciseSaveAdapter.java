package fi.sportti.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fi.sportti.app.R;

/**
 * Custom adapter for listview to display recorded data after recorded exercise.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

public class ExerciseSaveAdapter extends ArrayAdapter<String> {

    private final int textViewResourceId;
    String[] saveTopicArray;

    public ExerciseSaveAdapter(Context context, int textViewResourceId, List<String> exerciseDataList) {
        super(context, textViewResourceId, exerciseDataList);
        this.textViewResourceId = textViewResourceId;
        this.saveTopicArray = context.getResources().getStringArray(R.array.save_topic_list);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //Assign the view we are converting to a local variable
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(textViewResourceId, null);
        }

        //Set title for data for data we will display from resources
        TextView titleForData = (TextView) v.findViewById(R.id.saveexercise_listview_textview_topic);
        String topic = saveTopicArray[position];
        titleForData.setText(topic);

        //Data display from array
        TextView recordedDataTextView = (TextView) v.findViewById(R.id.saveexercise_listview_textview_data);
        String exerciseData = getItem(position);
        if (exerciseData != null) {
            recordedDataTextView.setText(exerciseData);
        } else {
            recordedDataTextView.setText(" "); //Set it to empty string if no data is found from list
        }

        //Set more details after our data to make it more appealing
        TextView detailsForDataType = (TextView) v.findViewById(R.id.saveexercise_listview_textview_system_type);
        String unitType;
        switch (position) {
            case 1:
                unitType = ZonedDateTime.now().getOffset().toString();
                break;
            case 3:
                unitType = "kcal";
                break;
            case 4:
                unitType = "avg bpm";
                break;
            case 5:
                unitType = "km";
                break;
            default:
                unitType = " ";
        }
        detailsForDataType.setText(unitType);

        return v;
    }
}
