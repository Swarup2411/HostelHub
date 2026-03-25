package com.mountrich.hostelhub;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mountrich.hostelhub.fragments.ComplaintFragment;
import com.mountrich.hostelhub.fragments.GatepassFragment;
import com.mountrich.hostelhub.fragments.HomeFragment;
import com.mountrich.hostelhub.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Default Fragment
        loadFragment(new HomeFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if( itemId == R.id.home){
                loadFragment(new HomeFragment());
                return true;
            }

            if( itemId == R.id.profile){
                loadFragment(new ProfileFragment());
                return true;
            }

            if( itemId == R.id.complaint){
                loadFragment(new ComplaintFragment());
                return true;
            }

            if( itemId == R.id.gatepass){
                loadFragment(new GatepassFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}