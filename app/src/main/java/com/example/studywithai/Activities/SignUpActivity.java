package com.example.studywithai.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    Spinner spinnerGradeLevel; // Khai báo thêm Spinner
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
        spinnerGradeLevel = findViewById(R.id.spinnerGradeLevel); // Ánh xạ Spinner
        btnSignup   = findViewById(R.id.btnSignup);
        tvLogin     = findViewById(R.id.tvLogin);

        // Nạp dữ liệu vào Spinner
        String[] grades = {"Học sinh Cấp 2 (Lớp 6 - 9)", "Học sinh Cấp 3 (Lớp 10 - 12)", "Sinh viên Đại học"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, grades);
        spinnerGradeLevel.setAdapter(adapter);

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

                // Lấy chỉ mục đã chọn từ Spinner (0, 1, 2) và chuyển thành gradeLevel (1, 2, 3)
                int selectedGradeIndex = spinnerGradeLevel.getSelectedItemPosition();
                int gradeLevel = selectedGradeIndex + 1; // 1: Cấp 2, 2: Cấp 3, 3: Đại học

                // Kiem tra tai khoan da co trong CSDL chua?
                boolean checkingUsername = userRepository.checkExistsUsername(username);
                if (checkingUsername){
                    Toast.makeText(SignUpActivity.this, "Username is exists", Toast.LENGTH_SHORT).show();
                    edtUsername.setError("Username is exists");
                    return;
                }

                // CẬP NHẬT: Truyền thêm gradeLevel vào hàm saveUserAccount
                long insert = userRepository.saveUserAccount(username, password, email, phone, gradeLevel);

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