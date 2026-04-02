package com.example.studywithai.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studywithai.MainActivity;
import com.example.studywithai.R;
import com.example.studywithai.Repositories.UserRepository;

public class SignUpActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnSignup;
    TextView tvLogin;
    UserRepository userRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userRepository = new UserRepository(SignUpActivity.this);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPhone    = findViewById(R.id.edtPhoneNumber);
        btnSignup   = findViewById(R.id.btnSignup);
        tvLogin     = findViewById(R.id.tvLogin);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password is required");
                    return;
                }
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email is required");
                    return;
                }
                String phone = edtPhone.getText().toString().trim();
                // kiem tra tai khoan da co trong CSDL chua?
                boolean checkingUsername = userRepository.checkExistsUsername(username);
                if (checkingUsername){
                    Toast.makeText(SignUpActivity.this, "Username is exists", Toast.LENGTH_SHORT).show();
                    edtUsername.setError("Username is exists");
                    return;
                }

                long insert = userRepository.saveUserAccount(username, password, email, phone);
                if (insert == -1){
                    Toast.makeText(SignUpActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignUpActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intentLogin);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
