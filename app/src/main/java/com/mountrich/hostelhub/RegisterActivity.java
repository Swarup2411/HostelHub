package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password;
    AutoCompleteTextView roleSpinner;
    MaterialButton registerBtn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register Activity");
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerBtn = findViewById(R.id.registerBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Spinner Data
        String[] roles = {"Student", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                roles
        );
        roleSpinner.setText("Student", false);
        roleSpinner.setAdapter(adapter);

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String role = roleSpinner.getText().toString().trim();

        // ✅ 1. Name Validation
        if (userName.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }

        if (userName.length() < 3) {
            name.setError("Name must be at least 3 characters");
            name.requestFocus();
            return;
        }

        // ✅ 2. Email Validation
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

        // ✅ 3. Password Validation
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

        // ✅ 4. Role Validation
        if (role.isEmpty()) {
            Toast.makeText(this, "Please select role", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔒 Disable button to prevent multiple clicks
        registerBtn.setEnabled(false);

        // ✅ 5. Firebase Register
        auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener(authResult -> {

                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        registerBtn.setEnabled(true);
                        return;
                    }

                    String userId = auth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", userName);
                    user.put("email", userEmail);
                    user.put("role", role);

                    db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                registerBtn.setEnabled(true);
                                Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    registerBtn.setEnabled(true);
                    Toast.makeText(this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}