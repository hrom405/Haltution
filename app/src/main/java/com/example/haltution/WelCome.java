package com.example.haltution;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.haltution.login.CreateNew;
import com.example.haltution.login.LoginPage;

public class WelCome extends AppCompatActivity {

    private Button submit;
    private Button login;
    private CheckBox student;
    private CheckBox teacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        login = findViewById(R.id.welcome_login);
        submit = findViewById(R.id.welcome_submit);
        student = findViewById(R.id.welcome_student);
        teacher = findViewById(R.id.welcome_teacher);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(i);
            }
        });

    }
}