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

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SideBarLayout extends LinearLayout {
    public final static String TAG = "SideBarLayout";
    private TextView testTextView;
    private boolean loadOnce = false;
    View header;

    public SideBarLayout(Context context) {
        super(context, null);
    }

    public SideBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

         //header = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.header_week_num, null);
//        testTextView = new TextView(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        testTextView.setLayoutParams(layoutParams);
//
//        testTextView.setText("Hello");
        //layoutParams.rightMargin = -10;
        //testTextView.setLayoutParams(layoutParams);
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
                    params.topMargin = -200;
                    header.setLayoutParams(params);
                    addView(header, 0);

//                View view = getChildAt(1);
//                if (view instanceof ViewPager) {
//                    for (int i = 0;i < ((ViewPager) view).getChildCount(); i ++) {
//                        RecyclerView list = (RecyclerView) ((FrameLayout) ((ViewPager) view).getChildAt(i)).getChildAt(0);
//                        list.setOnTouchListener(this);
//                    }
//                } else {
//                    Log.i(TAG, "not view pager");
//                }

                    loadOnce = true;

                }
            } else {
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                if (layoutParams != null)
                    Log.i(TAG, "header height: " + layoutParams.height);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.i(TAG, "layout touch event");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0) {
                    float deltaY = event.getY() - event.getHistoricalY(event.getActionIndex());
                    LinearLayout.LayoutParams params = (LayoutParams) header.getLayoutParams();
                    params.topMargin += deltaY;
                    header.setLayoutParams(params);
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
