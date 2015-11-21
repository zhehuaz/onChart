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

package org.oo.onchart.parser;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.oo.onchart.student.Lesson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * parser for student information parser.
 */
public class StudentInfoParser {
    private final static String TAG = "StudentInfoParser";

    public static List<Lesson> parseChart(@NonNull String htmlText)
    {
        Document doc = Jsoup.parse(htmlText);
        Element chartTable = doc.select("table#dgrdKb").first();
        Elements lessonEles = chartTable.select("tr");
        List<Lesson> lessons = new ArrayList<>();

        List<Lesson> dupLessons = new ArrayList<>();
        for(Element e : lessonEles) {
            dupLessons.clear();
            if(!e.className().equals("datagridhead")) {
                Lesson baseLesson = new Lesson();
                //dupLessons.add(newLesson);
                Elements lessonInfo = e.getAllElements();

                baseLesson.setName(lessonInfo.get(1).text());
                baseLesson.setCredit(Float.parseFloat(lessonInfo.get(2).text()));
                baseLesson.setDepartment(lessonInfo.get(5).text());
                baseLesson.setTeacher(lessonInfo.get(7).text());

                String textTime = lessonInfo.get(8).text();
                String[] textsTime = textTime.split(";");
                String textClassroom = lessonInfo.get(9).text();
                String[] textsClassroom = textClassroom.split(";");
                int j = 0;
                for (String s : textsClassroom) {
                    dupLessons.add(new Lesson(baseLesson));
                    dupLessons.get(j).setClassroom(s);

                    //Log.d(TAG, textsTime[j]);
                    if(textsTime[j].length() == 1)
                        dupLessons.get(j).setWeekDay('0');
                    else
                        dupLessons.get(j).setWeekDay(textsTime[j].charAt(1));

                    String pattern = "(\\d)+(?=,)|(\\d+)*(?=节)";
                    Pattern reg = Pattern.compile(pattern);
                    Matcher m = reg.matcher(textsTime[j]);
                    if(m.find()) {
                        //Log.d(TAG, m.group());
                        dupLessons.get(j).setStartTime(Integer.parseInt(m.group()));
                    }
                    String endTime = null;
                    while(m.find() && m.group().length() > 0) {
                        endTime = m.group();
                    }
                    if(endTime == null) {
                        dupLessons.get(j).setEndTime(dupLessons.get(j).getStartTime());
                    } else {
                        //Log.d(TAG, endTime);
                        dupLessons.get(j).setEndTime(Integer.parseInt(endTime));
                    }

                    pattern = "\\d(?=-)|\\d+(?=周)";
                    reg = Pattern.compile(pattern);
                    m = reg.matcher(textsTime[j]);
                    if(m.find()) {
                        dupLessons.get(j).setStartWeek(Integer.parseInt(m.group()));
                    } else {
                        dupLessons.get(j).setStartWeek(0);
                    }
                    if(m.find()) {
                        dupLessons.get(j).setEndWeek(Integer.parseInt(m.group()));
                    } else {
                        dupLessons.get(j).setEndTime(dupLessons.get(j).getStartTime());
                    }
                    j++;
                }
            }
            lessons.addAll(dupLessons);
            Collections.sort(lessons);
        }
        return lessons;
    }

    public static int parseWeek(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Element rootElement = doc.select("a.black").get(0);
        Element childElement;
        if(rootElement != null) {
            childElement = rootElement.select("b").get(0);
            if(childElement != null)
                return Integer.parseInt(childElement.text());
        }
        return -1;
    }

    public static String parseName(@NonNull String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Element element = doc.select("span#xhxm").get(0);
        if(element != null) {
            Pattern pattern = Pattern.compile(" .*(?=同学)");
            Matcher m = pattern.matcher(element.text());
            if(m.find()) {
                return m.group().trim();
            }
        }
        return "";
    }

}
