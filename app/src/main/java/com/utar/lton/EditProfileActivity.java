package com.utar.lton;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class EditProfileActivity extends AppCompatActivity {

    ImageView profileImg;
    TextView profileUserName;
    EditText editPassword, editEmail;
    Button saveButton;
    String usernameUser, emailUser, passwordUser;
    DatabaseReference reference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri ImageUri;

    StorageReference userImageRef = storageRef.child("images/users/profile.jpg");


    Uri file = Uri.fromFile(new File("path/to/local/file.jpg"));
    UploadTask uploadTask = userImageRef.putFile(file);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseApp.initializeApp(this);

        reference = FirebaseDatabase.getInstance().getReference("users");

        editPassword = findViewById(R.id.editpassword);
        editEmail = findViewById(R.id.editemail);
        saveButton = findViewById(R.id.saveprofile_button);
        profileUserName = findViewById(R.id.profileUserName);
        profileImg = findViewById(R.id.profileImg);

        showData();

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        Glide.with(this)
                .load(ImageUri)
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImg);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailChange() && isPasswordChange() && ImageUri != null) {
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    public boolean isEmailChange() {
        if (!emailUser.equals(editEmail.getText().toString())) {
            reference.child(usernameUser).child("email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString();
            return true;
        } else {
            return false;
        }

    }

    public boolean isPasswordChange() {
        if (!passwordUser.equals(editPassword.getText().toString())) {
            reference.child(usernameUser).child("password").setValue(editPassword.getText().toString());
            passwordUser = editPassword.getText().toString();
            return true;
        } else {
            return false;
        }

    }

    public void showData() {
        Intent intent = getIntent();
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");
        emailUser = intent.getStringExtra("email");

        profileUserName.setText(usernameUser);
        editPassword.setText(passwordUser);
        editEmail.setText(emailUser);
    }
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();
            profileImg.setImageURI(ImageUri);

            uploadImageToFirebase(ImageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child("profile_images/" + fileName);


        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    final long ONE_MEGABYTE = 1024 * 1024; // 1MB
                    userImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profileImg.setImageBitmap(bitmap);

                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        reference.child(usernameUser).child("profileImageUrl").setValue(imageUrl);
    }


}