/*
 *     Copyright 2016 Zhehua Chang
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package zchang.me.uilibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SideBarLayout extends LinearLayout {
    public final static String TAG = "SideBarLayout";
    private boolean loadOnce = false;
    View header;

    public SideBarLayout(Context context) {
        super(context, null);
    }

    public SideBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setHeader(View header) {
        this.header = header;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (!loadOnce) {
                if (header != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = -220;
                    header.setLayoutParams(params);
                    addView(header, 0);

                    loadOnce = true;

                }
            } else {
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                if (layoutParams != null)
                    Log.i(TAG, "header height: " + layoutParams.height);
            }
        }
    }

    private final static int DRAG_THRESHOLD = -150;
    private final static int DRAG_THRESHOLD_UP = -70;
    private final static int MOTION_THRESHOLD_DOWN = -175;
    private final static int MOTION_THRESHOLD_UP = -55;


    float deltaY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0) {
                    deltaY = (event.getY() - event.getHistoricalY(event.getActionIndex())) / 3;
                    LinearLayout.LayoutParams params = (LayoutParams) header.getLayoutParams();
                    if (params.topMargin + deltaY <= DRAG_THRESHOLD && deltaY > 0) {
                        params.topMargin += deltaY;
                        header.setLayoutParams(params);
                    } else if (params.topMargin + deltaY > DRAG_THRESHOLD_UP && deltaY < 0) {
                        params.topMargin += deltaY;
                        header.setLayoutParams(params);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final LinearLayout.LayoutParams params = (LayoutParams) header.getLayoutParams();
                if (deltaY > 0 && params.topMargin >= MOTION_THRESHOLD_DOWN) {
                    ValueAnimator animator = ValueAnimator.ofInt(params.topMargin, 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            params.topMargin = (int) animation.getAnimatedValue();
                            header.setLayoutParams(params);
                        }
                    });
                    animator.setInterpolator(new AccelerateInterpolator(3));
                    animator.setDuration(300);
                    animator.setTarget(header);
                    animator.start();
                } else if (deltaY < 0 && params.topMargin < MOTION_THRESHOLD_UP) {
                    ValueAnimator animator = ValueAnimator.ofInt(params.topMargin, -200);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            params.topMargin = (int) animation.getAnimatedValue();
                            header.setLayoutParams(params);
                        }
                    });
                    animator.setInterpolator(new AccelerateInterpolator(3));
                    animator.setDuration(300);
                    animator.setTarget(header);
                    animator.start();
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
