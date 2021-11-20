package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");
    // Password@4
//                    "(?=.*[a-zA-Z])" +      //any letter

    private EditText mEmail;
    private EditText mPass;
    private TextInputLayout mTextlayoutEmailReg;
    private TextInputLayout mTextlayoutPassReg;


    private Button btnReg;
    private TextView mSignin;

    //Progress Dialog
    private ProgressDialog mDialog;

    //Firebase...
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);


        mTextlayoutEmailReg = findViewById(R.id.xLayoutEmailReg);
        mTextlayoutPassReg = findViewById(R.id.xLayoutPassReg);
//        mEmail = findViewById(R.id.xEmailRegistration);
//        mPass = findViewById(R.id.xRegistrationPassword);

        registration();

    }

    private boolean validateEmail() {
        String emailInput = mTextlayoutEmailReg.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(emailInput)) {
            mTextlayoutEmailReg.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            mTextlayoutEmailReg.setError("Please enter a valid email address");
            return false;
        } else {
            mTextlayoutEmailReg.setError(null);
            return true;
        }
    }

    private boolean validatePass() {
        String passwordInput = mTextlayoutPassReg.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(passwordInput)) {
            mTextlayoutPassReg.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            mTextlayoutPassReg.setError("Password must be at least 6 characters,minimum one character lower,upper,numeric and special character i.e.,Password@4");
            return false;
        } else {
            mTextlayoutPassReg.setError(null);
            return true;
        }
    }

    private void registration() {


        btnReg = findViewById(R.id.xButton_registraion);
        mSignin = findViewById(R.id.xAllreadyhaveanaccount);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = mTextlayoutEmailReg.getEditText().getText().toString().trim();
                String passwordInput = mTextlayoutPassReg.getEditText().getText().toString().trim();
//                String email = mEmail.getText().toString().trim();
//                String pass = mPass.getText().toString().trim();


                mDialog.setMessage("Processing..");
                mDialog.show();

                //first condition
//                if (TextUtils.isEmpty(email)) {
//                    mEmail.setError("Email Required..");
//                    mDialog.dismiss();
//                    return;
//                }
                //second condition
//                if (TextUtils.isEmpty(pass)) {
//                    mPass.setError("Password Required..");
//                    mDialog.dismiss();
//                }

//                if (!validateEmail()) {
//                    return;
//                }
//                if (!validatePass()) {
//                    return;
//                }


                if (validateEmail() && validatePass()) {
                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //if user regis successfully done
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                //Toast.makeText(RegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    mDialog.dismiss();
                }
            }
        });


        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


    }
}