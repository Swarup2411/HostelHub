package com.mountrich.hostelhub.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mountrich.hostelhub.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView profileImage;
    ImageView uploadBtn;
    MaterialButton updateBtn;

    TextInputEditText name, phone, hostel;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        profileImage = view.findViewById(R.id.profileImage);
        uploadBtn = view.findViewById(R.id.uploadBtn);
        updateBtn = view.findViewById(R.id.updateBtn);

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        hostel = view.findViewById(R.id.hostel);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        loadProfile();

        uploadBtn.setOnClickListener(v -> openGallery());

        updateBtn.setOnClickListener(v -> updateProfile());
    }

    // 📸 Open Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    // 📥 Get Image Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    // 🔄 Update Profile
    private void updateProfile() {

        if (auth.getCurrentUser() == null) {
            if (isAdded()) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        String userName = name.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userHostel = hostel.getText().toString().trim();

        // 🔍 Validation
        if (TextUtils.isEmpty(userName)) {
            name.setError("Enter name");
            return;
        }

        if (userPhone.length() != 10) {
            phone.setError("Enter valid phone");
            return;
        }

        if (TextUtils.isEmpty(userHostel)) {
            hostel.setError("Enter hostel");
            return;
        }

        // 🔥 If image selected → upload
        if (imageUri != null) {

            StorageReference ref = storage.getReference()
                    .child("profileImages/" + userId);

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {

                                saveToFirestore(userId, userName, userPhone, userHostel, uri.toString());

                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());

        } else {
            saveToFirestore(userId, userName, userPhone, userHostel, null);
        }
    }

    // 💾 Save Data
    private void saveToFirestore(String userId, String name, String phone, String hostel, String imageUrl) {

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("hostel", hostel);

        if (imageUrl != null) {
            user.put("image", imageUrl);
        }

        db.collection("users").document(userId)
                .update(user)
                .addOnSuccessListener(unused -> {
                    if (isAdded()) { // ✅ SAFE CHECK
                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) { // ✅ SAFE CHECK
                        Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 📥 Load Profile
    private void loadProfile() {

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        name.setText(doc.getString("name"));
                        phone.setText(doc.getString("phone"));
                        hostel.setText(doc.getString("hostel"));

                        String image = doc.getString("image");

                        if (image != null) {
                            Glide.with(this)
                                    .load(image)
                                    .placeholder(R.drawable.baseline_person_24)
                                    .into(profileImage);
                        }
                    }
                });
    }
}