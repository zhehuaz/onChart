package me.zchang.onchart.ui.adapter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import me.zchang.onchart.R;

/**
 * Created by Administrator on 2015/12/23.
 */
public class DiffTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        if (page instanceof FrameLayout) {
            RecyclerView recyclerView = (RecyclerView) page.findViewById(R.id.rv_lessons);
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (recyclerView != null && adapter != null) {
                int count = adapter.getItemCount();
                for (int i = 0;i < count;i ++) {
                    if (adapter.getItemViewType(i) == LessonListAdapter.VIEW_TYPE_LIST) {
                        View child = recyclerView.getChildAt(i);
                        if (child != null) {
                            if (position <= 1)
                                child.setTranslationX((float) (-(1.0 - position) * 0.1 * i * pageWidth));
                        }
                    }
                }
            }
        }
    }
}
