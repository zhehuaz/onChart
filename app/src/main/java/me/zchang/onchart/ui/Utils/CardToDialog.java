package me.zchang.onchart.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import me.zchang.onchart.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class CardToDialog extends ChangeBounds {

    private static final String PROPERTY_COLOR = "property_color";
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

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) return;
        transitionValues.values.put(PROPERTY_COLOR, startColor);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) return;
        transitionValues.values.put(PROPERTY_COLOR,
               ContextCompat.getColor(view.getContext(), R.color.super_light_grey));
        //transitionValues.values.put(PROPERTY_COLOR, ((ColorDrawable)view.getBackground()).getColor());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   final TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || changeBounds == null) return null;

        Integer startColor = (Integer) startValues.values.get(PROPERTY_COLOR);
        Integer endColor = (Integer) endValues.values.get(PROPERTY_COLOR);

        if (startColor == null || endColor == null) return null;

        MorphDrawable background = new MorphDrawable(startColor, 0);
        endValues.view.setBackground(background);

        Animator color = ObjectAnimator.ofArgb(background, background.COLOR, endColor);

        // ease in the dialog's child views (slide up & fade_fast in)
//        if (endValues.view instanceof ViewGroup) {
//            ViewGroup vg = (ViewGroup) endValues.view;
//            //float offset = vg.getHeight() / 3;
//            for (int i = 0; i < vg.getChildCount(); i++) {
//                View v = vg.getChildAt(i);
//                //v.setTranslationY(offset);
//                if (v.getId() != R.id.iv_label) {
//                    v.setAlpha(0f);
//                    v.animate()
//                            .alpha(1f)
//                                    //.translationY(0f)
//                            .setDuration(250)
//                            .setStartDelay(0)//;
//                            .setInterpolator(AnimationUtils.loadInterpolator(vg.getContext(),
//                                    android.R.interpolator.fast_out_slow_in));
//                }
//                //offset *= 1.8f;
//            }
//        }

        AnimatorSet transition = new AnimatorSet();
        transition.playTogether(changeBounds, color);
        transition.setDuration(180);
        transition.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(),
                android.R.interpolator.fast_out_slow_in));
        return transition;
    }

}