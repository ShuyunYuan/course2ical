package me.yuanshuyun.java.course2ical;

import java.util.List;

public class Course {

    private final String mTitle;
    private final List<TimeAndLocation> mTimeAndLocation;
    private final String mInstructorName;
    private final String mInstructorEmail;

    // Add fields about the course, including title, instructor and some extra information.

    public Course(String title, List<TimeAndLocation> times, String email, String name) {
        mTitle = title;
        mTimeAndLocation = times;
        mInstructorEmail = email;
        mInstructorName = name;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<TimeAndLocation> getTimeAndLocation() {
        return mTimeAndLocation;
    }

    public String getInstructorName() {
        return mInstructorName;
    }

    public String getInstructorEmail() {
        return mInstructorEmail;
    }
}
