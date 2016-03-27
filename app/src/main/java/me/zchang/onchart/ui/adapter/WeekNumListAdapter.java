/*
 *     Copyright 2016 Zhehua Chang
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package me.zchang.onchart.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.jar.Attributes;

import me.zchang.onchart.session.events.SwitchWeekNumEvent;

public class WeekNumListAdapter extends RecyclerView.Adapter {

    private final static int WEEK_COUNT = 32;
    Context context;

    public WeekNumListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView newTextView = new TextView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTextView.setLayoutParams(params);
        return new WeekNumViewHolder(newTextView);
    }

    int lastSelectPos = -1;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        WeekNumViewHolder holder = (WeekNumViewHolder) viewHolder;
        holder.text.setText(position + "");
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchWeekNumEvent(position));
//                v.setBackgroundColor(0xcd898989);
//                if (lastSelectPos != -1) {
//
//                }
//                lastSelectPos = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return WEEK_COUNT;
    }

    private class WeekNumViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public WeekNumViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
            text.setTextSize(30);
            text.setTextColor(0xcddddddd);
            text.setPadding(20, 20, 20, 20);
        }
    }
}
