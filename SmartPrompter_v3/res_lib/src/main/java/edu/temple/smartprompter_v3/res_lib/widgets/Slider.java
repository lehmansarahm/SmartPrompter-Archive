package edu.temple.smartprompter_v3.res_lib.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class Slider extends androidx.appcompat.widget.AppCompatSeekBar {

    private Drawable mThumb;

    public Slider(Context context) {
        super(context);

    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (event.getX() >= mThumb.getBounds().left
                    && event.getX() <= mThumb.getBounds().right
                    && event.getY() <= mThumb.getBounds().bottom
                    && event.getY() >= mThumb.getBounds().top) {

                super.onTouchEvent(event);
            } else {
                return false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return false;
        } else {
            super.onTouchEvent(event);
        }

        return true;
    }

}