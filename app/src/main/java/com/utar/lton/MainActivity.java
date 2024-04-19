package com.utar.lton;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.utar.lton.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set initial fragment
        HomeFragment homeFragment = new HomeFragment();
        add_Fragment(homeFragment);

        AddFragment addFragment = new AddFragment();
        add_Fragment(addFragment);

        ProfileFragment profileFragment = new ProfileFragment();
        add_Fragment(profileFragment);

        LocationFragment locationFragment = new LocationFragment();
        add_Fragment(locationFragment);

        showFragment(homeFragment);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.btm_home) {
                showFragment(homeFragment);
            } else if (itemId == R.id.btm_add) {
                showFragment(addFragment);
            } else if (itemId == R.id.btm_profile) {
                showFragment(profileFragment);
            } else if (itemId == R.id.btm_location) {
                showFragment(locationFragment);
            }
            return true;
        });

    }

    private void add_Fragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag != fragment) {
                fragmentTransaction.hide(frag);
            }
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }
}

