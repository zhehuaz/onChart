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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.oo.onchart.student.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * parser for student information parser.
 */
public class StudentInfoParser {


    public static void parseChart(String htmlText)
    {
        Document doc = Jsoup.parse(htmlText);
        Element chartTable = doc.select("table#dgrdKb").first();
        Elements lessonEles = chartTable.select("tr");
        List<Lesson> lessons = new ArrayList<>();

        List<Lesson> dupLessons = new ArrayList<>();
        for(Element e : lessonEles) {
            if(!e.className().equals("datagridhead")) {
                Lesson baseLesson = new Lesson();
                //dupLessons.add(newLesson);
                Elements lessonInfo = e.getAllElements();
                Lesson newLesson = new Lesson();
                int i = 0;
                String text;
                String[] texts;
                for(Element info : lessonInfo) {
                    switch (i) {
                        case 1:
                            baseLesson.setName(info.text());
                            break;
                        case 2:
                            baseLesson.setCredit(Float.parseFloat(info.text()));
                            break;
                        case 5:
                            baseLesson.setDepartment(info.text());
                            break;
                        case 7:
                            baseLesson.setTeacher(info.text());
                            break;
                        case 8:
                            text = info.text();
                            texts = text.split(";");
                            for(String s : texts) {
                                //dupLessons.add(new Lesson(baseLesson));
                                // TODO parse time string
                                newLesson = new Lesson(baseLesson);
                                newLesson.setClassroom(s);
                                dupLessons.add(newLesson);
                            }
                            break;
                        case 9:
                            text = info.text();
                            texts = text.split(";");
                            int j = 0;
                            for(String s : texts) {
                                dupLessons.get(j).setClassroom(s);
                                j ++;
                            }
                            break;
                    }
                    i ++;
                }
            }
        }

        return ;
    }
}
