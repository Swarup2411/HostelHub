package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView registerText;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerText = findViewById(R.id.registerText);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {

        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        // ✅ 1. Empty Validation
        if (userEmail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (userPass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        // ✅ 2. Email Format Validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter valid email");
            email.requestFocus();
            return;
        }

        // ✅ 3. Password Length Check
        if (userPass.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        // ✅ 4. Firebase Login
        auth.signInWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener(authResult -> {

                    String userId = auth.getCurrentUser().getUid();

                    db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                if (documentSnapshot.exists()) {

                                    String role = documentSnapshot.getString("role");

                                    if (role != null && role.equals("Admin")) {
                                        startActivity(new Intent(this, AdminDashboardActivity.class));
                                    } else {
                                        startActivity(new Intent(this, StudentDashboardActivity.class));
                                    }

                                    finish();

                                } else {
                                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}