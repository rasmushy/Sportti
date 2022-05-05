package fi.sportti.app.ui.activities;

import static fi.sportti.app.ui.utilities.CalorieConversionUtilities.getBasalMetabolicRate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import fi.sportti.app.R;
import fi.sportti.app.datastorage.room.User;
import fi.sportti.app.ui.viewmodels.MainViewModel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.ZonedDateTime;
import java.util.Calendar;

/**
 * ProfileActivity for user's personal data.
 * Data is used to calculate estimates of calories etc.
 *
 * @author Yana Krylova
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewWeight, textViewHeight, textViewGender, textViewBirthday, textViewWeeklyGoalsHours, textViewWeeklyGoalsMinutes, textViewRestingHeartRate, textViewMaximumHeartRate, textViewBasalMetabolicRate;
    private EditText userName;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private User user;
    private MainViewModel mainViewModel;

    //static final for image requesting
    //static final int GALLERY_REQUEST = 1;

    //values for date set up
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mainViewModel = MainActivity.getMainViewModel();
        user = mainViewModel.getFirstUser();
        dialogBuilder = new AlertDialog.Builder(this);
        //getting the name
        userName = findViewById(R.id.user_name);
        userName.setText(user.getUserName());
        textViewHeight = findViewById(R.id.text_height);
        textViewWeight = findViewById(R.id.text_weight);
        textViewGender = findViewById(R.id.text_gender);
        textViewBirthday = findViewById(R.id.text_date);
        LinearLayout linearWeeklyGoalsLayout = findViewById(R.id.weekly_goals_layout);
        LinearLayout linearHeightSelectionLayout = findViewById(R.id.height_selection_layout);
        LinearLayout linearWeightSelectionLayout = findViewById(R.id.weight_selection_layout);
        LinearLayout linearAgeSelectionLayout = findViewById(R.id.age_selection_layout);
        LinearLayout linearUserNameLayout = findViewById(R.id.username_selection_layout);
        LinearLayout linearRestHeartRateLayout = findViewById(R.id.resting_heart_rate_layout);
        LinearLayout linearMaxHeartRateLayout = findViewById(R.id.maximum_heartrate_layout);
        LinearLayout linearGenderSelectionLayout = findViewById(R.id.gender_selection_layout);

        textViewRestingHeartRate = findViewById(R.id.text_resting_heart_rate);
        textViewMaximumHeartRate = findViewById(R.id.text_maximum_heart_rate);
        textViewBasalMetabolicRate = findViewById(R.id.text_estimated_metabolic_rate);
        textViewWeeklyGoalsMinutes = findViewById(R.id.text_weekly_goal_minutes);
        textViewWeeklyGoalsHours = findViewById(R.id.text_weekly_goal_hours);
        //Profile save button to update user data
        Button profileSaveButton = findViewById(R.id.profile_button_save);
        textViewHeight.setText(Integer.toString(user.getHeight()) + " centimeters");
        textViewWeight.setText(Integer.toString(user.getWeight()) + " kilograms");
        textViewGender.setText(user.getGender());
        textViewBirthday.setText(Integer.toString(user.getAge()) + " years");
        textViewWeeklyGoalsMinutes.setText(Integer.toString(user.getWeeklyGoalMinute()) + " min");
        textViewWeeklyGoalsHours.setText(Integer.toString(user.getWeeklyGoalHour()) + " h, ");
        textViewRestingHeartRate.setText(Integer.toString(user.getRestHeartRate()) + " bpm");
        textViewMaximumHeartRate.setText(Integer.toString(user.getMaxHeartRate()) + " bpm");
        updateBMRToUI();

        profileSaveButton.setOnClickListener(this);
        linearWeeklyGoalsLayout.setOnClickListener(this);
        linearHeightSelectionLayout.setOnClickListener(this);
        linearWeightSelectionLayout.setOnClickListener(this);
        linearAgeSelectionLayout.setOnClickListener(this);
        linearUserNameLayout.setOnClickListener(this);
        linearRestHeartRateLayout.setOnClickListener(this);
        linearMaxHeartRateLayout.setOnClickListener(this);
        linearGenderSelectionLayout.setOnClickListener(this);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int age = ZonedDateTime.now().getYear() - year;
                int maxHr = calculateMaximumHeartRate(age);
                String birthday = Integer.toString(age);
                textViewBirthday.setText(birthday);
                textViewMaximumHeartRate.setText(Integer.toString(maxHr));
                user.setAge(age);
                user.setMaxHeartRate(maxHr);
                updateBMRToUI();
                datePickerDialog.dismiss();
            }
        };
/**
 * Not used currently
 * @author Yana Krylova
 */
