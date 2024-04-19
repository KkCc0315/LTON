package com.utar.lton;

import static java.lang.Float.parseFloat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public EditText input_name;
    public EditText input_contact;
    public EditText input_destination;
    private EditText input_price;
    private String mParam1;
    private String mParam2;
    private ImageButton datePickerButton;
    private TextView selectedDateTimeTextView;
    private TextView errorMessage;
    private TextView priceMessage;
    private Calendar calendar;
    private Button submit;
    private Button driver;
    private TextView notification;
    private DatabaseReference mDatabase;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        input_name = view.findViewById(R.id.enterName);
        input_contact = view.findViewById(R.id.enterContact);
        input_price = view.findViewById(R.id.enterPrice);
        input_destination = view.findViewById(R.id.enterDestination);

        datePickerButton = view.findViewById(R.id.datePickerButton);
        selectedDateTimeTextView = view.findViewById(R.id.selectedDateTimeTextView);
        calendar = Calendar.getInstance();
        submit = view.findViewById(R.id.submitButton);
        notification = view.findViewById(R.id.successful);
        errorMessage = view.findViewById(R.id.errorMessage);
        priceMessage = view.findViewById(R.id.priceMessage);
        driver = view.findViewById(R.id.driver);

        Bundle result = new Bundle();

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmpty()) {
                    String dateTimeString = selectedDateTimeTextView.getText().toString();

                    // Define the regular expression pattern to match "GMT 00:00"
                    Pattern pattern = Pattern.compile("\\bGMT\\+\\d{2}:\\d{2}\\b");

                    // Create a matcher for the input string
                    Matcher matcher = pattern.matcher(dateTimeString);

                    // Replace all occurrences of the pattern with an empty string
                    String trimmedDateTimeString = matcher.replaceAll("");

                    // Construct new data string
                    String newData = "Passenger\n" + "Name: " + input_name.getText().toString() + "\n"
                            + "Contact Number: " + input_contact.getText().toString() + "\n"
                            + "Destination: " + input_destination.getText().toString() + "\n"
                            + "Date & Time: " + trimmedDateTimeString + "\n"
                            + "Price: RM" + input_price.getText().toString();

                    // Fetch existing data from Firebase
                    DatabaseReference activityRef = mDatabase.child("activity");
                    activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isDuplicate = false;

                            // Iterate through existing data to check for duplicates
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String existingData = snapshot.getValue(String.class);
                                if (existingData.equals(newData)) {
                                    isDuplicate = true;
                                    break; // Exit loop if duplicate found
                                }
                            }

                            // Handle duplication check result
                            if (!isDuplicate) {
                                // No duplicate found, proceed to add new data
                                showNotification(0);
                                writeDataToFirebase(newData);
                                clearAll();
                            } else {
                                // Duplicate found, notify user
                                showNotification(1);
                                clearAll();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error or onCancelled event
                        }
                    });
                }
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverFragment driverFragment = new DriverFragment();
                replaceFragment(driverFragment);
            }
        });
        return view;
    }

    private void showDateTimePickerDialog() {
        // Get current date and time
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = 0;

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Show TimePickerDialog after selecting the date
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Display selected date and time in TextView
                                        calendar.set(java.util.Calendar.YEAR, year);
                                        calendar.set(java.util.Calendar.MONTH, monthOfYear);
                                        calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                                        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(java.util.Calendar.MINUTE, minute);
                                        calendar.set(java.util.Calendar.SECOND, second);

                                        String selectedDateTime = calendar.getTime().toString();
                                        selectedDateTimeTextView.setText(selectedDateTime);
                                    }
                                }, hour, minute, false);
                        timePickerDialog.show();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showNotification(int i) {
        if(i == 0) {
            notification.setText("Add new activity successful.");
            notification.setTextColor(Color.GREEN);
        }

        else{
            notification.setText("Duplicate activity entered.");
            notification.setTextColor(Color.RED);
        }

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1500); // Duration of fade out animation (1.5 second)
        alphaAnimation.setFillAfter(true);

        // Start the animation on the TextView
        notification.startAnimation(alphaAnimation);

    }

    private boolean checkEmpty(){
        int count = 0;
        float price;

        if(input_name.getText().toString().isEmpty()){
            input_name.setHint("Please fill in your name");
            input_name.setHintTextColor(Color.RED);
            count++;
        }
        if(input_contact.getText().toString().isEmpty()){
            input_contact.setHint("Please fill in your contact number");
            input_contact.setHintTextColor(Color.RED);
            count++;
        }
        if(!(input_contact.getText().toString().isEmpty()) && !(input_contact.getText().toString().matches("\\d{3}-\\d{7,8}"))){
            errorMessage.setText("Invalid format. Should be like (012-3456789/012-34567890)");
            errorMessage.setTextColor(Color.RED);
            count++;
        }
        if(input_destination.getText().toString().isEmpty()){
            input_destination.setHint("Please fill in your destination");
            input_destination.setHintTextColor(Color.RED);
            count++;
        }
        if(selectedDateTimeTextView.getText().toString().isEmpty()){
            selectedDateTimeTextView.setHint("Please fill in date & time");
            selectedDateTimeTextView.setHintTextColor(Color.RED);
            count++;
        }

        if(input_price.getText().toString().isEmpty()){
            input_price.setHint("Please fill in your price");
            input_price.setHintTextColor(Color.RED);
            count++;
        }
        else {
            price = parseFloat(input_price.getText().toString());
            if (price < 0) {
                priceMessage.setText("Please fill in your price properly");
                priceMessage.setTextColor(Color.RED);
                count++;
            }
            else if (!(input_price.getText().toString().matches("\\d+(\\.\\d{1,2})?"))) {
                priceMessage.setText("Please fill in your price properly");
                priceMessage.setTextColor(Color.RED);
                count++;
            }
        }

        if(count > 0 && count <= 5) {
            return false;
        }

        else {
            return true;
        }
    }

    private void clearAll(){
        input_price.setText("");
        input_price.setHint("Enter the price");
        input_price.setHintTextColor(Color.GRAY);

        input_destination.setText("");
        input_destination.setHint("Where you want to go");
        input_destination.setHintTextColor(Color.GRAY);

        input_contact.setText("");
        input_contact.setHint("Phone Number(012-3456789/012-34567890)");
        input_contact.setHintTextColor(Color.GRAY);

        errorMessage.setText("");
        priceMessage.setText("");

        input_name.setText("");
        input_name.setHint("Enter Name");
        input_name.setHintTextColor(Color.GRAY);

        selectedDateTimeTextView.setText("");
        selectedDateTimeTextView.setHint("Selected Date and Time: Not set");
        selectedDateTimeTextView.setHintTextColor(Color.GRAY);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(this); // Hide current fragment
        fragmentTransaction.add(R.id.frame_layout, fragment); // Show new fragment
        fragmentTransaction.addToBackStack(null); // Add transaction to back stack
        fragmentTransaction.commit();
    }

    private void writeDataToFirebase(String newData) {
        // Generate a unique key for the new data
        String key = mDatabase.child("activity").push().getKey();
        // Write the new data to the database
        mDatabase.child("activity").child(key).setValue(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved
                        showNotification(0); // or handle success as needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                        showNotification(1); // or handle failure as needed
                    }
                });
    }
}