package com.example.studycloudapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;    

public class UploadMaterialActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);

        DataManager dataManager = new DataManager(this);
        Spinner spinnerCourse = findViewById(R.id.spinnerMaterialCourse);
        EditText etTitle = findViewById(R.id.etMaterialTitle);
        EditText etLink = findViewById(R.id.etMaterialLink);
        Button btnUpload = findViewById(R.id.btnUploadMaterial);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dataManager.getCourses());
        spinnerCourse.setAdapter(adapter);

        btnUpload.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String link = etLink.getText().toString().trim();

            if (spinnerCourse.getSelectedItem() == null || title.isEmpty()) {
                Toast.makeText(this, "Please select a course and provide a title", Toast.LENGTH_SHORT).show();
                return;
            }

            String course = spinnerCourse.getSelectedItem().toString();
            dataManager.addMaterial(course, title, link);
            Toast.makeText(this, "Material published to " + course + " students!", Toast.LENGTH_SHORT).show();
            DatabaseHelper db = new DatabaseHelper(this);
            db.addMaterial(course, title, link);
            finish();
        });
    }
}
