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
package zchang.me.uilibrary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 *
 */
public class BounceList extends RecyclerView {
	public final static String TAG = "BounceList";

	public BounceList(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutManager(new LinearLayoutManager(context));
	}

	float deltaY = 0;
	float amountY = 0;
	int courseCount = 0;

	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent) {
		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				//Log.i(TAG, "ACTION DOWN");
				courseCount = getChildCount();
				break;
			case MotionEvent.ACTION_MOVE:
				//Log.i(TAG, "ACTION MOVE");
				if (((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition() == getAdapter().getItemCount() - 1
						|| ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0) {
					if (motionEvent.getHistorySize() > 0) {
						// is moving
						// add deltaY to amountY
						deltaY = motionEvent.getY(motionEvent.getActionIndex())
								- motionEvent.getHistoricalY(motionEvent.getActionIndex(), motionEvent.getHistorySize() - 1);
						amountY += deltaY;
						for (int i = 1; i < courseCount; i++) {
							View childView = getChildAt(i);
							if (childView != null) {
								int position = amountY > 0 ? i : courseCount - i;
								float deltaYi =
										((position / 6.f) * (float) Math.atan(amountY / getHeight() * 10f) * getHeight() / 1.6f);
								childView.setTranslationY(deltaYi / 6.f);
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				//Log.i(TAG, "ACTION UP");
				amountY = 0;
				Interpolator interpolator = new AccelerateDecelerateInterpolator();
				for (int i = 1; i < getChildCount(); i++) {
					View childView = getChildAt(i);
                        if (childView != null) {
                            childView.animate()
								.translationY(0)
								.setInterpolator(interpolator)
								.setDuration(180)
								.setStartDelay(50);
					}
				}
				break;
		}
		return super.dispatchTouchEvent(motionEvent);
	}

	@Override
	public void onViewAdded(View child) {
		super.onViewAdded(child);
	}

	@Override
	public void onViewRemoved(View child) {
		super.onViewRemoved(child);
	}
}
