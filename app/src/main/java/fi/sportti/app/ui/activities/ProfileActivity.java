package fi.sportti.app.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import fi.sportti.app.R;
import androidx.annotation.RequiresApi;
import java.io.IOException;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)

public class ProfileActivity extends MainActivity {

    private TextView Weight, Height;

    private String userName;

//static final for image requesting
    static final int GALLERY_REQUEST = 1;

//values for date set up
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView text_weight = (TextView) findViewById(R.id.text_weight);
        TextView text_height = (TextView) findViewById(R.id.text_height);

        EditText name = (EditText) findViewById(R.id.user_name);
        TextView date = (TextView) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProfileActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1; //because January is 0
                String birthday = day + "/" + month + "/" + year;
                date.setText(birthday);
            }
        };

        //getting the name
        String userName = name.getText().toString();
        ImageButton userPhoto = (ImageButton) findViewById(R.id.user_photo);

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        ////Spinner for weight
        Spinner weight = (Spinner) findViewById(R.id.weight);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProfileActivity.this,
                R.array.weight_values,
                android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        weight.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener1 = new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String weight_item = (String)parent.getItemAtPosition(position);
                text_weight.setText(weight_item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        weight.setOnItemSelectedListener(itemSelectedListener1);

        //Spinner for height
        Spinner height = (Spinner) findViewById(R.id.height);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(ProfileActivity.this,
                R.array.height_values,
                android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        height.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String height_item = (String)parent.getItemAtPosition(position);
                text_height.setText(height_item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        height.setOnItemSelectedListener(itemSelectedListener2);
    }

    // set profile image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;
        ImageButton userPhoto = (ImageButton) findViewById(R.id.user_photo);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userPhoto.setImageBitmap(bitmap);
                }
        }
    }
}



