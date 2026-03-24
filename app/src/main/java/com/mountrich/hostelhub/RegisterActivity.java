package com.mountrich.hostelhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, phone, hostel;
    AutoCompleteTextView roleSpinner;
    MaterialButton registerBtn;
    ProgressBar progressbarRegister;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");

        name = findViewById(R.id.name);
        email = findViewById(R.id.emailText);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        hostel = findViewById(R.id.hostel);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerBtn = findViewById(R.id.registerBtn);
        progressbarRegister = findViewById(R.id.progressbarRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Role Dropdown
        String[] roles = {"Student", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                roles
        );
        roleSpinner.setAdapter(adapter);
        roleSpinner.setText("Student", false);

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String role = roleSpinner.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userHostel = hostel.getText().toString().trim();

        // 🔍 VALIDATIONS

        if (userName.isEmpty() || userName.length() < 3) {
            name.setError("Enter valid name");
            name.requestFocus();
            return;
        }

        if (userEmail.isEmpty() ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter valid email");
            email.requestFocus();
            return;
        }

        if (userPass.isEmpty() || userPass.length() < 6) {
            password.setError("Password must be 6+ chars");
            password.requestFocus();
            return;
        }

        if (userPhone.length() != 10) {
            phone.setError("Enter valid 10 digit phone");
            phone.requestFocus();
            return;
        }

        if (userHostel.isEmpty()) {
            hostel.setError("Enter hostel name");
            hostel.requestFocus();
            return;
        }

        if (role.isEmpty()) {
            Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔄 Show loading
        progressbarRegister.setVisibility(View.VISIBLE);
        registerBtn.setEnabled(false);

        // 🔥 Firebase Auth
        auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener(authResult -> {

                    if (auth.getCurrentUser() == null) {
                        showError("Something went wrong");
                        return;
                    }

                    String userId = auth.getCurrentUser().getUid();

                    // 🔥 Firestore Data
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", userName);
                    user.put("email", userEmail);
                    user.put("role", role);
                    user.put("phone", userPhone);
                    user.put("hostel", userHostel);

                    db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                progressbarRegister.setVisibility(View.GONE);
                                registerBtn.setEnabled(true);

                                clearAllFields();

                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                showError("Database Error: " + e.getMessage());
                            });

                })
                .addOnFailureListener(e -> {
                    showError("Auth Error: " + e.getMessage());
                });
    }

    private void showError(String message) {
        progressbarRegister.setVisibility(View.GONE);
        registerBtn.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearAllFields() {
        name.setText("");
        email.setText("");
        password.setText("");
        phone.setText("");
        hostel.setText("");
    }
}