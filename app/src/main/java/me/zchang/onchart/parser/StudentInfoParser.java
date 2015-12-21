/*
 *
 *  *    Copyright 2015 Zhehua Chang
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package me.zchang.onchart.parser;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.LabelCourse;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Parser for the information relative to students.
 * All the html files are from <a href="http://jwc.bit.edu.cn"/>
 */
public class StudentInfoParser {
    private final static String TAG = "StudentInfoParser";

    /**
     * Parse the schedule from a html text.
     * @param htmlText the source html text.
     * @return the list of courses parsed from the source.Return null if failed.
     */
    public static List<Course> parseSchedule(@NonNull String htmlText)
    {
        Document doc = Jsoup.parse(htmlText);
        Element chartTable = doc.select("table#dgrdKb").first();
        if(chartTable != null) {
            Elements lessonEles = chartTable.select("tr");
            List<Course> courses = new ArrayList<>();
            int lessonId = 0;

            List<Course> dupCourses = new ArrayList<>();
            for (Element e : lessonEles) {
                dupCourses.clear();
                if (!e.className().equals("datagridhead")) {
                    Course baseCourse = new LabelCourse();
                    Elements lessonInfo = e.getAllElements();

                    baseCourse.setName(lessonInfo.get(1).text());
                    baseCourse.setCredit(Float.parseFloat(lessonInfo.get(2).text()));
                    baseCourse.setDepartment(lessonInfo.get(5).text());
                    baseCourse.setTeacher(lessonInfo.get(7).text());

                    String textTime = lessonInfo.get(8).text();
                    String[] textsTime = textTime.split(";");
                    String textClassroom = lessonInfo.get(9).text();
                    String[] textsClassroom = textClassroom.split(";");
                    int j = 0;
                    for (String s : textsClassroom) {
                        dupCourses.add(new LabelCourse(baseCourse).setId(lessonId++));
                        dupCourses.get(j).setClassroom(s);

                        if (textsTime[j].length() == 1)
                            dupCourses.get(j).setWeekDay('0');
                        else
                            dupCourses.get(j).setWeekDay(textsTime[j].charAt(1));

                        String pattern = "(\\d)+(?=,)|(\\d+)*(?=节)";
                        Pattern reg = Pattern.compile(pattern);
                        Matcher m = reg.matcher(textsTime[j]);
                        if (m.find()) {
                            dupCourses.get(j).setStartTime(Integer.parseInt(m.group()));
                        }
                        String endTime = null;
                        while (m.find() && m.group().length() > 0) {
                            endTime = m.group();
                        }
                        if (endTime == null) {
                            dupCourses.get(j).setEndTime(dupCourses.get(j).getStartTime());
                        } else {
                            dupCourses.get(j).setEndTime(Integer.parseInt(endTime));
                        }

                        pattern = "\\d+(?=-)|\\d+(?=周)";
                        reg = Pattern.compile(pattern);
                        m = reg.matcher(textsTime[j]);
                        if (m.find()) {
                            dupCourses.get(j).setStartWeek(Integer.parseInt(m.group()));
                        } else {
                            dupCourses.get(j).setStartWeek(0);
                        }
                        if (m.find()) {
                            dupCourses.get(j).setEndWeek(Integer.parseInt(m.group()));
                        } else {
                            dupCourses.get(j).setEndTime(dupCourses.get(j).getStartTime());
                        }

                        pattern = "(单|双)(?=周)";
                        reg = Pattern.compile(pattern);
                        m = reg.matcher(textsTime[j]);
                        if (m.find()) {
                            if (m.group().equals("单"))
                                dupCourses.get(j).setWeekParity((byte) 1);
                            else
                                dupCourses.get(j).setWeekParity((byte) 2);
                        } else {
                            dupCourses.get(j).setWeekParity((byte) -1);
                        }
                        j++;
                    }
                }
                courses.addAll(dupCourses);
            }
            Collections.sort(courses);
            return courses;
        } else {
            return null;
        }
    }

    /**
     * Parse current week number.
     * @param htmlText the source html text.
     * @return the week number.
     */
    public static int parseWeek(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Elements rootElements = doc.select("a.black");
        if (rootElements.size() > 0) {
            Element rootElement = rootElements.get(0);
            Element childElement;
            if (rootElement != null) {
                childElement = rootElement.select("b").get(0);
                if (childElement != null)
                    return Integer.parseInt(childElement.text());
            }
        }
        return -1;
    }

    /**
     * Parse student name.
     * @param htmlText the source html text.
     * @return the student name.
     */
    public static String parseName(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Elements elements = doc.select("span#xhxm");
        if(elements != null && !elements.isEmpty()) {
            Element element = elements.get(0);

            Pattern pattern = Pattern.compile(" .*(?=同学)");
            Matcher m = pattern.matcher(element.text());
            if (m.find()) {
                return m.group().trim();
            }

        }
        return null;
    }

}
