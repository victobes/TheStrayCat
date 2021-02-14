package com.example.thestraycat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thestraycat.Adapters.CommentAdapter;
import com.example.thestraycat.Models.Comment;
import com.example.thestraycat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


//Подробная информация о заметке, которая предоставляется пользователям приложения
//(заголовок; текст,; фото, загруженное автором; аватарка и имя автора заметки)

public class PostActivity extends AppCompatActivity {


    private ShapeableImageView noteImage;
    private ShapeableImageView noteUserPhoto;
    private TextView noteDescription, noteTitle, noteUserName, txtPostDateName;
    private String PostKey;
    //private ImageView imgCurrentUser;
    private ShapeableImageView imgCurrentUser;

    private EditText editTextComment;
    //private TextInputLayout textComment;
    //private TextInputEditText editTextComment;
    //private Button btnAddComment;
    private MaterialButton btnAddComment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    RecyclerView RvComment;
    private CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.note_activity);
        setContentView(R.layout.post_activity_test);
        RvComment = findViewById(R.id.rv_comment);

        /*Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();*/

        noteImage = findViewById(R.id.note_activity_image);
        noteUserPhoto = findViewById(R.id.note_activity_user_photo);
        noteDescription = findViewById(R.id.note_detail__text);
        noteTitle = findViewById(R.id.note_activity_title);
        noteUserName = findViewById(R.id.note_activity_username);
        txtPostDateName = findViewById(R.id.post_detail_date_name);

        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        editTextComment = findViewById(R.id.post_detail_comment);
        //textComment = findViewById(R.id.post_comment);

        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAddComment.setVisibility(View.INVISIBLE);

                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content, uid, uimg, uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PostActivity.this, "Комментарий успешно добавлен!", Toast.LENGTH_LONG).show();
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, "Ошибка : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        String nImage = getIntent().getExtras().getString("noteImage");
        Glide.with(PostActivity.this).load(nImage).into(noteImage);

        String nTitle = getIntent().getExtras().getString("noteTitle");
        noteTitle.setText(nTitle);

        String nDescription = getIntent().getExtras().getString("noteDescription");
        noteDescription.setText(nDescription);

        String nUsername = getIntent().getExtras().getString("noteUserName");
        noteUserName.setText(nUsername);

        String nUserPhoto = getIntent().getExtras().getString("noteUserPhoto");
        Glide.with(PostActivity.this).load(nUserPhoto).into(noteUserPhoto);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        PostKey = getIntent().getExtras().getString("noteKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        iniRvComment();

    }

    private void iniRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;

                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}