//        ImageButton userPhoto = (ImageButton) findViewById(R.id.user_photo);

//        userPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
//            }
//        });
    }

// set profile image
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        Bitmap bitmap = null;
//        ImageButton userPhoto = (ImageButton) findViewById(R.id.user_photo);
//        switch (requestCode) {
//            case GALLERY_REQUEST:
//                if (resultCode == RESULT_OK) {
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    userPhoto.setImageBitmap(bitmap);
//                }
//        }
//    }

    /**
     * Long onClick method for all listeners with switch method
     * This is used to take inputs from pop up layouts
     *
     * @param view Clicked view
     * @author Rasmus Hyyppä
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.height_selection_layout:
                final View selectHeightPopUp = getLayoutInflater().inflate(R.layout.pop_up_height, null);
                Button selectHeightSaveButton = selectHeightPopUp.findViewById(R.id.buttonSaveHeightPopUp);
                SeekBar seekBarHeight = selectHeightPopUp.findViewById(R.id.seekBarSelectHeightPopUp);
                TextView textViewSeekBarHeightValue = selectHeightPopUp.findViewById(R.id.textViewSeekBarHeightValue);
                textViewSeekBarHeightValue.setText(user.getHeight() + " centimeters");
                seekBarHeight.setMax(300);
                seekBarHeight.setMin(0);
                seekBarHeight.setProgress(user.getHeight()); //SET PROGRESS
                dialogBuilder.setView(selectHeightPopUp);
                dialog = dialogBuilder.create();
                dialog.show();
                seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarHeightValue.setText(seekBarHeight.getProgress() + " centimeters");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarHeightValue.setText(seekBarHeight.getProgress() + " centimeters");
                    }
                });
                selectHeightSaveButton.setOnClickListener(view1 -> {
                    user.setHeight(seekBarHeight.getProgress());
                    textViewHeight.setText(Integer.toString(seekBarHeight.getProgress()) + " centimeters");
                    updateBMRToUI();
                    dialog.dismiss();
                });
                break;
            case R.id.weight_selection_layout:
                final View selectWeightPopUp = getLayoutInflater().inflate(R.layout.pop_up_weight, null);
                Button selectWeightSaveButton = selectWeightPopUp.findViewById(R.id.buttonSaveWeightPopUp);
                SeekBar seekBarWeight = selectWeightPopUp.findViewById(R.id.seekBarSelectWeightPopUp);
                TextView textViewSeekBarWeightValue = selectWeightPopUp.findViewById(R.id.textViewSeekBarWeightValue);
                textViewSeekBarWeightValue.setText(user.getWeight() + " kilograms");
                seekBarWeight.setMax(150);
                seekBarWeight.setMin(30);
                seekBarWeight.setProgress(user.getWeight()); //SET PROGRESS
                dialogBuilder.setView(selectWeightPopUp);
                dialog = dialogBuilder.create();
                dialog.show();
                seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarWeightValue.setText(seekBarWeight.getProgress() + " kilograms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarWeightValue.setText(seekBarWeight.getProgress() + " kilograms");
                    }
                });
                selectWeightSaveButton.setOnClickListener(view1 -> {
                    user.setWeight(seekBarWeight.getProgress());
                    textViewWeight.setText(Integer.toString(seekBarWeight.getProgress()) + " kilograms");
                    updateBMRToUI();
                    dialog.dismiss();
                });
                break;
            case R.id.gender_selection_layout:
                new AlertDialog.Builder(this)
                        .setTitle("Select gender")
                        .setMessage("Gender is used for calculating calories")
                        .setNegativeButton("Male", (arg0, arg1) -> {
                            textViewGender.setText("Male");
                            user.setGender("Male");
                        })
                        //Generic lambda arg0,arg1 for yes & no alertdialog
                        .setPositiveButton("Female", (arg0, arg1) -> {
                            textViewGender.setText("Female");
                            user.setGender("Female");
                        }).create().show();
                break;
            case R.id.weekly_goals_layout:
                final View selectWeeklyGoalPopUp = getLayoutInflater().inflate(R.layout.pop_up_weekly_goal, null);
                Button selectWeeklyGoalButton = selectWeeklyGoalPopUp.findViewById(R.id.buttonSaveWeeklyPopUp);
                SeekBar seekBarHours = selectWeeklyGoalPopUp.findViewById(R.id.seekBarWeeklyHoursPopUp);
                SeekBar seekBarMinutes = selectWeeklyGoalPopUp.findViewById(R.id.seekBarWeeklyMinutesPopUp);
                TextView textViewSeekBarMinuteValue = selectWeeklyGoalPopUp.findViewById(R.id.textViewSeekBarMinutesValue);
                TextView textViewSeekBarHourValue = selectWeeklyGoalPopUp.findViewById(R.id.textViewSeekBarHourValue);

                textViewSeekBarHourValue.setText(user.getWeeklyGoalHour() + " hours");
                seekBarHours.setMax(35);
                seekBarHours.setMin(0);
                seekBarHours.setProgress(user.getWeeklyGoalHour());//SET PROGRESS

                textViewSeekBarMinuteValue.setText(user.getWeeklyGoalMinute() + " minutes");
                seekBarMinutes.setMax(60);
                seekBarMinutes.setMin(0);
                seekBarMinutes.setProgress(user.getWeeklyGoalMinute());//SET PROGRESS

                dialogBuilder.setView(selectWeeklyGoalPopUp);
                dialog = dialogBuilder.create();
                dialog.show();
                seekBarHours.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarHourValue.setText(seekBarHours.getProgress() + " hours");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarHourValue.setText(seekBarHours.getProgress() + " hours");
                    }
                });

                seekBarMinutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarMinuteValue.setText(seekBarMinutes.getProgress() + " minutes");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarMinuteValue.setText(seekBarMinutes.getProgress() + " minutes");
                    }
                });

                selectWeeklyGoalButton.setOnClickListener(view1 -> {
                    user.setWeeklyGoalHour(seekBarHours.getProgress());
                    user.setWeeklyGoalMinute(seekBarMinutes.getProgress());
                    textViewWeeklyGoalsMinutes.setText(Integer.toString(seekBarMinutes.getProgress()) + " min");
                    textViewWeeklyGoalsHours.setText(Integer.toString(seekBarHours.getProgress()) + " h, ");
                    dialog.dismiss();
                });
                break;
            case R.id.age_selection_layout:
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, ZonedDateTime.now().getYear() - user.getAge());
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(
                        ProfileActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                break;

            case R.id.maximum_heartrate_layout:
                final View selectMaximumHeartRate = getLayoutInflater().inflate(R.layout.pop_up_max_heart_rate, null);
                Button selectMaximumHeartRateSaveButton = selectMaximumHeartRate.findViewById(R.id.buttonSaveMaximumHeartRatePopUp);
                SeekBar seekBarMaximumHeartRate = selectMaximumHeartRate.findViewById(R.id.seekBarMaximumHeartRatePopUp);
                TextView textViewSeekBarHeartRateMaximumValue = selectMaximumHeartRate.findViewById(R.id.textViewSetYourMaximumHeartRatePopUp);
                textViewSeekBarHeartRateMaximumValue.setText(user.getMaxHeartRate() + " bpm");
                seekBarMaximumHeartRate.setMax(220);
                seekBarMaximumHeartRate.setMin(130);
                seekBarMaximumHeartRate.setProgress(user.getMaxHeartRate()); //SET PROGRESS
                dialogBuilder.setView(selectMaximumHeartRate);
                dialog = dialogBuilder.create();
                dialog.show();
                seekBarMaximumHeartRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarHeartRateMaximumValue.setText(seekBarMaximumHeartRate.getProgress() + " bpm");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarHeartRateMaximumValue.setText(seekBarMaximumHeartRate.getProgress() + " bpm");
                    }
                });
                selectMaximumHeartRateSaveButton.setOnClickListener(view1 -> {
                    user.setMaxHeartRate(seekBarMaximumHeartRate.getProgress());
                    textViewMaximumHeartRate.setText(Integer.toString(seekBarMaximumHeartRate.getProgress()) + " bpm");
                    dialog.dismiss();
                });
                break;
            case R.id.resting_heart_rate_layout:
                final View selectRestingHeartRate = getLayoutInflater().inflate(R.layout.pop_up_min_heart_rate, null);
                Button selectRestingHeartRateSaveButton = selectRestingHeartRate.findViewById(R.id.buttonSaveRestingHeartRate);
                SeekBar seekBarRestingHeartRate = selectRestingHeartRate.findViewById(R.id.seekBarRestingHeartRatePopUp);
                TextView textViewSeekBarHeartRateRestValue = selectRestingHeartRate.findViewById(R.id.textViewSeekBarRestHeartRateValue);
                textViewSeekBarHeartRateRestValue.setText(user.getRestHeartRate() + " bpm");
                seekBarRestingHeartRate.setProgress(user.getRestHeartRate()); //SET PROGRESS
                seekBarRestingHeartRate.setMax(150);
                seekBarRestingHeartRate.setMin(0);
                dialogBuilder.setView(selectRestingHeartRate);
                dialog = dialogBuilder.create();
                dialog.show();
                seekBarRestingHeartRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        textViewSeekBarHeartRateRestValue.setText(seekBarRestingHeartRate.getProgress() + " bpm");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textViewSeekBarHeartRateRestValue.setText(seekBarRestingHeartRate.getProgress() + " bpm");
                    }
                });
                selectRestingHeartRateSaveButton.setOnClickListener(view1 -> {
                    user.setRestHeartRate(seekBarRestingHeartRate.getProgress());
                    textViewRestingHeartRate.setText(Integer.toString(seekBarRestingHeartRate.getProgress()) + " bpm");
                    dialog.dismiss();
                });
                break;
            case R.id.profile_button_save:
                user.setUserName(userName.getText().toString());
                mainViewModel.updateUser(user);
                Intent intentForMainActivity = new Intent(getBaseContext(), MainActivity.class);
                intentForMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentForMainActivity);
                break;
        }
    }

    //Method to calculate maximum heart rate
    private int calculateMaximumHeartRate(int curAge) {
        // Max HeartRate calculated from age 208-(0,7 * 52) = 171,6
        return (int) (208 - (0.7 * curAge));
    }

    //Method to update BMR
    private void updateBMRToUI() {
        int basalMetabolicRate = getBasalMetabolicRate(user);
        if (basalMetabolicRate == getBasalMetabolicRate(new User())) {
            return; //no estimate for new users
        }
        textViewBasalMetabolicRate.setText("Estimated BMR: " + Integer.toString(getBasalMetabolicRate(user)));
    }

    /**
     * Menu for profile activity, it includes info button that opens up dialog of information.
     *
     * @param menu Menu from resource file, this param is done in activities OnCreate()
     * @return Returns true after option menu is set up.
     * @author Rasmus Hyyppä
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userprofile_menu_about_button) {
            final View aboutSporttiLayout = getLayoutInflater().inflate(R.layout.sportti_info_layout, null);
            Button continue_button_about = aboutSporttiLayout.findViewById(R.id.sportti_about_continue_button);
            dialogBuilder.setView(aboutSporttiLayout);
            dialog = dialogBuilder.create();
            dialog.show();
            continue_button_about.setOnClickListener(view -> dialog.dismiss());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null) { //See if there is any active dialogs active when going onPause
            dialog.dismiss(); //If there is dismiss it.
        }
    }
}


