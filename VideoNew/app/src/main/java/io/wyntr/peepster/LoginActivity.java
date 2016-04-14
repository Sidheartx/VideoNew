package io.wyntr.peepster;

/**
 * Created by Siddharth on 3/22/16.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.HashMap;


//import com.parse.anyphone.R;
//import com.parse.anyphone.MainActivity;
//import com.parse.anyphone.R;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String OTP_DELIMITER = "is ";
    public static final String SMS_ORIGIN = "+1585-300-4260";

    private static String[] PERMISSIONS_SMS = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};


    public static final int READ_SMS = 1;

    public static String phoneNumber = null;
    private TextView questionLabel, substituteLabel;
    private EditText textField;
    private Button sendCodeButton;
    private String token = null;
    private int code = 0;
    private int flag = 0; //If flag = 0 call the sendCode method, otherwise call the doLogin method.

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        progressBar = (ProgressBar) findViewById(R.id.loadingSpinner);
        questionLabel = (TextView) findViewById(R.id.questionLabel);
        substituteLabel = (TextView) findViewById(R.id.subtitleLabel);
        textField = (EditText) findViewById(R.id.phoneNumberField);
        sendCodeButton = (Button) findViewById(R.id.sendCodeButton);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            requestSMSPermissions();
            return;
        }

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCodeButton.setClickable(false);
                if (flag == 0)
                    sendCode();
                else
                    doLogin();
            }
        });
        phoneNumberUI();

    }

    private void phoneNumberUI() {

        flag = 0;
        questionLabel.setText("Please enter your phone number to log in:");
        substituteLabel.setText("This example is limited to 10-digit US numbers");
        textField.setHint(R.string.number_default);
        sendCodeButton.setClickable(true);

    }

    private void requestSMSPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");
            // Display a SnackBar with an explanation and a button to trigger the request.
            ActivityCompat
                    .requestPermissions(LoginActivity.this, PERMISSIONS_SMS,
                            READ_SMS);
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_SMS, READ_SMS);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            String message = b.getString("otp");
            Toast.makeText(getApplicationContext(), "OTP is " + message, Toast.LENGTH_LONG).show();

            Log.e("newmesage", "" + message);

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_SMS) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Recieve perm granted", Toast.LENGTH_LONG).show();


            }
            // permission was granted, yay! Do the
            // contacts-related task you need to do.

            //else if (requestCode == READ_SMS) {
            //  if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //Toast.makeText(getApplicationContext(),"Read perm granted",Toast.LENGTH_LONG).show();
            // Camera permission has been granted, preview can be displayed
            // Checks the last saved location to show cached data if it's available
            //}
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//}

    public void codeUI(){



        String reccode = getIntent().getStringExtra("otp");
        Toast.makeText(getApplicationContext(),
                "You must enter the 4 digit code texted to your phone number." + reccode,
                Toast.LENGTH_LONG).show();
        flag = 1;

        questionLabel.setText("Enter the 4-digit confirmation code:");
        substituteLabel.setText("It was sent in an SMS message to +1" + phoneNumber);
        textField.setText("");
        textField.setHint("1234");
        sendCodeButton.setClickable(true);
    }

    private void sendCode() {
        if(textField.getText().toString().length() != 10){
            Toast.makeText(getApplicationContext(),
                    "You must enter a 10-digit US phone number including area code.",
                    Toast.LENGTH_LONG).show();
            phoneNumberUI();
        } else{
            phoneNumber = String.valueOf(textField.getText());
            String zip= "+" + GetCountryZipCode();
            Toast.makeText(getApplicationContext(),zip,Toast.LENGTH_LONG);


            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phoneNumber", phoneNumber);
            params.put("zipcode", zip);
            progressBar.setVisibility(View.VISIBLE);
            ParseCloud.callFunctionInBackground("sendCode", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap hashMap, ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    doLogin();

                }


                public void done(JSONObject response, ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    if (e == null) {
                        Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                        codeUI();
                    } else {
                        Log.d("Cloud Response", "Exception: " + response.toString() + e);
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong.  Please try again." + e,
                                Toast.LENGTH_LONG).show();
                        phoneNumberUI();
                    }
                }
            });
        }
    }

    private void doLogin() {
       /* String otp = getIntent().getStringExtra("otp");
        Toast.makeText(getApplicationContext(),
                "You must enter the 4 digit code texted to your phone number." + otp,
                Toast.LENGTH_LONG).show();*/


        if(textField.getText().toString().length() != 4) {
            Toast.makeText(getApplicationContext(),
                    "You must enter the 4 digit code texted to your phone number.",
                    Toast.LENGTH_LONG).show();
            codeUI();
        } else {
            code = Integer.parseInt(textField.getText().toString());
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("phoneNumber", phoneNumber);
            params.put("codeEntry", code);

            progressBar.setVisibility(View.VISIBLE);
            ParseCloud.callFunctionInBackground("logIn", params, new FunctionCallback<String>() {
                public void done(String response, ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    if (e == null) {
                        token = response;
                        Log.d("Cloud Response", "There were no exceptions! " + response);
                        ParseUser.becomeInBackground(token, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    Log.d("Cloud Response", "There were no exceptions! ");
                                    Intent i = new Intent(LoginActivity.this, EditProfileActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Log.d("Cloud Response", "Exception: " + e);
                                    Toast.makeText(getApplicationContext(),
                                            "Something went wrong.  Please try again." + e,
                                            Toast.LENGTH_LONG).show();
                                    phoneNumberUI();
                                }
                            }
                        });
                    } else {
                        phoneNumberUI();
                        Log.d("Cloud Response", "Exception: " + response + e);
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong.  Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }


    /////////classes to detect from the sms///////////



  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/



}
