package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class StudentDashboardActivity extends AppCompatActivity {

    TextView welcomeText, emailText;
    Button profileBtn, complaintBtn, gatepassBtn, logoutBtn;

    CardView jijauCard, savitriCard, matoshriCard, messCard;

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

        // CardViews
        jijauCard = findViewById(R.id.jijauCard);
        savitriCard = findViewById(R.id.savitriCard);
        matoshriCard = findViewById(R.id.matoshriCard);
        messCard = findViewById(R.id.messCard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        // 🔓 Logout
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });

        // 🔘 Buttons Navigation
        profileBtn.setOnClickListener(v ->
                Toast.makeText(this, "Profile Coming Soon", Toast.LENGTH_SHORT).show());

        complaintBtn.setOnClickListener(v ->
                Toast.makeText(this, "Complaint Module Next", Toast.LENGTH_SHORT).show());

        gatepassBtn.setOnClickListener(v ->
                Toast.makeText(this, "Gatepass Module Next", Toast.LENGTH_SHORT).show());

        // 🧱 Card Clicks
        jijauCard.setOnClickListener(v ->
                Toast.makeText(this, "Jijau Hostel", Toast.LENGTH_SHORT).show());

        savitriCard.setOnClickListener(v ->
                Toast.makeText(this, "Savitri Hostel", Toast.LENGTH_SHORT).show());

        matoshriCard.setOnClickListener(v ->
                Toast.makeText(this, "Matoshri Hostel", Toast.LENGTH_SHORT).show());

        messCard.setOnClickListener(v ->
                Toast.makeText(this, "Mess Details", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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