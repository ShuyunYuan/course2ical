package me.yuanshuyun.java.course2ical;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

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
        Elements courseItemElements = document.getElementsByClass("CourseItem");
        // 4. Iterate course objects and feed them to ical4j
        List<Course> courses = new ArrayList<>();
        for (Element courseItemElement : courseItemElements) {
            // Get the string
            // get course title
            Element courseTitleElement = courseItemElement.getElementsByClass("ClassTitle").first();
            String courseTitle = courseTitleElement != null ? courseTitleElement.text() : null;
            // get instructor information, name and email
            Element courseInstructorElement = courseItemElement.selectFirst(
                    ".classDescription > div:nth-child(2) > a");
            String courseInstructorName = courseInstructorElement != null ? courseInstructorElement.text() : null;
            String courseInstructorEmail = courseInstructorElement != null ? courseInstructorElement.attr("href")
                    : null;
            Elements courseTimeElements = courseItemElement.select(".meeting-times > .meeting");
            List<String> courseTimes = new ArrayList<>();
            for (Element courseTimeElement : courseTimeElements) {
                courseTimes.add(courseItemElement.child(0).text());
                courseTimes.add(courseItemElement.child(1).text());
                courseTimes.add(courseItemElement.child(2).text());

            }
            Course(courseTitle, courseTimes, courseInstructorEmail, courseInstructorName);

        }
        // 5. Write ical4j ouput to file.
    }
}
    List<Course> courses = new ArrayList<>();

    private static void usage() {
        System.err.println("Usage: course2ical <HTML_FILE>");
    }
}
