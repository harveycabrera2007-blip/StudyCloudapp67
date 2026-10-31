package com.example.studycloudapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataManager {

    private static final String PREF_NAME = "StudyCloudData";
    private final SharedPreferences prefs;

    public DataManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initDefaultCourses();
    }

    public void saveSubmission(String student, String assignment, String notes) {
        Set<String> set = new HashSet<>(prefs.getStringSet("SUBMISSIONS_LIST", new HashSet<>()));

        String timestamp = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(new Date());
        String submissionId = String.valueOf(System.currentTimeMillis());
        String status = "Pending";

        String record = submissionId + "###" + student + "###" + assignment + "###" + notes + "###" + timestamp + "###" + status;
        set.add(record);

        prefs.edit().putStringSet("SUBMISSIONS_LIST", set).apply();
    }

    public ArrayList<String> getAllSubmissions() {
        Set<String> set = prefs.getStringSet("SUBMISSIONS_LIST", new HashSet<>());
        return new ArrayList<>(set);
    }

    public void markSubmissionChecked(String submissionId) {
        Set<String> set = new HashSet<>(prefs.getStringSet("SUBMISSIONS_LIST", new HashSet<>()));
        Set<String> updatedSet = new HashSet<>();

        for (String record : set) {
            String[] parts = record.split("###");
            if (parts[0].equals(submissionId)) {
                String updated = parts[0] + "###" + parts[1] + "###" + parts[2] + "###" + parts[3] + "###" + parts[4] + "###Activity Checked";
                updatedSet.add(updated);
            } else {
                updatedSet.add(record);
            }
        }
        prefs.edit().putStringSet("SUBMISSIONS_LIST", updatedSet).apply();
    }

    public void publishAssignment(String title, String deadline, String desc) {
        Set<String> set = new HashSet<>(prefs.getStringSet("ASSIGNMENTS_LIST", new HashSet<>()));
        set.add(title + ":::" + deadline + ":::" + desc);

        int unread = prefs.getInt("UNREAD_ASSIGNMENT_NOTIF", 0);
        prefs.edit()
                .putStringSet("ASSIGNMENTS_LIST", set)
                .putInt("UNREAD_ASSIGNMENT_NOTIF", unread + 1)
                .apply();
    }

    public int getUnreadNotificationCount() {
        return prefs.getInt("UNREAD_ASSIGNMENT_NOTIF", 0);
    }

    public void clearNotifications() {
        prefs.edit().putInt("UNREAD_ASSIGNMENT_NOTIF", 0).apply();
    }

    public ArrayList<String> getAllPublishedAssignments() {
        Set<String> set = prefs.getStringSet("ASSIGNMENTS_LIST", new HashSet<>());
        return new ArrayList<>(set);
    }

    public ArrayList<String> getSubmissionsForStudent(String targetStudent) {
        Set<String> set = prefs.getStringSet("SUBMISSIONS_LIST", new HashSet<>());
        ArrayList<String> studentRecords = new ArrayList<>();

        for (String record : set) {
            String[] parts = record.split("###");
            if (parts.length > 1 && parts[1].equalsIgnoreCase(targetStudent)) {
                studentRecords.add(record);
            }
        }
        return studentRecords;
    }

    private void initDefaultCourses() {
        if (!prefs.contains("COURSE_LIST")) {
            Set<String> defaults = new HashSet<>(Arrays.asList("BSIT", "BSIS"));
            prefs.edit().putStringSet("COURSE_LIST", defaults).apply();
        }
    }

    public ArrayList<String> getCourses() {
        Set<String> set = prefs.getStringSet("COURSE_LIST", new HashSet<>());
        return new ArrayList<>(set);
    }

    public void addCourse(String courseName) {
        Set<String> set = new HashSet<>(prefs.getStringSet("COURSE_LIST", new HashSet<>()));
        set.add(courseName.toUpperCase().trim());
        prefs.edit().putStringSet("COURSE_LIST", set).apply();
    }

    public boolean isStudentEnrolled(String username) {
        return prefs.getString(username + "_ENROLLED_COURSE", null) != null;
    }

    public String getStudentCourse(String username) {
        return prefs.getString(username + "_ENROLLED_COURSE", "Not Enrolled");
    }

    public String getStudentSection(String username) {
        return prefs.getString(username + "_ENROLLED_SECTION", "N/A");
    }

    public synchronized String enrollStudent(String username, String course) {
        int currentCount = prefs.getInt("COUNT_" + course, 0);

        int sectionIndex = currentCount / 40;
        char sectionLetter = (char) ('A' + sectionIndex);
        String assignedSection = course + "-1" + sectionLetter;

        prefs.edit()
                .putString(username + "_ENROLLED_COURSE", course)
                .putString(username + "_ENROLLED_SECTION", assignedSection)
                .putInt("COUNT_" + course, currentCount + 1)
                .apply();

        Set<String> enrolled = new HashSet<>(prefs.getStringSet("ALL_ENROLLED_STUDENTS", new HashSet<>()));
        enrolled.add(username);
        prefs.edit().putStringSet("ALL_ENROLLED_STUDENTS", enrolled).apply();

        return assignedSection;
    }

    public void updateStudentSection(String username, String newSection) {
        prefs.edit().putString(username + "_ENROLLED_SECTION", newSection).apply();
    }

    public Set<String> getAllEnrolledStudents() {
        return prefs.getStringSet("ALL_ENROLLED_STUDENTS", new HashSet<>());
    }

    public void addMaterial(String course, String title, String linkOrDesc) {
        Set<String> materials = new HashSet<>(prefs.getStringSet("MATERIALS_" + course, new HashSet<>()));
        materials.add(title + ":::" + linkOrDesc);
        prefs.edit().putStringSet("MATERIALS_" + course, materials).apply();
    }

    public ArrayList<String> getMaterials(String course) {
        Set<String> set = prefs.getStringSet("MATERIALS_" + course, new HashSet<>());
        return new ArrayList<>(set);
    }
}
