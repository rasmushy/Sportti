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
     * <p>
     * Currently not in use, this gives us daily energy consumption estimate
     *
     * @param user
     * @return BMR as double
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
     * @return Estimated calories burned as integer value
     * @author Rasmus Hyyppä
     */
    public static int getCalories(@NonNull User user, int sportType, ZonedDateTime startDate, ZonedDateTime endDate) {
        int calories = (int) (ChronoUnit.MINUTES.between(startDate, endDate)
                * ExerciseType.values()[sportType].getMetabolicEquivalentOfTask()
                * user.getWeight())
                / 200;
        Log.d("getCalories()", "int calories: " + calories);
        return calories;
    }


    /**
     * Calorie calculation with Heart rate
     * VO2MAX is more accurate, uses generic form if user has not set resting heart rate.
     *
     * @param user         Current user (for gender, weight and age)
     * @param avgHeartRate Average heart rate in exercise
     * @param startDate    Start date to calculate time duration
     * @param endDate      End date to calculate time duration
     * @return Estimated calories burned as integer value
     * @author Rasmus Hyyppä
     */
    public static int getCaloriesWithHeartRate(@NonNull User user, int avgHeartRate, ZonedDateTime startDate, ZonedDateTime endDate) {
        // VO2MAX calculations: https://www.mdapp.co/vo2-max-calculator-for-aerobic-capacity-369/
        double calories = 0;
        double maxHeartRate = 208 - (0.7 * getAgeFromDate(user.getAge()));
        double vo2MAX = 15.3 * (maxHeartRate / user.getRestHeartRate());
        Log.d("getCaloriesWithVOMax() ", "vo2MAX is: " + vo2MAX);
        Log.d("getCaloriesWithVOMax() ", "maxHR is: " + maxHeartRate);
        if (user.getRestHeartRate() > 20) {
            Log.d("getCaloriesWithVOMAX() ", "userRestHeartRate less than 20, calculating without vo2MAX.");
            if (user.getGender().equals("Male")) {
                calories = ((-95.7735 + (0.634 * avgHeartRate)
                        + (0.404 * vo2MAX)
                        + (0.394 * user.getWeight())
                        + (0.271 * getAgeFromDate(user.getAge()))) / 4.184)
                        * 60 * ChronoUnit.HOURS.between(startDate, endDate);
            } else if (user.getGender().equals("Female")) {
                calories = ((-59.3954 + (0.45 * avgHeartRate)
                        + (0.380 * vo2MAX)
                        + (0.103 * user.getWeight())
                        + (0.274 * getAgeFromDate(user.getAge()))) / 4.184)
                        * 60 * ChronoUnit.HOURS.between(startDate, endDate);
            }
        } else {
            if (user.getGender().equals("Male")) {
                calories = ((-55.0969 + (0.6309 * avgHeartRate)
                        + (0.1988 * user.getWeight())
                        + (0.2017 * getAgeFromDate(user.getAge()))) / 4.184)
                        * 60 * ChronoUnit.HOURS.between(startDate, endDate);
            } else if (user.getGender().equals("Female")) {
                calories = ((-20.4022 + (0.4472 * avgHeartRate)
                        + (0.1263 * user.getWeight())
                        + (0.074 * getAgeFromDate(user.getAge()))) / 4.184)
                        * 60 * ChronoUnit.HOURS.between(startDate, endDate);
            }
        }

        Log.d("getCaloriesWithVOMax() ", "Calories burned: " + calories);
        return (int) Math.abs(calories);
    }

}
