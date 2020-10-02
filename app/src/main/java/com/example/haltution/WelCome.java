package com.example.haltution;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.haltution.login.LoginPage;

public class WelCome extends AppCompatActivity {

    private Button login;
    private CardView student;
    private CardView teacher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        login = findViewById(R.id.welcome_loginButton);
        teacher = findViewById(R.id.welcome_teacher);
        student = findViewById(R.id.welcome_student);


        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Teacher.class);
                startActivity(i);
                finish();
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Student.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(i);
            }
        });

    }
}