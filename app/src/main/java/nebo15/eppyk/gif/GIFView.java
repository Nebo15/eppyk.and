package nebo15.eppyk.gif;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import nebo15.eppyk.R;

public class GIFView extends View {
    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private GIFObject mGifObject;

    private long mMovieStart;
    private int mCurrentAnimationTime = 0;
    private int mCurrentFrame = 0;
    private int mCurrentLoop = 0;
    private IGIFEvent handler;

    /**
     * Position for drawing animation frames in the center of the view.
     */
    private float mLeft;
    private float mTop;

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
         mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime;
        invalidate();

        if (handler != null)
            handler.gifAnimationDidStart(mGifObject);
    }

    public void stop() {
        this.mPaused = true;

        if (handler != null)
            handler.gifAnimationDidStop(mGifObject);
    }


    public boolean isPaused() {
        return this.mPaused;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mGifObject != null && mGifObject.movie != null) {
            int movieWidth = mGifObject.movie.width();
            int movieHeight = mGifObject.movie.height();

			/*
			 * Calculate horizontal scaling
			 */
            float scaleH = 1f;
            int measureModeWidth = MeasureSpec.getMode(widthMeasureSpec);

            if (measureModeWidth != MeasureSpec.UNSPECIFIED) {
                int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
                if (movieWidth > maximumWidth) {
                    scaleH = (float) movieWidth / (float) maximumWidth;
                }
            }

			/*
			 * calculate vertical scaling
			 */
            float scaleW = 1f;
            int measureModeHeight = MeasureSpec.getMode(heightMeasureSpec);

            if (measureModeHeight != MeasureSpec.UNSPECIFIED) {
                int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
                if (movieHeight > maximumHeight) {
                    scaleW = (float) movieHeight / (float) maximumHeight;
                }
            }

			/*
			 * calculate overall scale
			 */
            mScale = 1f / Math.max(scaleH, scaleW);

            mMeasuredMovieWidth = (int) (movieWidth * mScale);
            mMeasuredMovieHeight = (int) (movieHeight * mScale);

            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);

        } else {
			/*
			 * No movie set, just set minimum available size.
			 */
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

		/*
		 * Calculate left / top for drawing in center
		 */
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;

        mVisible = getVisibility() == View.VISIBLE;
    }

    int f = 0;
    @Override
    protected void onDraw(Canvas canvas) {
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

   private void updateAnimationTime() {
        int dur = mGifObject.framesCount * 60;
//       Log.i("GIF", String.format("Duration %d", dur) );
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }

       mCurrentAnimationTime = (mCurrentFrame * 30);
//       Log.i("GIF", String.format("%d - %d", mCurrentFrame, mCurrentAnimationTime) );
    }

    /**
     * Draw current GIF frame
     *
     */
    private void drawMovieFrame(Canvas canvas) {

        mCurrentFrame++;
        if (mCurrentFrame == mGifObject.framesCount) {
            finishLoop();
        }

        mGifObject.movie.setTime(mCurrentAnimationTime);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScale, mScale);
        mGifObject.movie.draw(canvas, mLeft / mScale, mTop / mScale);
        canvas.restore();

        if (handler != null)
            handler.gifAnimationShowFrame(mGifObject, mCurrentFrame);
    }

    private void finishLoop() {
        mCurrentLoop++;
        mCurrentFrame = 0;

        if (mGifObject.loopsCount != 0) {
            if (mCurrentLoop == mGifObject.loopsCount) {
                mCurrentFrame = mGifObject.framesCount-1;
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