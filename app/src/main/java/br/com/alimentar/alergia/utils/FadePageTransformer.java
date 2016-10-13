package br.com.alimentar.alergia.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by gilmar on 09/10/16.
 */

public class FadePageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);

        if(position <= -1.0F || position >= 1.0F) {
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setAlpha(1.0F - Math.abs(position));
        }
    }
    /*public void transformPage(View view, float position) {

        if (position <= -1.0F || position >= 1.0F) {        // [-Infinity,-1) OR (1,+Infinity]
            view.setAlpha(0.0F);
            view.setVisibility(View.GONE);
        } else if( position == 0.0F ) {     // [0]
            view.setAlpha(1.0F);
            view.setVisibility(View.VISIBLE);
        } else {

            // Position is between [-1,1]
            view.setAlpha(1.0F - Math.abs(position));
            view.setTranslationX(-position * (view.getWidth() / 2));
            view.setVisibility(View.VISIBLE);
        }
    }

    public void transformPage(View view, float position) {
        if(position <= -1.0F || position >= 1.0F) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(1.0F - Math.abs(position));
        }
    }*/
}