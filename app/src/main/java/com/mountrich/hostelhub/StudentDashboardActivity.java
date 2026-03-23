package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Objects;

public class StudentDashboardActivity extends AppCompatActivity {

    TextView welcomeText, emailText;
    TextView profileBtn, complaintBtn, gatepassBtn, logoutBtn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Dashboard");
        welcomeText = findViewById(R.id.welcomeText);
        emailText = findViewById(R.id.emailText);

        profileBtn = findViewById(R.id.profileBtn);
        complaintBtn = findViewById(R.id.complaintBtn);
        gatepassBtn = findViewById(R.id.gatepassBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Navigation (we will build these screens later)
        profileBtn.setOnClickListener(v ->
                Toast.makeText(this, "Profile Coming Soon", Toast.LENGTH_SHORT).show());

        complaintBtn.setOnClickListener(v ->
                Toast.makeText(this, "Complaint Module Next", Toast.LENGTH_SHORT).show());

        gatepassBtn.setOnClickListener(v ->
                Toast.makeText(this, "Gatepass Module Next", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        welcomeText.setText("Welcome, " + name);
                        emailText.setText(email);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}