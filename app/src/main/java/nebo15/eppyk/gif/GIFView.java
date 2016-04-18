package nebo15.eppyk.gif;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import nebo15.eppyk.R;

public class GIFView extends View {
    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private GIFObject mGifObject;

    private int mCurrentAnimationTime = 0;
    private int mCurrentFrame = 0;
    private int mCurrentLoop = 0;
    private IGIFEvent handler;



    /**
     * Animation speed
     */
    private final int sourceImageFrameDuration = 60;
    private final int animationFrameDuration = 50;

    /**
     * Scaling factor to fit the animation within view bounds.
     */
    private float mScale;

    /**
     * Scaled movie frames width and height.
     */
    private int mMeasuredMovieWidth;
    private int mMeasuredMovieHeight;

    private volatile boolean mPaused = true;
    private boolean mVisible = true;

    public GIFView(Context context) {
        this(context, null);
    }

    public GIFView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifMoviewViewStyle);
    }

    public GIFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setViewAttributes(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {

        /**
         * Starting from HONEYCOMB have to turn off HW acceleration to draw
         * Movie on Canvas.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifMoviewView, defStyle,
                R.style.Widget_GifMoviewView);

        mMovieResourceId = array.getResourceId(R.styleable.GifMoviewView_gif, -1);
        mPaused = array.getBoolean(R.styleable.GifMoviewView_paused, true);

        array.recycle();

        if (mMovieResourceId != -1) {
            int framesCount = array.getResourceId(R.styleable.GifMoviewView_framesCount, 0);
            mGifObject = new GIFObject(getResources().openRawResource(mMovieResourceId), framesCount);
        }
    }

    public void setGifResource(int movieResId, int frameCount) {
        this.mMovieResourceId = movieResId;
        mGifObject = new GIFObject(getResources().openRawResource(mMovieResourceId), frameCount);
        requestLayout();
    }

    public void setGif(GIFObject gifObject) {
        this.mGifObject = gifObject;
        mCurrentFrame = 0;
        mCurrentLoop = 0;
        stop();
        requestLayout();
    }

    public GIFObject getGif() {
        return mGifObject;
    }

    public void setHandler(IGIFEvent handler) {
        this.handler = handler;
    }

    public void setGifTime(int time) {
        mCurrentAnimationTime = time;
        invalidate();
    }

    public void play() {
        this.mPaused = false;
        mMovieStart = 0;
        invalidate();

        if (handler != null)
            handler.gifAnimationDidStart(mGifObject);
    }

    public void stop() {
        this.mPaused = true;

        if (handler != null)
            handler.gifAnimationDidStop(mGifObject);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
        
        if (mGifObject != null && mGifObject.movie != null) {
            if (!mPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    /**
     * Invalidates view only if it is visible.
     * <br>
     * {@link #postInvalidateOnAnimation()} is used for Jelly Bean and higher.
     *
     */
    @SuppressLint("NewApi")
    private void invalidateView() {
        if(mVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }

    /**
     * Calculate current animation time
     *
     */

//    private void updateAnimationTime() {
//        long now = android.os.SystemClock.uptimeMillis();
//
//        if (mMovieStart == 0) {
//            mMovieStart = now;
//        }
//
//        int dur = mGifObject.framesCount * 60;
//
//        if (dur == 0) {
//            dur = DEFAULT_MOVIEW_DURATION;
//        }
//
//        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
//    }

    private long mMovieStart;
    private void updateAnimationTime() {
       long now = android.os.SystemClock.uptimeMillis();

       if (mMovieStart == 0) {
           mMovieStart = now;
       }

        mCurrentAnimationTime = (mCurrentFrame * sourceImageFrameDuration);
    }

    /**
     * Draw current GIF frame
     *
     */
    private void drawMovieFrame(Canvas canvas) {

        long now = android.os.SystemClock.uptimeMillis();

        mCurrentFrame = (int)(now - mMovieStart) / animationFrameDuration;
        if (mCurrentFrame >= mGifObject.framesCount) {
            finishLoop();
        }

        mGifObject.movie.setTime(mCurrentAnimationTime);

        float width = this.getWidth();
        float height= this.getHeight();

        float gifWidth = mGifObject.movie.width();
        float gifHeight = mGifObject.movie.height();
        mScale = Math.max(width / gifWidth, height / gifHeight);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.scale(mScale, mScale);
        mGifObject.movie.draw(canvas, 0, 0);
        canvas.restore();


        if (handler != null)
            handler.gifAnimationShowFrame(mGifObject, mCurrentFrame);
    }


    private void finishLoop() {
        mMovieStart = 0;
        mCurrentLoop++;
        mCurrentFrame = 0;

        if (mGifObject.loopsCount != 0) {
            if (mCurrentLoop == mGifObject.loopsCount) {
                mCurrentFrame = mGifObject.framesCount-1;
                mCurrentAnimationTime = mGifObject.framesCount * 60;
                finishAnimation();
            }
        }
        if (handler != null)
            handler.gifAnimationDidFinishLoop(mGifObject, mCurrentLoop);
    }

    private void finishAnimation() {
        this.stop();

        if (handler != null)
            handler.gifAnimationDidFinish(mGifObject);
    }


    @SuppressLint("NewApi")
    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }
}