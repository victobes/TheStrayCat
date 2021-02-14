package com.example.thestraycat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.thestraycat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// Авторизация пользователя(логин и пароль).
// Возможность перейти на активность регистрации.

public class LoginActivity extends AppCompatActivity {

    //private EditText userEmail, userPassword;
    private MaterialTextView loginText;
    private TextInputLayout userEmail, userPassword;
    private TextInputEditText userEmailEdit, userPasswordEdit;
    private MaterialButton loginButton, loginRegisterButton;

    //private Button buttonLogin, buttonRegister;
    //private ProgressBar progressLogin;

    //private ImageView loginPhoto;

    private FirebaseAuth mAuth;
    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginText = findViewById(R.id.register_text);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        userEmailEdit = findViewById(R.id.login_email_edit);
        userPasswordEdit = findViewById(R.id.login_password_edit);

        //buttonLogin = findViewById(R.id.login_button);
        //buttonRegister = findViewById(R.id.register_button);
        //progressLogin = findViewById(R.id.login_progress);

        loginButton = findViewById(R.id.login_button);;
        loginRegisterButton = findViewById(R.id.login_register_button);

        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.example.thestraycat.Activities.HomeActivity.class);

        //loginPhoto = findViewById(R.id.login_photo);

        //progressLogin.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progressLogin.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                //final String mail = userEmail.getText().toString();
                //final String password = userPassword.getText().toString();

                final String mail = userEmailEdit.getText().toString();
                final String password = userPasswordEdit.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {

                    Toast.makeText(getApplicationContext(), R.string.toast_about_fields, Toast.LENGTH_LONG).show();
                    loginButton.setVisibility(View.VISIBLE);
                    //progressLogin.setVisibility(View.INVISIBLE);

                } else {
                    signIn(mail, password);
                }
            }
        });

        loginRegisterButton.setOnClickListener(new View.OnClickListener() {
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

                    //progressLogin.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    updateUI();

                } else {

                    Toast.makeText(getApplicationContext(), "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    loginButton.setVisibility(View.VISIBLE);
                    //progressLogin.setVisibility(View.INVISIBLE);
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