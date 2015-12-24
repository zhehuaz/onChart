package me.zchang.onchart.ui.adapter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import me.zchang.onchart.R;

/**
 * Created by Administrator on 2015/12/23.
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
                    if (adapter.getItemViewType(i) == LessonListAdapter.VIEW_TYPE_LIST) {
                        View child = recyclerView.getChildAt(i);
                        if (child != null) {
                                child.setTranslationX(offsetOf(i, position));
                        }
                    }
                }
            }
        }
    }

    private float offsetOf(int i, float position) {
        Log.d("DiffTransformer", "position " + position);
        if (position <= 1)
            return ( position / 2 * pageWidth);
        else
            return ( (2-position) / 2  * pageWidth);
    }
}
