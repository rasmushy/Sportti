package fi.sportti.app.ui.utilities;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import fi.sportti.app.R;

/**
 * Dialog utilities class for onClickEvents on popup layout in one place with complex if methods.
 * Class for reducing code repetition
 * SaveExerciseActivity uses this
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */
public class DialogUtilities {
    //Method for calorie popup
    public static void openGiveCalories(View giveCaloriesPopUp, Dialog dialog, TextView textViewForData) {
        dialog.show();

        //Init
        Button buttonSaveCalories = giveCaloriesPopUp.findViewById(R.id.buttonSaveCaloriesPopUp);
        ImageButton imageButtonCaloriesPlus = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus);
        ImageButton imageButtonCaloriesPlus10 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus10);
        ImageButton imageButtonCaloriesPlus100 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesPlus100);
        ImageButton imageButtonCaloriesMinus = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus);
        ImageButton imageButtonCaloriesMinus10 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus10);
        ImageButton imageButtonCaloriesMinus100 = giveCaloriesPopUp.findViewById(R.id.imageButtonCaloriesMinus100);
        TextView textViewCaloriesPopUp = giveCaloriesPopUp.findViewById(R.id.textViewCaloriesPopUpValue);

        //Set user view
        textViewCaloriesPopUp.setText(textViewForData.getText().toString() + "kcal");
        CounterUtility counterUtilityCalories = new CounterUtility(0, 9999, Integer.parseInt(textViewForData.getText().toString()), 1, true);

        imageButtonCaloriesPlus.setOnClickListener(view -> {
            counterUtilityCalories.addToCounter();
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        imageButtonCaloriesPlus10.setOnClickListener(view -> {
            counterUtilityCalories.addToCounter(10);
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        imageButtonCaloriesPlus100.setOnClickListener(view -> {
            counterUtilityCalories.addToCounter(100);
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        imageButtonCaloriesMinus.setOnClickListener(view -> {
            counterUtilityCalories.minusToCounter();
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        imageButtonCaloriesMinus10.setOnClickListener(view -> {
            counterUtilityCalories.minusToCounter(10);
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        imageButtonCaloriesMinus100.setOnClickListener(view -> {
            counterUtilityCalories.minusToCounter(100);
            setTextToCaloriePopup(counterUtilityCalories, textViewCaloriesPopUp);
        });

        buttonSaveCalories.setOnClickListener(view -> {
            textViewCaloriesPopUp.setText(counterUtilityCalories.returnCounterInt() + " kcal");
            textViewForData.setText(Integer.toString(counterUtilityCalories.returnCounterInt()));
            dialog.dismiss();
        });
    }

    //Method for pulse popup
    public static void openGiveAveragePulse(View giveAveragePulsePopUp, Dialog dialog, TextView textViewForData) {
        dialog.show();

        //Init
        Button buttonSavePulse = giveAveragePulsePopUp.findViewById(R.id.buttonSavePulsePopUp);
        ImageButton imageButtonPulsePlus = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus);
        ImageButton imageButtonPulsePlus10 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus10);
        ImageButton imageButtonPulsePlus100 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulsePlus100);
        ImageButton imageButtonPulseMinus = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus);
        ImageButton imageButtonPulseMinus10 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus10);
        ImageButton imageButtonPulseMinus100 = giveAveragePulsePopUp.findViewById(R.id.imageButtonPulseMinus100);
        TextView textViewPulsePopUp = giveAveragePulsePopUp.findViewById(R.id.textViewPulseValuePopUp);

        //Set user view
        textViewPulsePopUp.setText(textViewForData.getText().toString() + "bpm");
        CounterUtility counterUtilityPulse = new CounterUtility(0, 240, Integer.parseInt(textViewForData.getText().toString()), 1);

        imageButtonPulsePlus.setOnClickListener(view -> {
            counterUtilityPulse.addToCounter();
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        imageButtonPulsePlus10.setOnClickListener(view -> {
            counterUtilityPulse.addToCounter(10);
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        imageButtonPulsePlus100.setOnClickListener(view -> {
            counterUtilityPulse.addToCounter(100);
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        imageButtonPulseMinus.setOnClickListener(view -> {
            counterUtilityPulse.minusToCounter();
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        imageButtonPulseMinus10.setOnClickListener(view -> {
            counterUtilityPulse.minusToCounter(10);
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        imageButtonPulseMinus100.setOnClickListener(view -> {
            counterUtilityPulse.minusToCounter(100);
            setTextToPulsePopup(counterUtilityPulse, textViewPulsePopUp);
        });

        buttonSavePulse.setOnClickListener(view -> {
            textViewPulsePopUp.setText(counterUtilityPulse.returnCounterInt() + "bpm");
            textViewForData.setText(Integer.toString(counterUtilityPulse.returnCounterInt()));
            dialog.dismiss();
        });
    }

    /**
     * Method for Counter, reduces amount of code.
     *
     * @param countUtiCalorie CounterUtility what we have created
     * @param textViewCalorie Textview what we update
     * @author Rasmus Hyyppä
     */
    private static void setTextToCaloriePopup(CounterUtility countUtiCalorie, TextView textViewCalorie) {
        if (countUtiCalorie.returnCounterInt() < 10) {
            textViewCalorie.setText("000" + countUtiCalorie.returnCounterInt() + "kcal");
        } else if (countUtiCalorie.returnCounterInt() < 100) {
            textViewCalorie.setText("00" + countUtiCalorie.returnCounterInt() + "kcal");
        } else if (countUtiCalorie.returnCounterInt() < 1000) {
            textViewCalorie.setText("0" + countUtiCalorie.returnCounterInt() + "kcal");
        } else {
            textViewCalorie.setText(countUtiCalorie.returnCounterInt() + "kcal");
        }
    }

    /**
     * Method for Counter, reduces amount of code.
     *
     * @param countUtiPulse CounterUtility what we have created
     * @param textViewPulse Textview what we update
     * @author Rasmus Hyyppä
     */
    private static void setTextToPulsePopup(CounterUtility countUtiPulse, TextView textViewPulse) {
        if (countUtiPulse.returnCounterInt() < 10) {
            textViewPulse.setText("00" + countUtiPulse.returnCounterInt() + "bpm");
        } else if (countUtiPulse.returnCounterInt() < 100) {
            textViewPulse.setText("0" + countUtiPulse.returnCounterInt() + "bpm");
        } else {
            textViewPulse.setText(countUtiPulse.returnCounterInt() + "bpm");
        }
    }
}


