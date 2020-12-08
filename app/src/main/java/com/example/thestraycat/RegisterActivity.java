package com.example.thestraycat;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//Регистрация пользователя(имя пользователя, адрес эл.почты, пароль, аватарка - по желанию)
//Используется Firebase Authentication и Storage

public class RegisterActivity extends AppCompatActivity {


    ImageView registerUserPhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    private EditText userEmail, userPassword, userPasswordRepeat, userName;
    private ProgressBar progressRegister;
    private Button buttonRegister;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPasswordRepeat = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        progressRegister = findViewById(R.id.regProgressBar);
        buttonRegister = findViewById(R.id.regBtn);
        progressRegister.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonRegister.setVisibility(View.INVISIBLE);
                progressRegister.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String passwordRepeat = userPasswordRepeat.getText().toString();
                final String name = userName.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(passwordRepeat)) {

                    Toast.makeText(getApplicationContext(), R.string.toast_about_fields, Toast.LENGTH_LONG).show();
                    buttonRegister.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.INVISIBLE);

                } else {

                    CreateUserAccount(email, name, password);
                }

            }
        });

        registerUserPhoto = findViewById(R.id.regUserPhoto);

        registerUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAndRequestForPermission();
                openGallery();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }


    private void CreateUserAccount(String email, final String name, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), R.string.toast_registration_is_ok, Toast.LENGTH_LONG).show();

                            //Добавление аватарки и имени пользователя
                            if (pickedImgUri != null) {
                                updateUserInfoWithPhoto(name, pickedImgUri, mAuth.getCurrentUser());
                            } else {
                                updateUserInfoWithoutPhoto(name, mAuth.getCurrentUser());
                            }

                        } else {

                            Toast.makeText(getApplicationContext(), R.string.toast_registration_is_failed + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            buttonRegister.setVisibility(View.VISIBLE);
                            progressRegister.setVisibility(View.INVISIBLE);

                        }
                    }
                });

    }


    private void updateUserInfoWithPhoto(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        //Загрузка фото в облачное хранилище и получение uri
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // получение url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(getApplicationContext(),R.string.toast_after_registration,Toast.LENGTH_LONG).show();
                                            updateUI();
                                        }

                                    }
                                });

                    }
                });

            }
        });

    }


    private void updateUserInfoWithoutPhoto(final String name, final FirebaseUser currentUser) {

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();


        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),R.string.toast_after_registration,Toast.LENGTH_LONG).show();
                            updateUI();
                        }

                    }
                });

    }


    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();

            } else {

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else

            openGallery();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {

            pickedImgUri = data.getData();
            registerUserPhoto.setImageURI(pickedImgUri);

        }

    }
}