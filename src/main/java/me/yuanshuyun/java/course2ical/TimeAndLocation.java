package me.yuanshuyun.java.course2ical;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

public class TimeAndLocation {

    private final String mType;
    private final LocalTime mStartTime;
    private final LocalTime mEndTime;
    private final DayOfWeek mDayOfWeek;
    private final String mLocation;

    public TimeAndLocation(String type, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, String location) {
        mType = type;
        mStartTime = startTime;
        mEndTime = endTime;
        mDayOfWeek = dayOfWeek;
        mLocation = location;
    }

    public String getType() {
        return mType;
    }

    public LocalTime getStartTime() {
        return mStartTime;
    }

    public LocalTime getEndTime() {
        return mEndTime;
    }

    public DayOfWeek getDayOfWeek() {
        return mDayOfWeek;
    }

    public String getLocation() {
        return mLocation;
    }
}
