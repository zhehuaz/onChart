package org.oo.onchart.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.oo.onchart.parser.Utils;
import org.oo.onchart.ui.LessonListFragment;

import java.util.List;

/**
 * Created by langley on 11/17/15.
 */
public class LessonPagerAdapter extends FragmentPagerAdapter {
    private List<LessonListFragment> fragments;
    private Context context;

    public LessonPagerAdapter(Context context, FragmentManager fm, List<LessonListFragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(Utils.weekdayFromIndex[position]);
    }
}
