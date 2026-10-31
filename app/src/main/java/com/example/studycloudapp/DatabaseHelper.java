package com.example.studycloudapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudyCloud.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_ROLE = "role";
    private static final String COL_COURSE = "course";
    private static final String COL_SECTION = "section";

    private static final String TABLE_COURSES = "courses";
    private static final String COL_COURSE_NAME = "course_name";

    private static final String TABLE_ASSIGNMENTS = "assignments";
    private static final String COL_ASSIGN_TITLE = "title";
    private static final String COL_ASSIGN_DEADLINE = "deadline";
    private static final String COL_ASSIGN_DESC = "description";

    private static final String TABLE_SUBMISSIONS = "submissions";
    private static final String COL_SUB_ID = "id";
    private static final String COL_SUB_STUDENT = "student_username";
    private static final String COL_SUB_ASSIGNMENT = "assignment_title";
    private static final String COL_SUB_NOTES = "notes";
    private static final String COL_SUB_TIMESTAMP = "timestamp";
    private static final String COL_SUB_STATUS = "status";

    private static final String TABLE_MATERIALS = "materials";
    private static final String COL_MAT_COURSE = "course";
    private static final String COL_MAT_TITLE = "title";
    private static final String COL_MAT_LINK = "link_or_desc";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT, " +
                COL_ROLE + " TEXT, " +
                COL_COURSE + " TEXT, " +
                COL_SECTION + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_COURSES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COURSE_NAME + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ASSIGN_TITLE + " TEXT, " +
                COL_ASSIGN_DEADLINE + " TEXT, " +
                COL_ASSIGN_DESC + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SUBMISSIONS + " (" +
                COL_SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUB_STUDENT + " TEXT, " +
                COL_SUB_ASSIGNMENT + " TEXT, " +
                COL_SUB_NOTES + " TEXT, " +
                COL_SUB_TIMESTAMP + " TEXT, " +
                COL_SUB_STATUS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_MATERIALS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MAT_COURSE + " TEXT, " +
                COL_MAT_TITLE + " TEXT, " +
                COL_MAT_LINK + " TEXT)");

        db.execSQL("INSERT INTO " + TABLE_COURSES + " (" + COL_COURSE_NAME + ") VALUES ('BSIT'), ('BSIS')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COL_USERNAME + ", " + COL_PASSWORD + ", " + COL_ROLE + ") " +
                "VALUES ('admin', 'admin123', 'Admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIALS);
        onCreate(db);
    }

    public boolean registerUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        cv.put(COL_ROLE, role);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ROLE + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        String role = "Student";
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USERNAME + ", " + COL_ROLE + " FROM " + TABLE_USERS, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) + " (" + cursor.getString(1) + ")");
        }
        cursor.close();
        return list;
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COL_USERNAME + " = ?", new String[]{username});
    }

    public boolean isStudentEnrolled(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_COURSE + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_COURSE + " IS NOT NULL", new String[]{username});
        boolean enrolled = cursor.getCount() > 0;
        cursor.close();
        return enrolled;
    }

    public String getUserCourse(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_COURSE + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        String course = "Not Enrolled";
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            course = cursor.getString(0);
        }
        cursor.close();
        return course;
    }

    public String getUserSection(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SECTION + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username});
        String section = "N/A";
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            section = cursor.getString(0);
        }
        cursor.close();
        return section;
    }

    public String enrollStudent(String username, String course) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COL_COURSE + " = ?", new String[]{course});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        int sectionIndex = count / 40;
        char letter = (char) ('A' + sectionIndex);
        String assignedSection = course + "-1" + letter;

        ContentValues cv = new ContentValues();
        cv.put(COL_COURSE, course);
        cv.put(COL_SECTION, assignedSection);
        db.update(TABLE_USERS, cv, COL_USERNAME + " = ?", new String[]{username});

        return assignedSection;
    }

    public void updateStudentSection(String username, String newSection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SECTION, newSection);
        db.update(TABLE_USERS, cv, COL_USERNAME + " = ?", new String[]{username});
    }

    public ArrayList<String> getAllEnrolledStudents() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USERNAME + ", " + COL_COURSE + ", " + COL_SECTION +
                " FROM " + TABLE_USERS + " WHERE " + COL_COURSE + " IS NOT NULL", null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) + " — [" + cursor.getString(1) + "] Section: " + cursor.getString(2));
        }
        cursor.close();
        return list;
    }

    public boolean addCourse(String courseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_COURSE_NAME, courseName.toUpperCase().trim());
        long result = db.insertWithOnConflict(TABLE_COURSES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public ArrayList<String> getAllCourses() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_COURSE_NAME + " FROM " + TABLE_COURSES, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    public void addAssignment(String title, String deadline, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ASSIGN_TITLE, title);
        cv.put(COL_ASSIGN_DEADLINE, deadline);
        cv.put(COL_ASSIGN_DESC, desc);
        db.insert(TABLE_ASSIGNMENTS, null, cv);
    }

    public ArrayList<String> getAllAssignments() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ASSIGN_TITLE + ", " + COL_ASSIGN_DEADLINE + ", " + COL_ASSIGN_DESC +
                " FROM " + TABLE_ASSIGNMENTS + " ORDER BY id DESC", null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) + ":::" + cursor.getString(1) + ":::" + cursor.getString(2));
        }
        cursor.close();
        return list;
    }

    public void addSubmission(String student, String assignment, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        String timestamp = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(new Date());

        ContentValues cv = new ContentValues();
        cv.put(COL_SUB_STUDENT, student);
        cv.put(COL_SUB_ASSIGNMENT, assignment);
        cv.put(COL_SUB_NOTES, notes);
        cv.put(COL_SUB_TIMESTAMP, timestamp);
        cv.put(COL_SUB_STATUS, "Pending");
        db.insert(TABLE_SUBMISSIONS, null, cv);
    }

    public ArrayList<String> getAllSubmissions() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SUB_ID + ", " + COL_SUB_STUDENT + ", " + COL_SUB_ASSIGNMENT + ", " +
                COL_SUB_NOTES + ", " + COL_SUB_TIMESTAMP + ", " + COL_SUB_STATUS + " FROM " + TABLE_SUBMISSIONS + " ORDER BY " + COL_SUB_ID + " DESC", null);
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0) + "###" + cursor.getString(1) + "###" + cursor.getString(2) + "###" +
                    cursor.getString(3) + "###" + cursor.getString(4) + "###" + cursor.getString(5));
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> getSubmissionsForStudent(String student) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SUB_ID + ", " + COL_SUB_STUDENT + ", " + COL_SUB_ASSIGNMENT + ", " +
                COL_SUB_NOTES + ", " + COL_SUB_TIMESTAMP + ", " + COL_SUB_STATUS +
                " FROM " + TABLE_SUBMISSIONS + " WHERE " + COL_SUB_STUDENT + " = ? ORDER BY " + COL_SUB_ID + " DESC", new String[]{student});
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0) + "###" + cursor.getString(1) + "###" + cursor.getString(2) + "###" +
                    cursor.getString(3) + "###" + cursor.getString(4) + "###" + cursor.getString(5));
        }
        cursor.close();
        return list;
    }

    public void markSubmissionChecked(int submissionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SUB_STATUS, "Activity Checked");
        db.update(TABLE_SUBMISSIONS, cv, COL_SUB_ID + " = ?", new String[]{String.valueOf(submissionId)});
    }

    public void addMaterial(String course, String title, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MAT_COURSE, course);
        cv.put(COL_MAT_TITLE, title);
        cv.put(COL_MAT_LINK, link);
        db.insert(TABLE_MATERIALS, null, cv);
    }

    public ArrayList<String> getMaterials(String course) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_MAT_TITLE + ", " + COL_MAT_LINK + " FROM " + TABLE_MATERIALS + " WHERE " + COL_MAT_COURSE + " = ?", new String[]{course});
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0) + ":::" + cursor.getString(1));
        }
        cursor.close();
        return list;
    }
}
