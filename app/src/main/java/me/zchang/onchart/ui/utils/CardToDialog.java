package me.zchang.onchart.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;

import me.zchang.onchart.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class CardToDialog extends ChangeBounds {

	private static final String PROPERTY_COLOR = "plaid:circleMorph:color";
	private static final String[] TRANSITION_PROPERTIES = {
            PROPERTY_COLOR
    };
    private @ColorInt
    int startColor = Color.TRANSPARENT;

    public CardToDialog(@ColorInt int startColor) {
        super();
        setStartColor(startColor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardToDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStartColor(@ColorInt int startColor) {
        this.startColor = startColor;
    }
//
//    @Override
//    public String[] getTransitionProperties() {
//        return TRANSITION_PROPERTIES;
//    }

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void captureStartValues(TransitionValues transitionValues) {
		super.captureStartValues(transitionValues);
		final View view = transitionValues.view;
		if (view.getWidth() <= 0 || view.getHeight() <= 0) return;
		transitionValues.values.put(PROPERTY_COLOR, view.getBackground());
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void captureEndValues(TransitionValues transitionValues) {
		super.captureEndValues(transitionValues);
		final View view = transitionValues.view;
		if (view.getWidth() <= 0 || view.getHeight() <= 0) return;
		transitionValues.values.put(PROPERTY_COLOR, view.getBackground());
//               ContextCompat.getColor(view.getContext(), R.color.super_light_grey));
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public Animator createAnimator(final ViewGroup sceneRoot,
	                               TransitionValues startValues,
	                               final TransitionValues endValues) {
		Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
		if (startValues == null || endValues == null || changeBounds == null) return null;

		Drawable startColor = (Drawable) startValues.values.get(PROPERTY_COLOR);
		Drawable endColor = (Drawable) endValues.values.get(PROPERTY_COLOR);

		if (startColor == null || endColor == null) return null;

		//MorphDrawable background = new MorphDrawable(startColor, 0);
		//endValues.view.setBackground(new ColorDrawable(Color.parseColor("#ff0000")));
		//Animator color = ObjectAnimator.ofArgb(background, background.COLOR, endColor);
		AnimatorSet transition = new AnimatorSet();
		//transition.playTogether(changeBounds, color);
		transition.setDuration(180);
		transition.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(),
				android.R.interpolator.fast_out_slow_in));

		return transition;
//	    return changeBounds;
    }

//    @Override
//    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
//        return super.createAnimator(sceneRoot, startValues, endValues);
//    }
}