package com.example.studycloudapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentHistoryActivity extends BaseActivity {

    private ListView lvHistory;
    private DataManager dataManager;
    private ArrayList<String> mySubmissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        lvHistory = findViewById(R.id.lvStudentHistory);
        dataManager = new DataManager(this);

        String currentStudent = getIntent().getStringExtra("USERNAME");
        if (currentStudent == null) {
            currentStudent = getSharedPreferences("StudyCloudPrefs", MODE_PRIVATE).getString("LAST_LOGGED_IN_USER", "Student");
        }

        mySubmissions = dataManager.getSubmissionsForStudent(currentStudent);

        if (mySubmissions.isEmpty()) {
            Toast.makeText(this, "You haven't submitted any assignments yet!", Toast.LENGTH_SHORT).show();
        }

        lvHistory.setAdapter(new HistoryAdapter());
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mySubmissions.size();
        }

        @Override
        public Object getItem(int position) {
            return mySubmissions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(StudentHistoryActivity.this)
                        .inflate(R.layout.item_submission, parent, false);
            }

            String[] parts = mySubmissions.get(position).split("###");
            String assignment = parts[2];
            String notes = parts[3];
            String timestamp = parts[4];
            String status = parts.length > 5 ? parts[5] : "Pending";

            TextView tvStudent = convertView.findViewById(R.id.tvSubStudent);
            TextView tvAssignment = convertView.findViewById(R.id.tvSubAssignment);
            TextView tvNotes = convertView.findViewById(R.id.tvSubNotes);
            TextView tvTimestamp = convertView.findViewById(R.id.tvSubTimestamp);
            TextView tvStatus = convertView.findViewById(R.id.tvSubStatus);
            Button btnAction = convertView.findViewById(R.id.btnCheckActivity);

            tvStudent.setText(assignment);
            tvAssignment.setText("Notes: " + (notes.isEmpty() ? "None" : notes));
            tvNotes.setVisibility(View.GONE);
            tvTimestamp.setText("🕒 Submitted: " + timestamp);
            tvStatus.setText(status);

            btnAction.setVisibility(View.GONE);

            if ("Activity Checked".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundColor(Color.parseColor("#55EFC4"));
                tvStatus.setTextColor(Color.parseColor("#00B894"));
            } else {
                tvStatus.setBackgroundColor(Color.parseColor("#FFEAA7"));
                tvStatus.setTextColor(Color.parseColor("#D63031"));
            }

            return convertView;
        }
    }
}
