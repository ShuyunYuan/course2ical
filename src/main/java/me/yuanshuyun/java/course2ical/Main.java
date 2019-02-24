package me.yuanshuyun.java.course2ical;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Date;

public class Main {

    private static final LocalDate START_DATE = LocalDate.of(2019, Month.MARCH, 28);

    private static final LocalDate END_DATE = LocalDate.of(2019, Month.JUNE, 6);

    public static void main(String[] args) throws IOException {

        // 1. Read file name from command line.
        if (args.length != 1) {
            usage();
            System.exit(1);
            return;
        }
        File htmlFile = new File(args[0]);

        // 2. Read file content and feed it to jsoup.
        Document document = Jsoup.parse(htmlFile, StandardCharsets.UTF_8.name());

        // 3. Traverse the parsed DOM from jsoup and generate Course objects
        List<Course> coursesList = parseCourses(document);

        // 4. Iterate course objects and feed them to ical4j
        Calendar courseCalendar = generateCalendar(coursesList);

        // 5. Write ical4j ouput to file.
        outputCalendar(courseCalendar);
    }

    private static void usage() {
        System.err.println("Usage: course2ical <HTML_FILE>");
    }

    private static List<Course> parseCourses(Document document) {

        Elements courseItemElements = document.getElementsByClass("CourseItem");
        List<Course> coursesList = new ArrayList<>();
        for (Element courseItemElement : courseItemElements) {

            Element titleElement = courseItemElement.getElementsByClass("ClassTitle").first();
            String title = titleElement != null ? titleElement.text() : null;

            Element instructorElement = courseItemElement.selectFirst(
                    ".classDescription > div:nth-child(2) > a");
            String instructorName = instructorElement != null ? instructorElement.text() : null;
            String instructorEmail = instructorElement != null ? instructorElement.attr("href") : null;

            Elements courseTimeAndLocationElements = courseItemElement.select(".meeting-times > .meeting");
            List<TimeAndLocation> courseTimesAndLocationList = new ArrayList<>();
            for (Element courseTimeAndLocationElement : courseTimeAndLocationElements) {
                String type = courseTimeAndLocationElement.child(0).text();
                String localTimeString = courseTimeAndLocationElement.child(1).text();
                // split the start and end time
                String[] timeString = localTimeString.split(" - ");
                String courseStartTimeString = timeString[0];
                String courseEndTimeString = timeString[1];
                // parse the localTime String by the format of 9:00 AM - 10:20 AM
                DateTimeFormatter timeFormatter= DateTimeFormatter.ofPattern("h:m a");
                LocalTime courseStartTime = LocalTime.parse(courseStartTimeString, timeFormatter);
                LocalTime courseEndTime = LocalTime.parse(courseEndTimeString, timeFormatter);
                // get location
                String courseLocation = courseTimeAndLocationElement.child(3).text();
                // get the course days of week
                String dayOfWeeksString = courseTimeAndLocationElement.child(2).text();
                // split the dayOdWeeksString and parse the dayOfWeekString into DayOfWeek
                for (char dayOfWeekChar : dayOfWeeksString.toCharArray()) {
                    DayOfWeek dayOfWeek;
                    switch (dayOfWeekChar) {
                        case 'M':
                            dayOfWeek = DayOfWeek.MONDAY;
                            courseTimesAndLocationList.add(new TimeAndLocation(type, courseStartTime, courseEndTime,
                                    dayOfWeek, courseLocation));
                            break;
                        case 'T':
                            dayOfWeek = DayOfWeek.TUESDAY;
                            courseTimesAndLocationList.add(new TimeAndLocation(type, courseStartTime, courseEndTime,
                                    dayOfWeek, courseLocation));
                            break;
                        case 'W':
                            dayOfWeek = DayOfWeek.WEDNESDAY;
                            courseTimesAndLocationList.add(new TimeAndLocation(type, courseStartTime, courseEndTime,
                                    dayOfWeek, courseLocation));
                            break;
                        case 'R':
                            dayOfWeek = DayOfWeek.THURSDAY;
                            courseTimesAndLocationList.add(new TimeAndLocation(type, courseStartTime, courseEndTime,
                                    dayOfWeek, courseLocation));
                            break;
                        case 'F':
                            dayOfWeek = DayOfWeek.FRIDAY;
                            courseTimesAndLocationList.add(new TimeAndLocation(type, courseStartTime, courseEndTime,
                                    dayOfWeek, courseLocation));
                            break;
                    }
                }
                coursesList.add(new Course(title, courseTimesAndLocationList, instructorEmail,
                        instructorName));
            }
        }
        return coursesList;
    }

    private static Calendar generateCalendar(List<Course> coursesList) {

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        calendar.getProperties().add(CalScale.GREGORIAN);

        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        Date utilEndDate = DateTimeUtils.toDate(END_DATE.atStartOfDay(zoneId).toInstant());
        net.fortuna.ical4j.model.Date icalEndDate = new net.fortuna.ical4j.model.Date(utilEndDate);

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone icalTimeZone = registry.getTimeZone(zoneId.getId());

        calendar.getComponents().add(icalTimeZone.getVTimeZone());

        UidGenerator uidGenerator = new RandomUidGenerator();

        for (Course course : coursesList) {
            for (TimeAndLocation timeAndLocation : course.getTimeAndLocation()) {

                // convert start time type and feed start time to the ical4j
                LocalDate startEndDate = START_DATE.minusDays(1);
                LocalDateTime startDateTime = startEndDate.atTime(timeAndLocation.getStartTime());
                DateTime icalStartDateTime = new DateTime(DateTimeUtils.toDate(
                        startDateTime.atZone(zoneId).toInstant()));
                icalStartDateTime.setTimeZone(icalTimeZone);

                LocalDateTime endDateTime = startEndDate.atTime(timeAndLocation.getEndTime());
                DateTime icalEndDateTime = new DateTime(DateTimeUtils.toDate(
                        endDateTime.atZone(zoneId).toInstant()));
                icalEndDateTime.setTimeZone(icalTimeZone);

                String summary = course.getTitle();

                VEvent event = new VEvent(icalStartDateTime, icalEndDateTime, summary);

                Uid uid = uidGenerator.generateUid();
                event.getProperties().add(uid);

                Recur recur = new Recur(Recur.WEEKLY, icalEndDate);
                WeekDay weekDay = WeekDay.getDay(timeAndLocation.getDayOfWeek().getValue() % 7 + 1);
                recur.getDayList().add(weekDay);
                RRule rRule = new RRule(recur);
                event.getProperties().add(rRule);

                DateList exDateList = new DateList(Value.DATE_TIME, icalTimeZone);
                exDateList.add(icalStartDateTime);
                ExDate exDate = new ExDate(exDateList);
                event.getProperties().add(exDate);

                //event.getProperties().add(icalTimeZone.getVTimeZone().getTimeZoneId());

                Attendee instructor = new Attendee(URI.create(course.getInstructorEmail()));
                instructor.getParameters().add(new Cn(course.getInstructorName()));
                event.getProperties().add(instructor);

                calendar.getComponents().add(event);
            }
        }

        calendar.validate();

        return calendar;
    }

    private static void outputCalendar(Calendar courseCalendar) throws IOException {
        System.out.println("Enter the output file name:");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        fileName = (fileName != null ? fileName : "CourseEvent") + ".ics";

        FileOutputStream outputStream = new FileOutputStream(fileName);
        new CalendarOutputter().output(courseCalendar, outputStream);
    }
}
