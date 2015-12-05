package org.oo.onchart.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.oo.onchart.R;
import org.oo.onchart.student.Lesson;
import org.w3c.dom.Text;

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

public class DetailFragment extends DialogFragment {

    Lesson lesson;
    public DetailFragment() {
        lesson = null;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if(lesson != null) {
            TextView lessonNameText = (TextView) rootView.findViewById(R.id.tv_lesson_name);
            TextView teacherText = (TextView) rootView.findViewById(R.id.tv_teacher);
            TextView classroomText = (TextView) rootView.findViewById(R.id.tv_classroom);
            TextView weekText = (TextView) rootView.findViewById(R.id.tv_week_cycle);
            TextView creditText = (TextView) rootView.findViewById(R.id.tv_credit);

            lessonNameText.setText(lesson.getName());
            teacherText.setText(lesson.getTeacher());
            classroomText.setText(lesson.getClassroom());
            weekText.setText(lesson.getStartWeek() + " - " + lesson.getEndWeek() + getString(R.string.weekday_week));
            creditText.setText(lesson.getCredit() + "");

        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return rootView;
    }


}
