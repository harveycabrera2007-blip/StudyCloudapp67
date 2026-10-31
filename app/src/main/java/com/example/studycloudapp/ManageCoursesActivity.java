package com.example.studycloudapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;

public class ManageCoursesActivity extends BaseActivity {

    private EditText etNewCourse;
    private Button btnAddCourse;
    private ListView lvEnrolledStudents;
    private DataManager dataManager;
    private ArrayList<String> studentDisplayList;
    private ArrayList<String> rawStudentUsernames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);

        dataManager = new DataManager(this);
        etNewCourse = findViewById(R.id.etNewCourse);
        btnAddCourse = findViewById(R.id.btnAddCourse);
        lvEnrolledStudents = findViewById(R.id.lvEnrolledStudents);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnAddCourse.setOnClickListener(v -> {
            String newCourse = etNewCourse.getText().toString().trim();
            DatabaseHelper db = new DatabaseHelper(this);
            db.addCourse(newCourse);
            if (!newCourse.isEmpty()) {
                dataManager.addCourse(newCourse);
                Toast.makeText(this, "Course " + newCourse.toUpperCase() + " added! Now visible to students.", Toast.LENGTH_SHORT).show();
                etNewCourse.setText("");
            }
        });

        loadStudentList();

        lvEnrolledStudents.setOnItemClickListener((parent, view, position, id) -> {
            String studentUser = rawStudentUsernames.get(position);
            String currentSection = dataManager.getStudentSection(studentUser);

            EditText inputSection = new EditText(this);
            inputSection.setHint("e.g. BSIT-1B");
            inputSection.setText(currentSection);

            new AlertDialog.Builder(this)
                    .setTitle("Change Section for " + studentUser)
                    .setMessage("Enter the new section code:")
                    .setView(inputSection)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String newSec = inputSection.getText().toString().trim();
                        if (!newSec.isEmpty()) {
                            dataManager.updateStudentSection(studentUser, newSec);
                            Toast.makeText(this, "Section updated!", Toast.LENGTH_SHORT).show();
                            loadStudentList();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadStudentList() {
        rawStudentUsernames = new ArrayList<>(dataManager.getAllEnrolledStudents());
        studentDisplayList = new ArrayList<>();
        for (String user : rawStudentUsernames) {
            String course = dataManager.getStudentCourse(user);
            String section = dataManager.getStudentSection(user);
            studentDisplayList.add(user + " — [" + course + "] Section: " + section);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentDisplayList);
        lvEnrolledStudents.setAdapter(adapter);
    }
}
