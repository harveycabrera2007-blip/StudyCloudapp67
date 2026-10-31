package com.example.studycloudapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        listView = findViewById(R.id.listViewUsers);

        DatabaseHelper db = new DatabaseHelper(this);
        userList = db.getAllUsers();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = userList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Manage Account")
                    .setMessage("Selected: " + selectedUser)
                    .setPositiveButton("Delete User", (dialog, which) -> {
                        String usernameOnly = selectedUser.split(" ")[0];
                        db.deleteUser(usernameOnly);
                        userList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, selectedUser + " was removed!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
