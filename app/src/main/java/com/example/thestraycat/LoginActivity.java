package com.example.thestraycat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Авторизация пользователя(логин и пароль).
// Возможность перейти на активность регистрации.

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private ProgressBar progressLogin;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.login_email);

        userPassword = findViewById(R.id.login_password);
        buttonLogin = findViewById(R.id.login_button);
        buttonRegister = findViewById(R.id.register_button);
        progressLogin = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, HomeActivity.class);

        loginPhoto = findViewById(R.id.login_photo);

        progressLogin.setVisibility(View.INVISIBLE);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressLogin.setVisibility(View.VISIBLE);
                buttonLogin.setVisibility(View.INVISIBLE);

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {

                    Toast.makeText(getApplicationContext(), R.string.toast_about_fields, Toast.LENGTH_LONG).show();
                    buttonLogin.setVisibility(View.VISIBLE);
                    progressLogin.setVisibility(View.INVISIBLE);

                } else {
                    signIn(mail, password);
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    progressLogin.setVisibility(View.INVISIBLE);
                    buttonLogin.setVisibility(View.VISIBLE);
                    updateUI();

                } else {

                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    buttonLogin.setVisibility(View.VISIBLE);
                    progressLogin.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void updateUI() {

        startActivity(HomeActivity);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
            //Автологин
            updateUI();
    }
}