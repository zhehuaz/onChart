package org.oo.onchart.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.oo.onchart.R;
import org.oo.onchart.student.Lesson;

import java.util.List;

/**
 * Created by langley on 11/17/15.
 */
public class LessonListAdapter extends RecyclerView.Adapter<LessonListAdapter.ViewHolder> {
    List<Lesson> lessons;
    Context context;

    public LessonListAdapter(Context context, List<Lesson> lessons) {
        this.lessons = lessons;
        this.context = context;
    }

    public void addLesson(Lesson lesson) {
        if(lesson != null && lessons != null)
            lessons.add(lesson);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cd_lesson_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(lessons.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cd_lesson_item);
            text = (TextView) itemView.findViewById(R.id.tv_test);
        }
    }


}
