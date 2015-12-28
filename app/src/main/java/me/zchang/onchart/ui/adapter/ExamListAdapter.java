package me.zchang.onchart.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.ui.MainActivity;

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

public class ExamListAdapter extends RecyclerView.Adapter {
    public final static int VIEW_TYPE_HEADER = 0x0;
    public final static int VIEW_TYPE_LIST = 0x1;
    List<Exam> exams;
    Context context;

    public ExamListAdapter(Context context, List<Exam> exams) {
        this.context = context;
        this.exams = exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.blank_short, parent, false));
            case VIEW_TYPE_LIST:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cd_exam_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Calendar today = Calendar.getInstance();
            Exam currExamItem = exams.get(position - 1);
            TextView dateText = ((ViewHolder) holder).dateText;
            TextView timeText = ((ViewHolder) holder).timeText;
            TextView courseNameText = ((ViewHolder) holder).courseNameText;
            TextView positionText = ((ViewHolder) holder).positionText;
            CardView cardView = ((ViewHolder) holder).cardView;
            RelativeLayout countdownLayout = ((ViewHolder) holder).countdownContainer;
            TextView countdownText = ((ViewHolder) holder).countdownText;
            TextView dayText = ((ViewHolder) holder).dayText;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
            dateText.setText(dateFormat.format(currExamItem.getStartTime()));
            timeText.setText(timeFormat.format(currExamItem.getStartTime())
                    + "-"
                    + timeFormat.format(currExamItem.getEndTime()));
            courseNameText.setText(currExamItem.getName());
            positionText.setText(currExamItem.getPosition());
            long previousMillis = currExamItem.getStartTime().getTime() - today.getTimeInMillis();
            if (previousMillis > 0) {
                long previousDays = previousMillis / MainActivity.MILLISECONDS_IN_A_DAY;
                if (previousDays <= 14) {
                    //countdownText.setTextColor(Color.parseColor("#EE0000"));
                    //dayText.setTextColor(Color.parseColor("#EE0000"));
                    cardView.setCardBackgroundColor(Color.parseColor("#EF5350"));
                    dateText.setTextColor(Color.parseColor("#EF5350"));
                    timeText.setTextColor(Color.parseColor("#EF5350"));
                } else {
                    cardView.setCardBackgroundColor(Color.parseColor("#00B0FF"));
                    dateText.setTextColor(Color.parseColor("#00B0FF"));
                    timeText.setTextColor(Color.parseColor("#00B0FF"));
                }
                courseNameText.setTextColor(0xffffffff);
                positionText.setTextColor(0xffffffff);
                countdownLayout.setVisibility(View.VISIBLE);
                countdownText.setText(previousDays + "");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (exams != null)
            return exams.size() + 1;
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_LIST;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView timeText;
        TextView courseNameText;
        TextView positionText;
        CardView cardView;
        TextView countdownText;
        TextView dayText;
        RelativeLayout countdownContainer;
        public ViewHolder(View itemView) {
            super(itemView);
            dateText = (TextView) itemView.findViewById(R.id.tv_exam_date);
            timeText = (TextView) itemView.findViewById(R.id.tv_exam_time);
            courseNameText = (TextView) itemView.findViewById(R.id.tv_exam_course_name);
            positionText = (TextView) itemView.findViewById(R.id.tv_exam_position);
            cardView = (CardView) itemView.findViewById(R.id.cd_exam);
            countdownContainer = (RelativeLayout) itemView.findViewById(R.id.rl_countdown);
            countdownText = (TextView) countdownContainer.findViewById(R.id.tv_exam_countdown);
            dayText = (TextView) countdownContainer.findViewById(R.id.tv_exam_day);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
