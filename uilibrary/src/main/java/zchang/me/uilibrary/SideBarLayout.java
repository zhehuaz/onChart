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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public class SideBarLayout extends LinearLayout {
    public final static String TAG = "SideBarLayout";
    protected Context context;
    private boolean loadOnce = false;
    View header;

    public SideBarLayout(Context context) {
        super(context, null);
    }

    public SideBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.context = context;
    }

    public void setHeader(View header) {
        if (this.header == null) {
            this.header = header;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (!loadOnce) {
                if (header != null) {
                    addView(header, 0);
                    LinearLayout.LayoutParams layoutParams = (LayoutParams) header.getLayoutParams();
                    if (layoutParams != null) {
                        HEIGHT = layoutParams.height;
                        MOTION_THRESHOLD_DOWN = -HEIGHT / 5 * 4;
                        MOTION_THRESHOLD_UP = -HEIGHT / 5;
                        layoutParams.topMargin = -HEIGHT;
                        header.setLayoutParams(layoutParams);
                    }
                    loadOnce = true;
                }
            }
        }
    }

    private int HEIGHT = 280;

    private int MOTION_THRESHOLD_DOWN = -225;
    private int MOTION_THRESHOLD_UP = -50;

    public final static int STATE_VISIBLE = 0x0;
    public final static int STATE_INVISIBLE = 0x1;

    public final static int DELAY_SHORT = 50;
    public final static int DELAY_LONG = 310;

    float deltaY = 0;
    float amountY = 0;
    int newPos;

    int state = STATE_INVISIBLE;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0) {
                    LinearLayout.LayoutParams params = (LayoutParams) header.getLayoutParams();
                    deltaY = (event.getY() - event.getHistoricalY(event.getActionIndex()));
                    Log.i(TAG, "deltaY is " + deltaY);
                    amountY += deltaY;
                    if (state == STATE_INVISIBLE) {
                        newPos = (int) (HEIGHT / 3.5 * Math.atan(amountY / HEIGHT)) - HEIGHT;
                    } else if (state == STATE_VISIBLE) {
                        newPos = (int) (HEIGHT / 1.4 * Math.atan(amountY / HEIGHT * 1.4));
                    }
                    if (validRange(newPos)) {
                        params.topMargin = newPos;
                        header.setLayoutParams(params);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final LinearLayout.LayoutParams params = (LayoutParams) header.getLayoutParams();
                if (amountY > 0) {
                    if (params.topMargin >= MOTION_THRESHOLD_DOWN) {
                        headerMarginAnimation(params, params.topMargin, 0, new AccelerateDecelerateInterpolator());
                        state = STATE_VISIBLE;
                    } else {
                        headerMarginAnimation(params, params.topMargin, -HEIGHT, new AccelerateDecelerateInterpolator());
                        state = STATE_INVISIBLE;
                    }
                } else if (amountY < 0) {
                    if (params.topMargin < MOTION_THRESHOLD_UP) {
                        headerMarginAnimation(params, params.topMargin, -HEIGHT, new LinearInterpolator());
                        state = STATE_INVISIBLE;
                    } else {
                        headerMarginAnimation(params, params.topMargin, 0, new AccelerateDecelerateInterpolator());
                        state = STATE_VISIBLE;
                    }
                }
                amountY = 0;
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private void headerMarginAnimation(final LinearLayout.LayoutParams params, int from, int to, Interpolator interpolator) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin = (int) animation.getAnimatedValue();
                header.setLayoutParams(params);
            }
        });
        animator.setInterpolator(interpolator);
        animator.setDuration(150);
        animator.start();
    }

    private boolean validRange(int pos) {
        return pos >= -HEIGHT && pos <= 0;
    }
}
