package org.oo.onchart.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.oo.onchart.R;
import org.oo.onchart.student.Lesson;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by langley on 11/17/15.
 */
public class LessonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int VIEW_TYPE_HEAD = 0;
    public final static int VIEW_TYPE_LIST = 1;
    List<Lesson> lessons;
    Context context;

    public LessonListAdapter(Context context, List<Lesson> lessons) {
        this.lessons = lessons;
        this.context = context;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEAD:
                return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.blank, parent, false));
            case VIEW_TYPE_LIST:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cd_lesson_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ((ViewHolder) holder).nameText.setText(lessons.get(position - 1).getName());
            ((ViewHolder) holder).roomText.setText(lessons.get(position - 1).getClassroom());
        }
    }

    @Override
    public int getItemCount() {
        return lessons.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameText;
        TextView roomText;
        ImageView nabImg;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cd_lesson_item);
            nameText = (TextView) itemView.findViewById(R.id.tv_lesson_name);
            roomText = (TextView) itemView.findViewById(R.id.tv_lesson_room);
            nabImg = (ImageView) itemView.findViewById(R.id.iv_nab);

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

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? VIEW_TYPE_LIST : VIEW_TYPE_HEAD;
    }
}
