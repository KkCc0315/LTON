package com.utar.lton;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout ll;
    private List<String> cardDataList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cardDataList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ll = view.findViewById(R.id.cardView);

        // Read data from Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("activity");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear previous data
                cardDataList.clear();
                ll.removeAllViews();

                TextView textView = new TextView(requireContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                textView.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tHome Page");
                textView.setTextSize(26);
                textView.setGravity(Gravity.CENTER);

                ll.addView(textView);

                // Iterate through dataSnapshot to get each data item
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve each data item
                    String data = snapshot.getValue(String.class);
                    // Add the data item to the list
                    cardDataList.add(data);
                }

                // Update UI with retrieved data
                addCardViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        return view;
    }

    private void addCardViews() {
        // Add card views for each item in the data list
        for (int i = 0; i < cardDataList.size(); i++) {
            final String data = cardDataList.get(i); // Retrieve data at position i

            // Create a new CardView
            CardView cardView = new CardView(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 20, 20, 20); // Set margins
            cardView.setLayoutParams(layoutParams);
            cardView.setRadius(10); // Set corner radius
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setCardElevation(10); // Set elevation

            // Create a LinearLayout to hold text and delete button
            LinearLayout cardContentLayout = new LinearLayout(requireContext());
            cardContentLayout.setOrientation(LinearLayout.VERTICAL);
            cardContentLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cardContentLayout.setPadding(20, 20, 20, 20); // Set padding

            // Add a TextView to the LinearLayout
            TextView textView = new TextView(requireContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            textView.setText(data);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            cardContentLayout.addView(textView);

            // Add a delete button to the LinearLayout
            Button deleteButton = new Button(requireContext());
            deleteButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            deleteButton.setText("Delete");
            cardContentLayout.addView(deleteButton);

            // Set OnClickListener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create a confirmation dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove the corresponding card view and data from the list
                            ll.removeView(cardView);
                            cardDataList.remove(data);

                            // Remove the corresponding data from Firebase Realtime Database
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("activity");
                            dbRef.orderByValue().equalTo(data).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue(); // Remove the data from Firebase
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    });

                    // Display the confirmation dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            // Add the LinearLayout to the CardView
            cardView.addView(cardContentLayout);

            // Add the CardView to the parent layout
            ll.addView(cardView);
        }
    }
}
