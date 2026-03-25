package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    MaterialButton loginBtn;
    TextView registerText;
    ProgressBar progressbarLogin;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");

        email = findViewById(R.id.emailText);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerText = findViewById(R.id.registerText);
        progressbarLogin = findViewById(R.id.progressbarLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {

        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        // 🔍 VALIDATIONS
        if (userEmail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter valid email");
            email.requestFocus();
            return;
        }

        if (userPass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (userPass.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        // 🔄 SHOW LOADING
        progressbarLogin.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        // 🔥 Firebase Login
        auth.signInWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener(authResult -> {

                    if (auth.getCurrentUser() == null) {
                        showError("Something went wrong");
                        return;
                    }

                    String userId = auth.getCurrentUser().getUid();

                    db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                progressbarLogin.setVisibility(View.GONE);
                                loginBtn.setEnabled(true);

                                if (documentSnapshot.exists()) {

                                    String role = documentSnapshot.getString("role");

                                    if ("Admin".equals(role)) {
                                        startActivity(new Intent(this, AdminDashboardActivity.class));
                                    } else {
                                        startActivity(new Intent(this, MainActivity.class));
                                    }

                                    finish();

                                } else {
                                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                showError("Failed to fetch user data");
                            });

                })
                .addOnFailureListener(e -> {
                    showError("Login Failed: " + e.getMessage());
                });
    }

    private void showError(String message) {
        progressbarLogin.setVisibility(View.GONE);
        loginBtn.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}