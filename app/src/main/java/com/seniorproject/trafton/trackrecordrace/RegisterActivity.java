package com.seniorproject.trafton.trackrecordrace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    //variables for user data
    Button registerButton;

    String usernametxt;
    String passwordtxt;
    String weighttxt;
    int weightNum;

    String gender;
    Boolean genderEntered;

    EditText password;
    EditText username;
    EditText weight;

    CheckBox male;
    CheckBox female;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //wire up widgets
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        weight = (EditText) findViewById(R.id.weight);
        male = (CheckBox) findViewById(R.id.cb_male);
        female = (CheckBox) findViewById(R.id.cb_female);

        //register button
        registerButton = (Button) findViewById(R.id.register_button);



        //Check for the user fields and if the information is present
        //-------------------------------------------
        registerButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {

                // Retrieve the information from the user registration form
                usernametxt = username.getText().toString();

                passwordtxt = password.getText().toString();

                weighttxt = weight.getText().toString();
                weightNum = Integer.parseInt(weighttxt);




                //weight has to be between 0 and 999
                // Force user to fill up the form
                if (usernametxt.equals("") && passwordtxt.equals("") && weighttxt.equals("") && weightNum < 0 && genderEntered == false ) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.put("weight",weightNum);
                    user.put("genderCode",gender);
                    user.put("wins", 0);
                    user.put("losses", 0);
                    user.setPassword(passwordtxt);

                    /*Fields that will initially be blank, related to challenges*/

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                //--------------------------------
                                //Send the user back to the login page where they can then log into the application
                                //--------------------------------
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        LoginSignupActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

        //-------------------------------------------

    }

    public void onCheckboxClicked(View view){
        //get checkBox information, only can be clicked. Set the string to the appropriate gender code.
        if (male.isChecked()){
            female.setChecked(false);
            gender = "m";
            genderEntered = true;

        }
        else if (female.isChecked()){
            male.setChecked(false);
            gender = "f";
            genderEntered = true;
        }
        else {
            gender = "none";
            genderEntered = false;

        }

    }




    //-------------------------------------
}
