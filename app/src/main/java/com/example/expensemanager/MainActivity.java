package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout mEmailLogin;
    private TextInputLayout mPassLogin;
    private Button buttonLogin;
    private TextView mSignupHere;
    private TextView mForgettextlink;

    private ProgressDialog mDialog;
    //Firebase..
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        //Jab tak use logged ha tab tak login krke ki jarurt ni ha
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        loginDetails();


    }

    //    Login details
    private void loginDetails() {
        mEmailLogin = findViewById(R.id.xLayoutEmailLogin);
        mPassLogin = findViewById(R.id.xLayoutPassLogin);
        buttonLogin = findViewById(R.id.xButton_login);
        mSignupHere = findViewById(R.id.xSignup_here);
        mForgettextlink = findViewById(R.id.xForget_Password);

        // Login Button Activity
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailLogin.getEditText().getText().toString().trim();
                String pass = mPassLogin.getEditText().getText().toString().trim();


                mDialog.setMessage("Processing...");
                mDialog.show();

                //checking email field is not empty
                if (TextUtils.isEmpty(email)) {
                    mEmailLogin.setError("Field can't be empty..");
                    mDialog.dismiss();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailLogin.setError("Please enter valid email address..");
                    mDialog.dismiss();
                    return;
                }


                mEmailLogin.setError("");
                //checking pass filed is not empty
                if (TextUtils.isEmpty(pass)) {
                    mPassLogin.setError("Field can't be empty..");
                    mDialog.dismiss();
                    return;
                }
                mPassLogin.setError("enter valid password");


                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mPassLogin.setError("");
                            mDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Login Successful..", Toast.LENGTH_LONG).show();

                        } else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Failed.. Please enter valid email and password..", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        // Registration Actvity
        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                finish();
            }
        });

        // Reset Password Activity
        mForgettextlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passRestDialog = new AlertDialog.Builder(v.getContext());
                passRestDialog.setTitle("Reset Password ?");
                passRestDialog.setMessage("Enter your email to received rest link");
                passRestDialog.setView(resetMail);




                passRestDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog

                    }
                });


                passRestDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset mail
                        String mail = resetMail.getText().toString();

                        if(TextUtils.isEmpty(mail)){
                            resetMail.setError("enter your email address..");
                            return;
                        }


                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Reset Link Sent To Your Email..",Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error ! Reset Link is Not Sent " , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



                passRestDialog.create().show();
//                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });


    }
}