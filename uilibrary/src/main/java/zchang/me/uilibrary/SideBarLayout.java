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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SideBarLayout extends LinearLayout {

    private TextView testTextView;
    private boolean loadOnce = false;
    LinearLayout header;

    public SideBarLayout(Context context) {
        super(context, null);
    }

    public SideBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

         header = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.header_week_num, null);
//        testTextView = new TextView(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        testTextView.setLayoutParams(layoutParams);
//
//        testTextView.setText("Hello");
        //layoutParams.rightMargin = -10;
        //testTextView.setLayoutParams(layoutParams);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) testTextView.getLayoutParams();
//            layoutParams.topMargin = 0;
//            layoutParams.height = 50;
//            testTextView.setLayoutParams(layoutParams);
            addView(header, 0);
            loadOnce = true;
        }
    }
}
