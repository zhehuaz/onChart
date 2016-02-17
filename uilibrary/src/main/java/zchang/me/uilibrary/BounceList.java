package zchang.me.uilibrary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by langley on 2/17/16.
 */
public class BounceList extends RecyclerView {
	public final static String TAG = "BounceList";

	public BounceList(Context context) {
		super(context);
		setLayoutManager(new LinearLayoutManager(context));
	}

	public BounceList(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutManager(new LinearLayoutManager(context));
		setOnTouchListener(new MyOnTouchListener());
	}

	public BounceList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayoutManager(new LinearLayoutManager(context));
		setOnTouchListener(new MyOnTouchListener());
	}

	private class MyOnTouchListener implements OnTouchListener {
		float firstY = 0;
		float curY = 0;
		float deltaY = 0;
		boolean isMoving = false;
		int courseCount = 0;

		@Override
		public boolean onTouch(View v, MotionEvent motionEvent) {
			int action = motionEvent.getActionMasked();
			Log.i(TAG, "Current action is " + action);
			if (action == 6) {
				int index = MotionEvent.ACTION_POINTER_INDEX_MASK & motionEvent.getAction() >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				Log.e(TAG, "index is " + index);
			}
			switch (motionEvent.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					isMoving = false;
					break;
				case MotionEvent.ACTION_MOVE:
					if (((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition() == getAdapter().getItemCount() - 1
							|| ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0) {
						if (!isMoving) {
							// start to move
							isMoving = true;
							courseCount = BounceList.this.getChildCount();
							curY = motionEvent.getY();
							firstY = curY;
						} else {
							// is moving
							curY = motionEvent.getY();
							deltaY = curY - firstY;
							for (int i = 1; i < courseCount; i++) {
								View childView = getChildAt(i);
								//Log.i(TAG, String.format("translationY is %f", childView.getTranslationY()));
								int position;
								if (deltaY > 0)
									position = i;
								else
									position = courseCount - i;
								float deltaYi = (float) ((position / 5.f) * Math.atan(deltaY / getHeight() * 2f) * getHeight() / 1f);
								Log.d(TAG, String.format("deltaY %d is %f", i, deltaYi));
								int offset = (int) (deltaYi / 16);
								Log.d(TAG, String.format("offset %d is %d", i, offset));
								if (childView != null)
									childView.setTranslationY(offset);
							}
						}
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					if (isMoving) {
						deltaY = 0;
						for (int i = 1; i < courseCount; i++) {
							final View childView = BounceList.this.getChildAt(i);
							if (childView != null) {
								childView.animate()
										.setInterpolator(new AccelerateDecelerateInterpolator())
										.translationY(0)
										.setDuration(180)
										.setStartDelay(20);
							}
						}
					}
					isMoving = false;
					break;
			}
			return false;
		}
	}
}
