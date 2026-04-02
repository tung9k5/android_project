package com.example.studywithai.Activities.Categories;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studywithai.Activities.MenuActivity;
import com.example.studywithai.R;
import com.example.studywithai.Repositories.CategoryRepository;

public class AddActivity extends AppCompatActivity {
    EditText edtName, edtDes;
    Button btnSave, btnBack;
    CategoryRepository categoryRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        categoryRepository = new CategoryRepository(AddActivity.this);
        edtName = findViewById(R.id.edtCategoryName);
        edtDes  = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSaveCategory);
        btnBack = findViewById(R.id.btnBackCategory);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, MenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MENU_TAB", "CATEGORY_TAB");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    edtName.setError("Name's Category is required");
                    return;
                }
                String description = edtDes.getText().toString().trim();
                long insert = categoryRepository.saveNewCategory(name, description);
                if (insert == -1){
                    // Error
                    Toast.makeText(AddActivity.this, "Create Category failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Success
                Toast.makeText(AddActivity.this, "Create Category success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddActivity.this, MenuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("MENU_TAB", "CATEGORY_TAB");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
