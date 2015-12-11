package me.zchang.onchart.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.student.Lesson;

public class DetailActivity extends AppCompatActivity {

    //Intent retIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //retIntent = new Intent();
        final Intent intent = getIntent();
        final Lesson lesson = intent.getParcelableExtra(getString(R.string.intent_lesson));
        int position = intent.getIntExtra(getString(R.string.intent_position), 0);
        final int fragIndex = intent.getIntExtra(getString(R.string.intent_lesson), 0);

        //setResult(RESULT_CANCELED);
        //retIntent.putExtra(getString(R.string.intent_frag_index), fragIndex);
        //retIntent.putExtra(getString(R.string.intent_position), position);
        if(lesson != null) {
            final TextView lessonNameText = (TextView) findViewById(R.id.tv_lesson_name);
            TextView teacherText = (TextView) findViewById(R.id.tv_teacher);
            TextView classroomText = (TextView) findViewById(R.id.tv_classroom);
            TextView weekText = (TextView) findViewById(R.id.tv_week_cycle);
            TextView creditText = (TextView) findViewById(R.id.tv_credit);
            final ImageView labelImage = (ImageView) findViewById(R.id.iv_label);

            lessonNameText.setText(lesson.getName());
            teacherText.setText(lesson.getTeacher());
            classroomText.setText(lesson.getClassroom());
            weekText.setText(lesson.getStartWeek() + " - " + lesson.getEndWeek() + getString(R.string.weekday_week));
            creditText.setText(lesson.getCredit() + "");
            labelImage.setImageResource(PreferenceManager.labelImgs[lesson.getLabelImgIndex()]);

            labelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO too many things to modify
                    lesson.setToNextLabelImg();
                    ((MainApp) getApplication()).getPreferenceManager().savePicPathIndex(lesson.getId(), lesson.getLabelImgIndex());
                    labelImage.setImageResource(PreferenceManager.labelImgs[lesson.getLabelImgIndex()]);
                    //((MainActivity)getActivity()).getListFragment().adapter.notifyItemChanged(position);
                    //retIntent.putExtra(getString(R.string.intent_frag_index), fragIndex);
                    //setResult(RESULT_OK, intent);
                }
            });
        }
    }

}
