package br.com.alimentar.alergia.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.SignInButton;
import com.nineoldandroids.animation.Animator;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Created by gilmar on 10/10/16.
 */

public class AnimationUtils {
    private static String status = "VISIBLE";

    public static void setVisible(final View view, final int height, final boolean visibilidade, float alphaVisible) {
        //1F
        animate(view).yBy(height).alpha(alphaVisible).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                if (visibilidade)
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void setGone(final View view, int height ,final boolean visibilidade,float alphaVisible) {
        //0F
        animate(view).yBy(-height).alpha(alphaVisible).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                if (visibilidade)
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void setAnimationVisible(SignInButton googleBtn, RelativeLayout reLayoutOU, LinearLayout ll) {
        int height = googleBtn.getHeight() + reLayoutOU.getHeight() + 70;
        if (status == "INVISIBLE") {
            animate(googleBtn).alpha(1F).setDuration(800).start();
            animate(reLayoutOU).alpha(1F).setDuration(800).start();
            animate(ll).yBy(height).setDuration(500).start();
            status = "VISIBLE";
        }
    }

    public static void setAnimationGone(SignInButton googleBtn, RelativeLayout reLayoutOU, LinearLayout ll) {
        int height = googleBtn.getHeight() + reLayoutOU.getHeight() + 70;
        if (status == "VISIBLE") {
            animate(googleBtn).alpha(0F).setDuration(270).start();
            animate(reLayoutOU).alpha(0F).setDuration(270).start();
            animate(ll).yBy(-height).setDuration(500).start();
            status = "INVISIBLE";
        }
    }
}


