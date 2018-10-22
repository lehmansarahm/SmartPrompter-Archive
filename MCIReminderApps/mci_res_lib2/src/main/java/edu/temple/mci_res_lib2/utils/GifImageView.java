package edu.temple.mci_res_lib2.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import edu.temple.mci_res_lib2.R;

public class GifImageView extends View {

    public Movie mMovie;
    public long mMovieStart;

    public GifImageView(Context context) {
        super(context);
        initializeView();
    }

    public GifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView();
    }

    private void initializeView() {
        InputStream is = getContext().getResources().openRawResource(R.raw.high_five_puppy);
        mMovie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) mMovieStart = now;

        if (mMovie != null) {
            int relTime = (int) ((now - mMovieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            this.invalidate();
        }
    }

}