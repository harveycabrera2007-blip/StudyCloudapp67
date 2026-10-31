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

public class ReviewSubmissionsActivity extends BaseActivity {

    private ListView lvSubmissions;
    private DataManager dataManager;
    private DatabaseHelper db;
    private ArrayList<String> submissionsList;
    private SubmissionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submissions);

        dataManager = new DataManager(this);
        db = new DatabaseHelper(this);
        lvSubmissions = findViewById(R.id.lvSubmissions);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadSubmissions();
    }

    private void loadSubmissions() {
        submissionsList = dataManager.getAllSubmissions();
        if (submissionsList.isEmpty()) {
            Toast.makeText(this, "No submissions yet!", Toast.LENGTH_SHORT).show();
        }
        adapter = new SubmissionAdapter();
        lvSubmissions.setAdapter(adapter);
    }

    private class SubmissionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return submissionsList.size();
        }

        @Override
        public Object getItem(int position) {
            return submissionsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ReviewSubmissionsActivity.this)
                        .inflate(R.layout.item_submission, parent, false);
            }

            String[] parts = submissionsList.get(position).split("###");
            String id = parts[0];
            String student = parts[1];
            String assignment = parts[2];
            String notes = parts[3];
            String timestamp = parts[4];
            String status = parts.length > 5 ? parts[5] : "Pending";

            TextView tvStudent = convertView.findViewById(R.id.tvSubStudent);
            TextView tvAssignment = convertView.findViewById(R.id.tvSubAssignment);
            TextView tvNotes = convertView.findViewById(R.id.tvSubNotes);
            TextView tvTimestamp = convertView.findViewById(R.id.tvSubTimestamp);
            TextView tvStatus = convertView.findViewById(R.id.tvSubStatus);
            Button btnCheck = convertView.findViewById(R.id.btnCheckActivity);

            tvStudent.setText(student);
            tvAssignment.setText("Activity: " + assignment);
            tvNotes.setText("Notes: " + (notes.isEmpty() ? "No notes" : notes));
            tvTimestamp.setText("🕒 Sent: " + timestamp);
            tvStatus.setText(status);

            if ("Activity Checked".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundColor(Color.parseColor("#55EFC4"));
                tvStatus.setTextColor(Color.parseColor("#00B894"));
                btnCheck.setText("Activity Checked ✓");
                btnCheck.setEnabled(false);
                btnCheck.setBackgroundColor(Color.parseColor("#B2BEC3"));
            } else {
                tvStatus.setBackgroundColor(Color.parseColor("#FFEAA7"));
                tvStatus.setTextColor(Color.parseColor("#D63031"));
                btnCheck.setText("Mark as Checked");
                btnCheck.setEnabled(true);
                btnCheck.setBackgroundColor(Color.parseColor("#00B894"));

                btnCheck.setOnClickListener(v -> {
                    dataManager.markSubmissionChecked(id);
                    int submissionId = Integer.parseInt(id);
                    db.markSubmissionChecked(submissionId);
                    Toast.makeText(ReviewSubmissionsActivity.this, "Marked as Checked!", Toast.LENGTH_SHORT).show();
                    loadSubmissions();
                });
            }

            return convertView;
        }
    }
}
