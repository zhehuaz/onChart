package org.oo.onchart.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oo.onchart.R;
import org.oo.onchart.student.Lesson;
import org.oo.onchart.ui.adapter.LessonListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonListFragment extends Fragment {

    RecyclerView lessonList;
    LessonListAdapter adapter;

    public LessonListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new LessonListAdapter(getActivity(), new ArrayList<Lesson>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lesson_list, container, false);
        lessonList = (RecyclerView) rootView.findViewById(R.id.rv_lessons);
        lessonList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //adapter.addLesson(lesson)
        lessonList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }




}
