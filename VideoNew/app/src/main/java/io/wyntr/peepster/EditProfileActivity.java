package io.wyntr.peepster;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;


//import com.parse.anyphone.R;


public class EditProfileActivity extends AppCompatActivity {
    ParseUser user = ParseUser.getCurrentUser();
    private EditText nameField;
    private Switch setting1, setting2, setting3;
    private int REQUEST_CONTACTS=1;
    ImageView profilepic;
    /////calender and date picker/////
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView phoneNumberField = (TextView) findViewById(R.id.phoneNumberField);
       // phoneNumberField.setText(LoginActivity.phoneNumber);
        dateView = (TextView) findViewById(R.id.setdateview);
        calendar = Calendar.getInstance();

        nameField = (EditText) findViewById(R.id.nameField);
        Button propicchange = (Button)findViewById(R.id.propicchange);

        propicchange.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});

        profilepic = (ImageView)findViewById(R.id.profileimgView);

        //setting1 = (Switch) findViewById(R.id.setting1);
        //setting2 = (Switch) findViewById(R.id.setting2);
        //setting3 = (Switch) findViewById(R.id.setting3);

        Button saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        if (!ParseUser.getCurrentUser().isNew())
            checkSettings();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS },
                    REQUEST_CONTACTS);
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            //textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                profilepic.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
                byte[] image=stream.toByteArray();
                // Create the ParseFile
                ParseFile file = new ParseFile("profilepic", image);
                // Upload the image into Parse Cloud
                file.saveInBackground();
                // Create a column named "ImageFile" and insert the image
                user.put("profilepictures", file);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getApplicationContext(), "Saved Successfully",
                                Toast.LENGTH_SHORT).show();}});



                // Show a simple toast message
                Toast.makeText(EditProfileActivity.this, "Image Uploaded",
                        Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void checkSettings() {
        nameField.setText(user.getString("name"));
//        setting1.setChecked(user.getBoolean("setting1"));
        //      setting2.setChecked(user.getBoolean("setting2"));
        //    setting2.setChecked(user.getBoolean("setting3"));
        ParseFile fileObject = (ParseFile) user.get("profilepictures");

        if (fileObject!=null) {
            fileObject
                    .getDataInBackground(new GetDataCallback() {

                        public void done(byte[] data,
                                         ParseException e) {
                            if (e == null) {
                                // Decode the Byte[] into
                                // Bitmap
                                Bitmap bmp = BitmapFactory
                                        .decodeByteArray(
                                                data, 0,
                                                data.length);

                                // initialize
                                profilepic = (ImageView) findViewById(R.id.profileimgView);

                                // Set the Bitmap into the
                                // ImageView
                                profilepic.setImageBitmap(bmp);

                            } else {
                                Log.d("test",
                                        "Problem load image the data.");
                            }
                        }
                    });
        }
        else Toast.makeText(EditProfileActivity.this,"Please set a profile picture",Toast.LENGTH_SHORT).show();
    }

    private void saveSettings() {



        if (nameField != null) {
            user.put("name", nameField.getText().toString());

            /*if (setting1.isChecked()) {
                user.put("setting1", true);
            }
            if (setting2.isChecked()) {
                user.put("setting2", true);
            }
            if (setting3.isChecked()) {
                user.put("setting3", true);
            }*/

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getApplicationContext(), "Saved Successfully",
                            Toast.LENGTH_SHORT).show();

                }
            });
            Intent i = new Intent(EditProfileActivity.this, EditFriendsActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter a username.",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void logout() {

        user.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Intent i = new Intent(EditProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }



}
