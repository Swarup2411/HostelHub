package com.mountrich.hostelhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mountrich.hostelhub.LoginActivity;
import com.mountrich.hostelhub.R;



public class HomeFragment extends Fragment {

    TextView welcomeText, emailText;
    Button profileBtn, complaintBtn, gatepassBtn, logoutBtn;

    CardView jijauCard, savitriCard, matoshriCard, messCard;

    FirebaseAuth auth;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welcomeText = view.findViewById(R.id.welcomeText);
        emailText = view.findViewById(R.id.emailText);

        profileBtn = view.findViewById(R.id.profileBtn);
        complaintBtn = view.findViewById(R.id.complaintBtn);
        gatepassBtn = view.findViewById(R.id.gatepassBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        // CardViews
        jijauCard = view.findViewById(R.id.jijauCard);
        savitriCard = view. findViewById(R.id.savitriCard);
        matoshriCard = view.findViewById(R.id.matoshriCard);
        messCard = view.findViewById(R.id.messCard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        // 🔓 Logout
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // 🔘 Buttons Navigation
        profileBtn.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Profile Coming Soon", Toast.LENGTH_SHORT).show());

        complaintBtn.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Complaint Module Next", Toast.LENGTH_SHORT).show());

        gatepassBtn.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Gatepass Module Next", Toast.LENGTH_SHORT).show());

        // 🧱 Card Clicks
        jijauCard.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Jijau Hostel", Toast.LENGTH_SHORT).show());

        savitriCard.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Savitri Hostel", Toast.LENGTH_SHORT).show());

        matoshriCard.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Matoshri Hostel", Toast.LENGTH_SHORT).show());

        messCard.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Mess Details", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
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
                        Toast.makeText(getActivity(), "Failed to load data", Toast.LENGTH_SHORT).show());
    }
    }
