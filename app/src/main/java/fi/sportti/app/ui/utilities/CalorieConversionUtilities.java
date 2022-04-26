package fi.sportti.app.ui.utilities;

import static fi.sportti.app.ui.utilities.TimeConversionUtilities.getAgeFromDate;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import fi.sportti.app.constants.ExerciseType;
import fi.sportti.app.datastorage.room.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalorieConversionUtilities {


    /**
     * Mifflin-St Jeor Equation: https://en.wikipedia.org/wiki/Basal_metabolic_rate
     * For men:
     * BMR = 10xWeight + 6.25xHeight - 5xAge + 5
     * For women:
     * BMR = 10xWeight + 6.25xHeight - 5xAge - 161
     *
     * @param user
     * @return
     */
    public static double getBasalMetabolicRate(@NonNull User user) {
        if (user.getGender().equals("Male")) {
            return ((10 * user.getWeight() + 6.25 * user.getHeight()) - 5 * getAgeFromDate(user.getAge()) + 5);
        } else if (user.getGender().equals("Female")) {
            return ((10 * user.getWeight() + 6.25 * user.getHeight()) - 5 * getAgeFromDate(user.getAge()) - 161);
        }
        return 0.0;
    }

    /**
     * 1 MET = 1 kcal/kg x h = 4.184 kJ/kg x h = 1.162 W/kg
     * https://en.wikipedia.org/wiki/Metabolic_equivalent_of_task
     * CALORIES: (Time(in minutes) x MET x Body Weight) / 200
     * https://www.calculator.net/calories-burned-calculator.html
     *
     * @param user      User data
     * @param sportType Sport Data
     * @param startDate StartDate to calculate duration
     * @param endDate   EndDate to calculate duration
     * @return Estimated calories burned as integer
     */
    public static int getCalories(@NonNull User user, int sportType, ZonedDateTime startDate, ZonedDateTime endDate) {
        int calories = (int) (ChronoUnit.MINUTES.between(startDate, endDate)
                * ExerciseType.values()[sportType].getMetabolicEquivalentOfTask()
                * user.getWeight())
                / 200;
        Log.d("getCalories()", "int calories: " + calories);
        return calories;
    }
}
