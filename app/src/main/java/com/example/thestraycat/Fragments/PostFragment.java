package com.example.thestraycat.Fragments;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thestraycat.Models.Post;
import com.example.thestraycat.Adapters.PostAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUESTCODE = 2;

    private String mParam1;
    private String mParam2;

    private Dialog posts;
    //private Button button, noteButton;
    //private Button postButton;
    private MaterialButton postButton, button;

    private View fragmentView;
    private ShapeableImageView postUserPhoto;
    private ImageView postPhoto;
    //private EditText postTitle, postDescription;
    //private EditText postTitle;
    private TextInputLayout postDescription, postTitle;
    private TextInputEditText postDescriptionEdit, postTitleEdit;
    private FirebaseUser currentUser;
    private Uri pickedImageUri = null;

    private String postUserName;

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;

    private DatabaseReference reference;

    private List<Post> postList;

    private OnFragmentInteractionListener mListener;

    public PostFragment() {

    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_posts, container, false);
        button = fragmentView.findViewById(R.id.create_post_button);

        initializationNote();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).into(postUserPhoto);
        } else {
            Glide.with(this).load(R.drawable.test_image_2).into(postUserPhoto);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posts.show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Notes");

        postRecyclerView = fragmentView.findViewById(R.id.note_recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);

        return fragmentView;

    }

    @Override
    public void onStart() {
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList = new ArrayList<>();

                for (DataSnapshot notes : dataSnapshot.getChildren()) {

                    Post post = notes.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter = new PostAdapter(getActivity(), postList);
                postRecyclerView.setAdapter(postAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializationNote() {

        posts = new Dialog(getContext());
        posts.setContentView(R.layout.dialog_view_create_post);
        posts.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        posts.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        posts.getWindow().getAttributes().gravity = Gravity.TOP;

        postUserPhoto = posts.findViewById(R.id.note_user_image);
        postTitle = posts.findViewById(R.id.post_title);
        postTitleEdit = posts.findViewById(R.id.post_title_edit);
        postDescription = posts.findViewById(R.id.post_description);
        postDescriptionEdit = posts.findViewById(R.id.post_description_edit);
        postPhoto = posts.findViewById(R.id.note_image);
        //noteUserName =
        postPhoto.setImageResource(R.drawable.add_image);

        postButton = posts.findViewById(R.id.add_post_button);

        postPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!postTitleEdit.getText().toString().isEmpty()
                        && !postDescriptionEdit.getText().toString().isEmpty()
                        && pickedImageUri != null) {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("note_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();

                                    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                            "://" + getResources().getResourcePackageName(R.drawable.add_image)
                                            + '/' + getResources().getResourceTypeName(R.drawable.add_image)
                                            + '/' + getResources().getResourceEntryName(R.drawable.add_image));

                                    Post post;

                                    if (currentUser.getPhotoUrl() != null) {
                                        post = new Post(postTitleEdit.getText().toString(),
                                                postDescriptionEdit.getText().toString(), currentUser.getPhotoUrl().toString(),
                                                imageDownlaodLink,
                                                currentUser.getUid(), currentUser.getDisplayName());
                                    } else {
                                        post = new Post(postTitleEdit.getText().toString(),
                                                postDescriptionEdit.getText().toString(), imageUri.toString(),
                                                imageDownlaodLink,
                                                currentUser.getUid(), currentUser.getDisplayName());
                                    }

                                    addNote(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(getContext(), R.string.toast_about_fields, Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void addNote(Post note) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Notes").push();

        String key = db.getKey();
        note.setNoteKey(key);

        db.setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext(), R.string.toast_note_success, Toast.LENGTH_SHORT).show();
                posts.dismiss();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            pickedImageUri = data.getData();

            pickedImageUri = data.getData();
            postPhoto.setImageURI(pickedImageUri);
        }
    }
}