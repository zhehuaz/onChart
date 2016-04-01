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

package me.zchang.onchart.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.zchang.onchart.R;
import me.zchang.onchart.config.ConfigManager;
import me.zchang.onchart.parser.Utils;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.LabelCourse;
import me.zchang.onchart.ui.DetailActivity;
import me.zchang.onchart.ui.MainActivity;

public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "LessonListAdapter";
    public final static int VIEW_TYPE_HEADER = 0;
    public final static int VIEW_TYPE_LIST = 1;
    public final static int VIEW_TYPE_SUBTITLE = 2;

    public final static int MORNING_FLAG = -1;
    public final static int AFTERNOON_FLAG = -2;
    public final static int EVENING_FLAG = -3;
    public final static int HEAD_FLAG = -4;

    private int morningCount;
    private int afternoonCount;
    private int eveningCount;

    private byte[] bitmap;

    List<Course> courses;
    Context context;
    private int fragId;

    private int screenWidth;

    public CourseListAdapter(Context context, List<Course> courses, int fragId) {
        bitmap = new byte[20];
        this.courses = courses;
        this.context = context;
        this.fragId = fragId;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        processLessons();
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        processLessons();
    }

    public void processLessons() {
        morningCount = 0;
        afternoonCount = 0;
        eveningCount = 0;
        for (Course l : courses) {
            long startTime = l.getStartTime();
            if (startTime < Utils.NOON_TIME)
                morningCount++;
            else if (startTime < Utils.EVENING_TIME)
                afternoonCount++;
            else
                eveningCount++;
        }

        int length = getItemCount();
        byte lessonCount = 0;
        bitmap[0] = HEAD_FLAG;
        for(int position = 1; position < length; position ++) {
            if (position == 1 && morningCount > 0) {
                bitmap[position] = MORNING_FLAG;
            } else if (position == ((morningCount + 1) * (morningCount > 0 ? 1 : 0) + 1)
                    && afternoonCount > 0) {
                bitmap[position] = AFTERNOON_FLAG;
            } else if (position == ((morningCount + 1) * (morningCount > 0 ? 1 : 0)
                    + (afternoonCount + 1) * (afternoonCount > 0 ? 1 : 0) + 1)
                    && eveningCount > 0) {
                bitmap[position] = EVENING_FLAG;
            } else {
                bitmap[position] = lessonCount;
                lessonCount++;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(new View(context));
            case VIEW_TYPE_LIST:
                return new ViewHolder(LayoutInflater.from(context).inflate(me.zchang.onchart.R.layout.cd_lesson_item, parent, false));
            case VIEW_TYPE_SUBTITLE:
                return new SubtitleViewHolder(LayoutInflater.from(context).inflate(me.zchang.onchart.R.layout.list_subtitle, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final LabelCourse course = (LabelCourse) courses.get(bitmap[position]);
            final TextView nameText = viewHolder.nameText;
            final TextView roomText = viewHolder.roomText;
            final TextView timeText = viewHolder.timeText;
            final ImageView nabImg = viewHolder.nabImg;
            final CardView cardView = viewHolder.cardView;
            final TextView backgroundIndicator = viewHolder.backgroundIndicator;
            nameText.setText(course.getName());
            roomText.setText(course.getClassroom());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            timeText.setText(dateFormat.format(course.getStartTime()));

            ViewGroup.LayoutParams params = ((ViewHolder) holder).frame.getLayoutParams();
            params.height = (viewHolder.cardHeight >> 1 ) *
                    (((int) course.getEndTime() - (int) course.getStartTime()) / Utils.MILLISECONDS_IN_ONE_CLASS + 1);
            ((ViewHolder) holder).frame.setLayoutParams(params);

            nabImg.setImageResource(ConfigManager.labelImgIndices[course.getLabelImgIndex()]);

            Drawable nab = nabImg.getDrawable();
            if(nab != null) {
                if ((course.getThemeColor()
                        | course.getTimeColor()
                        | course.getTitleColor()
                        | course.getSubTitleColor()) == 0) {
                    new Palette.Builder(((BitmapDrawable) nab).getBitmap())
                            .generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
                                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                                    if (cardView != null) {
                                        if (lightVibrant != null) {
                                            cardView.setCardBackgroundColor(lightVibrant.getRgb());
                                            backgroundIndicator.setTextColor(lightVibrant.getRgb());
                                            nameText.setTextColor(lightVibrant.getTitleTextColor());
                                            roomText.setTextColor(lightVibrant.getBodyTextColor());

                                            course.setThemeColor(lightVibrant.getRgb())
                                                    .setTitleColor(lightVibrant.getTitleTextColor())
                                                    .setSubTitleColor(lightVibrant.getBodyTextColor());
                                        } else if (vibrant != null) {
                                            cardView.setCardBackgroundColor(vibrant.getRgb());
                                            backgroundIndicator.setTextColor(vibrant.getRgb());
                                            nameText.setTextColor(vibrant.getTitleTextColor());
                                            roomText.setTextColor(vibrant.getBodyTextColor());

                                            course.setThemeColor(vibrant.getRgb())
                                                    .setTitleColor(vibrant.getTitleTextColor())
                                                    .setSubTitleColor(vibrant.getBodyTextColor());
                                        } else {
                                            int defaultColor = ContextCompat.getColor(context, R.color.cardview_light_background);
                                            cardView.setCardBackgroundColor(defaultColor);
                                            backgroundIndicator.setTextColor(defaultColor);
                                            nameText.setTextColor(defaultColor);
                                            roomText.setTextColor(defaultColor);

                                            course.setThemeColor(defaultColor)
                                                    .setTitleColor(defaultColor)
                                                    .setSubTitleColor(defaultColor);
                                        }
                                        if (vibrant != null) {
                                            timeText.setTextColor(vibrant.getRgb());
                                            course.setTimeColor(vibrant.getRgb());
                                        } else {
                                            timeText.setTextColor(ContextCompat.getColor(context, R.color.default_title));
                                            course.setTimeColor(ContextCompat.getColor(context, R.color.default_title));
                                        }
                                    }
                                }

                            });
                } else if (cardView != null) {
                    cardView.setCardBackgroundColor(course.getThemeColor());
                    backgroundIndicator.setTextColor(course.getThemeColor());
                    nameText.setTextColor(course.getTitleColor());
                    roomText.setTextColor(course.getSubTitleColor());
                    timeText.setTextColor(course.getTimeColor());
                }

            }

            if (cardView != null) {
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra(context.getString(R.string.intent_frag_index), fragId);
                        intent.putExtra(context.getString(R.string.intent_position), position);
                        intent.putExtra(context.getString(R.string.intent_lesson), course);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            intent.putExtra("color", backgroundIndicator.getTextColors().getDefaultColor());
                        else
                            intent.putExtra("color", 0xffffff);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity) context,
                                Pair.create(v, context.getString(R.string.trans_detail_item)),
                                Pair.create(v.findViewById(R.id.iv_label), context.getString(R.string.trans_detail_img)));
                        ((Activity) context).startActivityForResult(intent, MainActivity.REQ_POSITION, options.toBundle());
                    }
                });
            }
        } else if(holder instanceof SubtitleViewHolder) {
            if (MORNING_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(me.zchang.onchart.R.string.subtitle_morning));
            } else if (AFTERNOON_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(me.zchang.onchart.R.string.subtitle_afternoon));
            } else if (EVENING_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(me.zchang.onchart.R.string.subtitle_evening));
            }
        }
    }

    @Override
    public int getItemCount() {
        if(courses.size() != 0)
            return courses.size() + 1 + (morningCount > 0 ? 1 : 0)
                    + (afternoonCount > 0 ? 1 : 0)
                    + (eveningCount > 0 ? 1 : 0);
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout frame;
        CardView cardView;
        TextView nameText;
        TextView roomText;
        ImageView nabImg;
        TextView timeText;
        TextView backgroundIndicator;

        int cardHeight;
        public ViewHolder(View itemView) {
            super(itemView);
            frame = (FrameLayout) itemView.findViewById(R.id.fl_frame);
            cardView = (CardView) itemView.findViewById(R.id.cd_lesson_item);
            nameText = (TextView) itemView.findViewById(R.id.tv_lesson_name);
            roomText = (TextView) itemView.findViewById(R.id.tv_lesson_room);
            timeText = (TextView) itemView.findViewById(R.id.tv_time);
            nabImg = (ImageView) itemView.findViewById(R.id.iv_label);
            backgroundIndicator = (TextView) itemView.findViewById(R.id.iv_background_indicator);
            ViewGroup.LayoutParams params = cardView.getLayoutParams();
            params.width = (int) (screenWidth * 0.78);
            cardView.setLayoutParams(params);

            cardHeight = frame.getLayoutParams().height;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SubtitleViewHolder extends RecyclerView.ViewHolder {
        TextView subTitle;
        public SubtitleViewHolder(View itemView) {
            super(itemView);
            subTitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_TYPE_HEADER;
        else if(bitmap[position] < 0)
            return VIEW_TYPE_SUBTITLE;
        else
            return VIEW_TYPE_LIST;
    }
}
