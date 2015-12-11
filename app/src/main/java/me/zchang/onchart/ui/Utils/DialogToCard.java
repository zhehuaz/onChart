package me.zchang.onchart.ui.Utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;

/**
 * Created by Administrator on 2015/12/11.
 */
@TargetApi(19)
public class DialogToCard extends ChangeBounds {
    @Override
    public String[] getTransitionProperties() {
        return super.getTransitionProperties();
    }

    @TargetApi(21)
    public DialogToCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogToCard() {
        super();
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || changeBounds == null) return null;

        // ease in the dialog's child views (slide up & fade_fast in)
        if (endValues.view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) endValues.view;
            float offset = vg.getHeight() / 3;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = vg.getChildAt(i);
                v.setTranslationY(offset);
                v.setAlpha(0f);
                v.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(150)
                        .setStartDelay(150)
                        .setInterpolator(AnimationUtils.loadInterpolator(vg.getContext(),
                                android.R.interpolator.fast_out_slow_in));
                offset *= 1.8f;
            }
        }

        AnimatorSet transition = new AnimatorSet();
        transition.playTogether(changeBounds);
        transition.setDuration(300);
        transition.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(),
                android.R.interpolator.fast_out_slow_in));
        return transition;
    }
}
