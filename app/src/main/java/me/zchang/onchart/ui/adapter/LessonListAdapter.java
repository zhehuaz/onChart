package me.zchang.onchart.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.zchang.onchart.R;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.exception.LessonStartTimeException;
import me.zchang.onchart.parser.Utils;
import me.zchang.onchart.student.Lesson;
import me.zchang.onchart.ui.DetailActivity;
import me.zchang.onchart.ui.MainActivity;

import java.util.List;

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

public class LessonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "LessonListAdapter";
    public final static int VIEW_TYPE_HEAD = 0;
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

    List<Lesson> lessons;
    Context context;
    private int fragId;

    public LessonListAdapter(Context context, List<Lesson> lessons, int fragId) throws LessonStartTimeException {

        bitmap = new byte[20];
        this.lessons = lessons;
        this.context = context;
        this.fragId = fragId;

        processLessons();

    }

    public void setLessons(List<Lesson> lessons) throws LessonStartTimeException {
        this.lessons = lessons;
        processLessons();
    }

    public void processLessons() throws LessonStartTimeException {
        morningCount = 0;
        afternoonCount = 0;
        eveningCount = 0;
        for(Lesson l : lessons) {
            int startTime = l.getStartTime();
            switch (startTime) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    morningCount ++;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    afternoonCount ++;
                    break;
                case 11:
                case 12:
                case 13:
                    eveningCount ++;
                    break;
                default:
                    throw new LessonStartTimeException();
            }
        }

        int length = getItemCount();
        byte lessonCount = 0;
        bitmap[0] = HEAD_FLAG;
        for(int position = 1; position < length; position ++) {
            if (position == 1 && morningCount > 0) {
                bitmap[position] = MORNING_FLAG;
            } else if (position == ((morningCount + 1) * (morningCount > 0 ? 1 : 0) + 1) && afternoonCount > 0) {
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
            case VIEW_TYPE_HEAD:
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(me.zchang.onchart.R.layout.blank, parent, false));
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
        //Log.d(TAG, "On bind");
        if(holder instanceof ViewHolder) {
            final Lesson l = lessons.get(bitmap[position]);
            final TextView nameText = ((ViewHolder) holder).nameText;
            final TextView roomText = ((ViewHolder) holder).roomText;
            final TextView timeText = ((ViewHolder) holder).timeText;
            final ImageView nabImg = ((ViewHolder) holder).nabImg;
            final CardView cardView = ((ViewHolder) holder).cardView;
            nameText.setText(l.getName());
            roomText.setText(l.getClassroom());
            timeText.setText(Utils.timeFromPeriod(l.getStartTime()));

            ViewGroup.LayoutParams params = ((ViewHolder) holder).frame.getLayoutParams();
            params.height =(((ViewHolder) holder).cardHeight >> 1 ) * (l.getEndTime() - l.getStartTime() + 1);
            ((ViewHolder) holder).frame.setLayoutParams(params);

            nabImg.setImageResource(PreferenceManager.labelImgs[l.getLabelImgIndex()]);
            //nabImg.setScaleType(ImageView.);


            Drawable nab = nabImg.getDrawable();
            if(nab != null) {
                new Palette.Builder(((BitmapDrawable) nab).getBitmap())
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (cardView != null) {
                                    if (lightVibrant != null) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                            cardView.setBackground(new ColorDrawable(lightVibrant.getRgb()));
                                        else
                                            cardView.setCardBackgroundColor(lightVibrant.getRgb());
                                        //timeText.setTextColor(lightVibrant.getRgb());
                                        nameText.setTextColor(lightVibrant.getTitleTextColor());
                                        roomText.setTextColor(lightVibrant.getBodyTextColor());
                                    } else if (vibrant != null) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                            cardView.setBackground(new ColorDrawable(vibrant.getRgb()));
                                        else
                                            cardView.setBackgroundColor(lightVibrant.getRgb());
                                        //timeText.setTextColor(vibrant.getRgb());
                                        nameText.setTextColor(vibrant.getTitleTextColor());
                                        roomText.setTextColor(vibrant.getBodyTextColor());
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                            cardView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.cardview_light_background)));
                                        else
                                            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                                        nameText.setTextColor(context.getResources().getColor(R.color.default_title));
                                        roomText.setTextColor(context.getResources().getColor(R.color.default_title));
                                    }
                                    if (vibrant != null)
                                        timeText.setTextColor(vibrant.getRgb());
                                    else
                                        timeText.setTextColor(context.getResources().getColor(R.color.default_title));
                                }
                            }

                        });
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ((MainActivity) context).getSupportFragmentManager()
//                            .beginTransaction()
//                            .addSharedElement(v, context.getString(R.string.trans_detail_item))
//                            .addSharedElement(v.findViewById(R.id.iv_label), context.getString(R.string.trans_detail_img));
//                    DetailFragment fragment = new DetailFragment();
//                    fragment.setLesson(l);
//                    fragment.setPosition(position);
//                    fragment.show(((MainActivity)context).getSupportFragmentManager(), MainActivity.TAG);

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(context.getString(R.string.intent_frag_index), fragId);
                    intent.putExtra(context.getString(R.string.intent_position), position);
                    intent.putExtra(context.getString(R.string.intent_lesson), l);
                    //((Activity) context).startActivityForResult(intent, MainActivity.REQ_POSITION);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        intent.putExtra("color", ((ColorDrawable)cardView.getBackground()).getColor());
                    else
                        intent.putExtra("color", 0xffffff);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            Pair.create(v, context.getString(R.string.trans_detail_item)),
                            Pair.create(v.findViewById(R.id.iv_label), context.getString(R.string.trans_detail_img)));
                    ((Activity) context).startActivityForResult(intent, MainActivity.REQ_POSITION, options.toBundle());
                }
            });
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
        if(lessons.size() != 0)
            return lessons.size() + 1 + (morningCount > 0 ? 1 : 0)
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

        int cardHeight;
        public ViewHolder(View itemView) {
            super(itemView);
            frame = (FrameLayout) itemView.findViewById(me.zchang.onchart.R.id.fl_frame);
            cardView = (CardView) itemView.findViewById(me.zchang.onchart.R.id.cd_lesson_item);
            nameText = (TextView) itemView.findViewById(me.zchang.onchart.R.id.tv_lesson_name);
            roomText = (TextView) itemView.findViewById(me.zchang.onchart.R.id.tv_lesson_room);
            timeText = (TextView) itemView.findViewById(me.zchang.onchart.R.id.tv_time);
            nabImg = (ImageView) itemView.findViewById(me.zchang.onchart.R.id.iv_label);

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
            subTitle = (TextView) itemView.findViewById(me.zchang.onchart.R.id.tv_subtitle);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //Log.d(TAG, "Get Items.");
        if(position == 0)
            return VIEW_TYPE_HEAD;
        else if(bitmap[position] < 0)
            return VIEW_TYPE_SUBTITLE;
        else
            return VIEW_TYPE_LIST;
    }

    /**
     * Find the position of lesson in the list by id.
     * @param id ID of the lesson
     * @return the position of lesson in the list
     */
    public int findLessonById(int id) {
        if(!lessons.isEmpty()) {
            for (int i = 0; i < bitmap.length; i++) {
                if (bitmap[i] >= 0 && (lessons.get(bitmap[i]).getId() == id)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
