package me.zchang.onchart.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.ConfigManager;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.ui.utils.CardToDialog;
import me.zchang.onchart.ui.utils.DialogToCard;

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

public class DetailActivity extends AppCompatActivity {

    Intent retIntent;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();
        int startColor = intent.getIntExtra("color", -1);
        final Course course = intent.getParcelableExtra(getString(R.string.intent_lesson));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ArcMotion arcMotion = new ArcMotion();
            arcMotion.setMinimumHorizontalAngle(50f);
            arcMotion.setMinimumVerticalAngle(50f);
            Interpolator easeInOut = new AccelerateDecelerateInterpolator();
            TransitionSet sharedEnterSet = new TransitionSet();
            ChangeBounds sharedEnter = new ChangeBounds();
	        //CardToDialog sharedEnter = new CardToDialog(startColor);
	        sharedEnter.setPathMotion(arcMotion);
            sharedEnter.setInterpolator(easeInOut);
            sharedEnterSet.addTransition(sharedEnter);
            ChangeImageTransform imgTrans = new ChangeImageTransform();
            imgTrans.addTarget(R.id.iv_label);

            TransitionSet sharedExitSet = new TransitionSet();
            DialogToCard sharedExit = new DialogToCard(startColor);
            sharedExit.setPathMotion(arcMotion);
            sharedExitSet.addTransition(sharedExit);
            sharedExitSet.addTransition(imgTrans);
            getWindow().setSharedElementEnterTransition(sharedEnterSet);
            getWindow().setSharedElementReturnTransition(sharedExit);
        }

        retIntent = new Intent();

        setResult(RESULT_CANCELED, retIntent);
        retIntent.putExtra(getString(R.string.intent_position), intent.getIntExtra(getString(R.string.intent_position), 0));
        if(course != null) {
            retIntent.putExtra(getString(R.string.intent_course_id), course.getId());
            final TextView lessonNameText = (TextView) findViewById(R.id.tv_lesson_name);
            TextView teacherText = (TextView) findViewById(R.id.tv_teacher);
            TextView classroomText = (TextView) findViewById(R.id.tv_classroom);
            TextView weekText = (TextView) findViewById(R.id.tv_week_cycle);
            TextView creditText = (TextView) findViewById(R.id.tv_credit);
	        ImageButton deleteButton = (ImageButton) findViewById(R.id.iv_delete);
	        ImageButton editButton = (ImageButton) findViewById(R.id.iv_edit);
	        final ImageView labelImage = (ImageView) findViewById(R.id.iv_label);
            final ImageView backgroundImage = (ImageView) findViewById(R.id.iv_background);

            lessonNameText.setText(course.getName());
            teacherText.setText(course.getTeacher());
            classroomText.setText(course.getClassroom());
            weekText.setText(String.format(getString(R.string.detail_week_pattern), course.getStartWeek(), course.getEndWeek()));
	        creditText.setText(String.format(getString(R.string.detail_credit), course.getCredit()));
	        labelImage.setImageResource(ConfigManager.labelImgIndices[course.getLabelImgIndex()]);
	        labelImage.setOnTouchListener(new View.OnTouchListener() {
		        @Override
		        public boolean onTouch(View view, MotionEvent motionEvent) {
			        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // performance consideration
                            backgroundImage.setImageResource(ConfigManager.labelImgIndices[course.getLabelImgIndex()]);
                        course.setToNextLabelImg();
				        labelImage.setImageResource(ConfigManager.labelImgIndices[course.getLabelImgIndex()]);
				        // update local storage only.
				        ((MainApp) getApplication()).getConfigManager().saveImgPathIndex(course.getId(), course.getLabelImgIndex());

				        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					        Animator reveal = ViewAnimationUtils.createCircularReveal(
							        view,
							        (int) motionEvent.getX(),
							        (int) motionEvent.getY(),
							        0,
							        view.getLayoutParams().width + 300
					        );
					        reveal.start();
				        }

				        retIntent.putExtra(getString(R.string.intent_label_image_index), course.getLabelImgIndex());
				        setResult(RESULT_OK, retIntent);
				        return true;
			        }
			        return false;
		        }
	        });

	        deleteButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {

		        }
	        });

	        editButton.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View view) {

		        }
	        });
        }
    }

    @Override
    public void onBackPressed() {
            dismissCompat(null);
    }

    public void dismissCompat(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
