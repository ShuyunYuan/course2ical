package me.yuanshuyun.java.course2ical;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;

public class Course {

    private final String mTitle;
    private final ArrayList mTimeAndLocation;
    private final String mInstructorName;
    private final String mInstructorEmail;

    // Add fields about the course, including name, day of week, time, location, type, and some extra information.

    public Course(String title, ArrayList times, String email, String name) {
        mTitle = title;
        mTimeAndLocation = times;
        mInstructorEmail = email;
        mInstructorName = name;
    }

    public String getTitle() {
        return mTitle;
    }
    public String getInstructorName() {
        return  mInstructorName;
    }
    public String getInstructorEmail() {
        return mInstructorEmail;
    }
    public String getLectureLocation(){
        return mTimeAndLocation.get(3).toString();
    }
    public DayOfWeek getDayofWeek(){
        return mTimeAndLocation.get(2).;
    }

    public ArrayList getTimeAndLocation() {
        return mTimeAndLocation;
    }
    // TODO: Add more parsing code as static methods.

    public static LocalTime parseTime(String input) {
        // TODO: Implement this.
        return null;
    }


}
