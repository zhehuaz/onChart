package me.zchang.onchart.ui.adapter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import me.zchang.onchart.R;

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

public class DiffTransformer implements ViewPager.PageTransformer {
    int pageWidth;
    @Override
    public void transformPage(View page, float position) {
        pageWidth = page.getWidth();
        if (page instanceof FrameLayout) {
            RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.rv_lessons);
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (recyclerView != null && adapter != null) {
                int count = adapter.getItemCount();
                for (int i = 0;i < count;i ++) {
                    if (adapter.getItemViewType(i) == CourseListAdapter.VIEW_TYPE_LIST) {
                        View child = recyclerView.getChildAt(i);
                        if (child != null) {
                            View cardView = child.findViewById(R.id.cd_lesson_item);
                            if (cardView != null) {
                                cardView.setTranslationX(offsetOf(position));
                            }
                        }
                    }
                }
            }
        }
    }

    private float offsetOf(float position) {
        if (position <= 1)
            return ( position / 2 * pageWidth);
        else
            return ((1 - position) / 2 * pageWidth);
    }
}
