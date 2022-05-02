package fi.sportti.app.ui.utilities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import fi.sportti.app.R;

/**
 * Dialog utilities class for onClickEvents on popup layout in one place with complex if methods.
 * Class for reducing code repetition, code rewritten from NewManualExerciseActivity to make it convergent
 * SaveExerciseActivity uses this helper class.
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */
public class ExerciseDialogOnClickSetter implements View.OnClickListener {

    private CounterUtility counterUtility;
    private View givePopUp;
    private Dialog dialog;
    private TextView textViewPopUp;
    private TextView textViewForData;

    /**
     * @param givePopUp       This is view that is layout inflated, meaning it will be used to find views in layout
     * @param counterUtility  Utility to use image buttons inside of layout's
     * @param textViewPopUp   TextView in the popup,
     * @param dialog
     * @param textViewForData Textview outside of layout, transferring layout data to SaveExerciseActivity
     */
    public ExerciseDialogOnClickSetter(View givePopUp, CounterUtility counterUtility, TextView textViewPopUp, Dialog dialog, TextView textViewForData) {
        this.counterUtility = counterUtility;
        this.givePopUp = givePopUp;
        this.dialog = dialog;
        this.textViewForData = textViewForData;
        this.textViewPopUp = textViewPopUp;
    }

    //Method for calorie popup
    public void openGiveCalories() {
        dialog.show();
        //Init
        Button buttonSaveCalories = givePopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
        ImageButton imageButtonCaloriesPlus = givePopUp.findViewById(R.id.imageButtonCaloriesPlus);
        ImageButton imageButtonCaloriesPlus10 = givePopUp.findViewById(R.id.imageButtonCaloriesPlus10);
        ImageButton imageButtonCaloriesPlus100 = givePopUp.findViewById(R.id.imageButtonCaloriesPlus100);
        ImageButton imageButtonCaloriesMinus = givePopUp.findViewById(R.id.imageButtonCaloriesMinus);
        ImageButton imageButtonCaloriesMinus10 = givePopUp.findViewById(R.id.imageButtonCaloriesMinus10);
        ImageButton imageButtonCaloriesMinus100 = givePopUp.findViewById(R.id.imageButtonCaloriesMinus100);

        //Set user view
        textViewPopUp.setText(textViewForData.getText().toString() + "kcal");

        imageButtonCaloriesPlus.setOnClickListener(this::onClick);
        imageButtonCaloriesPlus10.setOnClickListener(this::onClick);
        imageButtonCaloriesPlus100.setOnClickListener(this::onClick);
        imageButtonCaloriesMinus.setOnClickListener(this::onClick);
        imageButtonCaloriesMinus10.setOnClickListener(this::onClick);
        imageButtonCaloriesMinus100.setOnClickListener(this::onClick);
        buttonSaveCalories.setOnClickListener(this::onClick);
    }

    //Method for pulse popup
    public void openGiveAveragePulse() {
        dialog.show();

        //Init
        Button buttonSavePulse = givePopUp.findViewById(R.id.buttonSavePulsePopUp);
        ImageButton imageButtonPulsePlus = givePopUp.findViewById(R.id.imageButtonPulsePlus);
        ImageButton imageButtonPulsePlus10 = givePopUp.findViewById(R.id.imageButtonPulsePlus10);
        ImageButton imageButtonPulsePlus100 = givePopUp.findViewById(R.id.imageButtonPulsePlus100);
        ImageButton imageButtonPulseMinus = givePopUp.findViewById(R.id.imageButtonPulseMinus);
        ImageButton imageButtonPulseMinus10 = givePopUp.findViewById(R.id.imageButtonPulseMinus10);
        ImageButton imageButtonPulseMinus100 = givePopUp.findViewById(R.id.imageButtonPulseMinus100);

        //Set user view
        textViewPopUp.setText(textViewForData.getText().toString() + "bpm");

        imageButtonPulsePlus.setOnClickListener(this::onClick);
        imageButtonPulsePlus10.setOnClickListener(this::onClick);
        imageButtonPulsePlus100.setOnClickListener(this::onClick);
        imageButtonPulseMinus.setOnClickListener(this::onClick);
        imageButtonPulseMinus10.setOnClickListener(this::onClick);
        imageButtonPulseMinus100.setOnClickListener(this::onClick);
        buttonSavePulse.setOnClickListener(this::onClick);
    }

    /**
     * Method for setting up textviews in popup layout, reduces amount of code in SaveExerciseActivity.
     *
     * @param countUtiCalorie CounterUtility what we have created
     * @param textViewPopUp   Textview what we update
     * @author Rasmus Hyyppä
     */
    private void setTextToPopup(CounterUtility countUtiCalorie, TextView textViewPopUp) {
        String startString = "";
        String endString = "";

        if (textViewPopUp.getId() == R.id.textViewCaloriesPopUpValue) {
            endString = "kcal";
            startString = "000";
        }

        if (textViewPopUp.getId() == R.id.textViewPulseValuePopUp) {
            endString = "bpm";
            startString = "00";
        }

        if (counterUtility.returnCounterInt() < 10) {
            textViewPopUp.setText(startString + countUtiCalorie.returnCounterInt() + endString);
        } else if (counterUtility.returnCounterInt() < 100) {
            textViewPopUp.setText(startString.substring(0, startString.length() - 1) + countUtiCalorie.returnCounterInt() + endString);
        } else if (counterUtility.returnCounterInt() < 1000 && endString.equals("kcal")) {
            textViewPopUp.setText(startString.substring(0, startString.length() - 2) + countUtiCalorie.returnCounterInt() + endString);
        } else {
            textViewPopUp.setText(counterUtility.returnCounterInt() + endString);
        }
    }

    /**
     * All of the onClickListeners listed under one method
     * This is done by implementing View.OnClickListener
     *
     * @param view View that is clicked
     * @author Rasmus Hyyppä
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButtonCaloriesPlus:
            case R.id.imageButtonPulsePlus:
                counterUtility.addToCounter();
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.imageButtonCaloriesPlus10:
            case R.id.imageButtonPulsePlus10:
                counterUtility.addToCounter(10);
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.imageButtonCaloriesPlus100:
            case R.id.imageButtonPulsePlus100:
                counterUtility.addToCounter(100);
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.imageButtonCaloriesMinus:
            case R.id.imageButtonPulseMinus:
                counterUtility.minusToCounter();
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.imageButtonCaloriesMinus10:
            case R.id.imageButtonPulseMinus10:
                counterUtility.minusToCounter(10);
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.imageButtonCaloriesMinus100:
            case R.id.imageButtonPulseMinus100:
                counterUtility.minusToCounter(100);
                setTextToPopup(counterUtility, textViewPopUp);
                break;
            case R.id.buttonSaveCaloriesPopUp:
            case R.id.buttonSavePulsePopUp:
                textViewForData.setText(Integer.toString(counterUtility.returnCounterInt()));
                dialog.dismiss();
                break;
            default:
                break;
        }
    }
}
