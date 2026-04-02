package com.example.studywithai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studywithai.Activities.MenuActivity;
import com.example.studywithai.Activities.SignUpActivity;
import com.example.studywithai.Models.UserModel;
import com.example.studywithai.Repositories.UserRepository;



public class MainActivity extends AppCompatActivity {

//hello
    UserRepository userRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_relative_layout);
        userRepository = new UserRepository(MainActivity.this);
        // find element in layout by Id
        EditText edtUsername = findViewById(R.id.edtUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnCancel = findViewById(R.id.btnCancel);
        TextView tvRegister = findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignup = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intentSignup);
            }
        });

        // event for element
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password is required");
                    return;
                }
                UserModel user = userRepository.loginUser(username, password);
                assert user != null;

                if (user.getId() > 0 && !TextUtils.isEmpty(user.getUsername())){
                    // UPDATE: Lưu thêm thông tin cấp học và Gamification vào Session
                    SharedPreferences sharedPf = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPf.edit();
                    editor.putInt("ID_USER", user.getId());
                    editor.putString("USERNAME_USER", user.getUsername());
                    editor.putString("EMAIL_USER", user.getEmail());
                    editor.putInt("ROLE_USER", user.getRole());

                    editor.putInt("GRADE_LEVEL", user.getGradeLevel());
                    editor.putInt("XP_USER", user.getXp());
                    editor.putInt("LEVEL_USER", user.getLevel());
                    editor.putInt("ENERGY_USER", user.getEnergy());
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    // Có thể bỏ Bundle này vì đã lưu đủ trong SharedPreferences
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}