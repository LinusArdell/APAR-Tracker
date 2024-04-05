package com.test.input;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.input.Activity.LoginActivity;
import com.test.input.Activity.MainActivity;

public class ProfileActivity extends AppCompatActivity {

    Button btnLogout;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());

        emailTextView = findViewById(R.id.email_value);

        // Mendapatkan user saat ini
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Mendapatkan email user dan menampilkannya pada TextView
            String email = currentUser.getEmail();
            emailTextView.setText(email);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_home:
                    // Buka activity MainActivity
                    Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                case R.id.menu_profile:
                    // Buka activity ProfileActivity
                    return true;
                default:
                    return false;
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        // Redirect ke halaman login atau activity lain yang sesuai
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
