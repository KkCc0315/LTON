package com.utar.lton;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class  ProfileFragment extends Fragment {

    TextView profileUserName,profileEmail;
    TextView titleUserName;
    Button editProfile;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileUserName = view.findViewById(R.id.profileUserName);
        titleUserName = view.findViewById(R.id.titleUserName);
        editProfile = view.findViewById(R.id.editButton);
        profileEmail = view.findViewById(R.id.profileEmail);

        showUserData();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserData();
            }
        });
        return view;
    }

    private void showUserData() {
        Intent intent = getActivity().getIntent();

        if (intent != null) {
            String emailUser = intent.getStringExtra("email");
            String usernameUser = intent.getStringExtra("username");
            titleUserName.setText(usernameUser);
            profileUserName.setText(usernameUser);
            profileEmail.setText(emailUser);
        }
    }

    public void UpdateUserData(){
        String userUsername = profileUserName.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);

                    Intent intent = new Intent(getActivity(),EditProfileActivity.class);

                    intent.putExtra("username",usernameFromDB);
                    intent.putExtra("password",passwordFromDB);
                    intent.putExtra("email",emailFromDB);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }});
    }
}