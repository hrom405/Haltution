package com.example.haltution.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haltution.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private EditText inputEmail;
    private EditText PassWord;
    private FirebaseAuth mAuth;
    private ProgressDialog progressBar;
    private Button submit;
    private CheckBox showPassword;
    private TextView createNew;
    private TextView forgetPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        inputEmail = findViewById(R.id.login_Email);
        submit = findViewById(R.id.login_loginButton);
        progressBar = new ProgressDialog(this);
        showPassword = findViewById(R.id.login_showPassWord);
        PassWord = findViewById(R.id.login_Password);
        createNew = findViewById(R.id.login_createNew);
        forgetPw = findViewById(R.id.login_forgetPassword);

        mAuth = FirebaseAuth.getInstance();


        //Show Password if checkbox is true
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else if (!isChecked) {
                    PassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }


        });


       //forget passWord Page
        forgetPw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPassWord.class);
                startActivity(i);
            }
        });

        //signUp Page
        createNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), signUp.class);
                startActivity(i);
            }
        });


        //login button onClick action
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = PassWord.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setMessage("Login..");
                progressBar.show();

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.hide();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        PassWord.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginPage.this, "Authentication failed Check Email And PassWord", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser name = mAuth.getCurrentUser();
                                    Intent i = new Intent(getApplicationContext(), afterlogin.class);
                                    startActivity(i);
                                }
                            }
                        });
            }
        });


    }

}