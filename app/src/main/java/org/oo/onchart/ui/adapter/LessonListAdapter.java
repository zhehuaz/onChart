package org.oo.onchart.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.oo.onchart.R;
import org.oo.onchart.exception.LessonStartTimeException;
import org.oo.onchart.student.Lesson;

import java.util.List;

/**
 * Created by langley on 11/17/15.
 */
public class LessonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "LessonListAdapter";
    public final static int VIEW_TYPE_HEAD = 0;
    public final static int VIEW_TYPE_LIST = 1;
    public final static int VIEW_TYPE_SUBTITLE = 2;

    public final static int MORNING_FLAG = -1;
    public final static int AFTERNOON_FLAG = -2;
    public final static int EVENING_FLAG = -3;

    private int morningCount;
    private int afternoonCount;
    private int eveningCount;
    private byte[] bitmap;

    List<Lesson> lessons;
    Context context;

    public LessonListAdapter(Context context, List<Lesson> lessons) throws LessonStartTimeException {


        bitmap = new byte[20];
        this.lessons = lessons;
        this.context = context;

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
                case 3:
                    morningCount ++;
                    break;
                case 6:
                case 8:
                    afternoonCount ++;
                    break;
                case 11:
                    eveningCount ++;
                    break;
                default:
                    throw new LessonStartTimeException();
            }
        }

        int length = getItemCount();
        byte lessonCount = 0;
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
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.blank, parent, false));
            case VIEW_TYPE_LIST:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cd_lesson_item, parent, false));
            case VIEW_TYPE_SUBTITLE:
                return new SubtitleViewHolder(LayoutInflater.from(context).inflate(R.layout.list_subtitle, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "On bind");
        if(holder instanceof ViewHolder) {
            Lesson l = lessons.get(bitmap[position]);
            ((ViewHolder) holder).nameText.setText(l.getName());
            ((ViewHolder) holder).roomText.setText(l.getClassroom());

            ViewGroup.LayoutParams params = ((ViewHolder) holder).frame.getLayoutParams();
            params.height =(((ViewHolder) holder).cardHeight >> 1 ) * (l.getEndTime() - l.getStartTime() + 1);
            ((ViewHolder) holder).frame.setLayoutParams(params);
        } else if(holder instanceof SubtitleViewHolder) {
            if (MORNING_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(R.string.subtitle_morning));
            } else if (AFTERNOON_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(R.string.subtitle_afternoon));
            } else if (EVENING_FLAG == bitmap[position]) {
                ((SubtitleViewHolder) holder).subTitle.setText(context.getResources().getString(R.string.subtitle_evening));
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

        int cardHeight;
        public ViewHolder(View itemView) {
            super(itemView);
            frame = (FrameLayout) itemView.findViewById(R.id.fl_frame);
            cardView = (CardView) itemView.findViewById(R.id.cd_lesson_item);
            nameText = (TextView) itemView.findViewById(R.id.tv_lesson_name);
            roomText = (TextView) itemView.findViewById(R.id.tv_lesson_room);
            nabImg = (ImageView) itemView.findViewById(R.id.iv_nab);

            cardHeight = frame.getLayoutParams().height;

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
                                    cardView.setCardBackgroundColor(lightVibrant.getRgb());
                                } else if (vibrant != null) {
                                    cardView.setCardBackgroundColor(vibrant.getRgb());
                                } else {
                                    cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                }
                            }
                            if (nameText != null && roomText != null) {
                                if (lightVibrant != null) {
                                    nameText.setTextColor(lightVibrant.getTitleTextColor());
                                    roomText.setTextColor(lightVibrant.getTitleTextColor());

                                } else if (vibrant != null) {
                                    nameText.setTextColor(vibrant.getTitleTextColor());
                                    roomText.setTextColor(vibrant.getTitleTextColor());
                                } else {
                                    nameText.setTextColor(Color.parseColor("#DCDCDC"));
                                    roomText.setTextColor(Color.parseColor("#DCDCDC"));
                                }
                            }
                        }
                    });
            }
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
        Log.d(TAG, "Get Items.");
        if(position == 0)
            return VIEW_TYPE_HEAD;
        else if(bitmap[position] < 0)
            return VIEW_TYPE_SUBTITLE;
        else
            return VIEW_TYPE_LIST;
    }


}
