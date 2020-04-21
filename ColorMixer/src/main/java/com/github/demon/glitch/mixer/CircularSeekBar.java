package com.github.demon.glitch.mixer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircularSeekBar extends View {
    private static final float DEFAULT_CIRCLE_X_RADIUS = 30f;
    private static final float DEFAULT_CIRCLE_Y_RADIUS = 30f;
    private static final float DEFAULT_POINTER_RADIUS = 7f;
    private static final float DEFAULT_POINTER_HALO_WIDTH = 6f;
    private static final float DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 5f;
    private static final float DEFAULT_START_ANGLE = 270f;
    private static final float DEFAULT_END_ANGLE = 270f;
    private static final int DEFAULT_MAX = 255;
    private static final int DEFAULT_PROGRESS = 255;
    private static final int DEFAULT_CIRCLE_COLOR = Color.DKGRAY;
    private static final int DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 255, 255, 255);
    private static final int DEFAULT_POINTER_COLOR = Color.argb(235, 255, 255, 255);
    private static final int DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 255,255, 255);
    private static final int DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 200, 200, 200);
    private static final int DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_POINTER_ALPHA = 135;
    private static final int DEFAULT_POINTER_ALPHA_ONTOUCH = 100;
    private static final boolean DEFAULT_USE_CUSTOM_RADII = false;
    private static final boolean DEFAULT_MAINTAIN_EQUAL_CIRCLE = true;
    private static final boolean DEFAULT_MOVE_OUTSIDE_CIRCLE = false;
    private static final boolean DEFAULT_LOCK_ENABLED = true;
    private final float DPTOPX_SCALE = getResources().getDisplayMetrics().density;
    private final float MIN_TOUCH_TARGET_DP = 48;
    private Paint mCirclePaint;
    private Paint mCircleFillPaint;
    private Paint mCircleProgressPaint;
    private Paint mCircleProgressGlowPaint;
    private Paint mPointerPaint;
    private Paint mPointerHaloPaint;
    private Paint mPointerHaloBorderPaint;
    private float mCircleStrokeWidth;
    private float mCircleXRadius;
    private float mCircleYRadius;
    private float mPointerRadius;
    private float mPointerHaloWidth;
    private float mPointerHaloBorderWidth;
    private float mStartAngle;
    private float mEndAngle;
    private RectF mCircleRectF = new RectF();
    private int mPointerColor = DEFAULT_POINTER_COLOR;
    private int mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
    private int mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;
    private int mCircleColor = DEFAULT_CIRCLE_COLOR;
    private int mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
    private int mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;
    private int mPointerAlpha = DEFAULT_POINTER_ALPHA;
    private int mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;
    private float mTotalCircleDegrees;
    private float mProgressDegrees;
    private Path mCirclePath;
    private Path mCircleProgressPath;
    private int mMax;
    private int mProgress;
    private boolean mCustomRadii;
    private boolean mMaintainEqualCircle;
    private boolean mMoveOutsideCircle;
    private boolean lockEnabled = true;
    private boolean lockAtStart = true;
    private boolean lockAtEnd = false;
    private boolean mUserIsMovingPointer = false;
    private float cwDistanceFromStart;
    private float ccwDistanceFromStart;
    private float cwDistanceFromEnd;
    @SuppressWarnings("unused")
    private float ccwDistanceFromEnd;
    private float lastCWDistanceFromStart;
    private float cwDistanceFromPointer;
    private float ccwDistanceFromPointer;
    private boolean mIsMovingCW;
    private float mCircleWidth;
    private float mCircleHeight;
    private float mPointerPosition;
    private float[] mPointerPositionXY = new float[2];
    private OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

    public CircularSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void initAttributes(TypedArray attrArray) {
        mCircleXRadius = attrArray.getFloat(R.styleable.CircularSeekBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS) * DPTOPX_SCALE;
        mCircleYRadius = attrArray.getFloat(R.styleable.CircularSeekBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS) * DPTOPX_SCALE;
        mPointerRadius = attrArray.getFloat(R.styleable.CircularSeekBar_pointer_radius, DEFAULT_POINTER_RADIUS) * DPTOPX_SCALE;
        mPointerHaloWidth = attrArray.getFloat(R.styleable.CircularSeekBar_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH) * DPTOPX_SCALE;
        mPointerHaloBorderWidth = attrArray.getFloat(R.styleable.CircularSeekBar_pointer_halo_border_width, DEFAULT_POINTER_HALO_BORDER_WIDTH) * DPTOPX_SCALE;
        mCircleStrokeWidth = attrArray.getFloat(R.styleable.CircularSeekBar_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH) * DPTOPX_SCALE;
        String tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_color);
        if (tempColor != null) {
            try {
                mPointerColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mPointerColor = DEFAULT_POINTER_COLOR;
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color);
        if (tempColor != null) {
            try {
                mPointerHaloColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color_ontouch);
        if (tempColor != null) {
            try {
                mPointerHaloColorOnTouch = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_color);
        if (tempColor != null) {
            try {
                mCircleColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mCircleColor = DEFAULT_CIRCLE_COLOR;
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_progress_color);
        if (tempColor != null) {
            try {
                mCircleProgressColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;
            }
        }
        tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_fill);
        if (tempColor != null) {
            try {
                mCircleFillColor = Color.parseColor(tempColor);
            } catch (IllegalArgumentException e) {
                mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
            }
        }
        mPointerAlpha = Color.alpha(mPointerHaloColor);
        mPointerAlphaOnTouch = attrArray.getInt(R.styleable.CircularSeekBar_pointer_alpha_ontouch, DEFAULT_POINTER_ALPHA_ONTOUCH);
        if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0) {
            mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;
        }
        mMax = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX);
        mProgress = attrArray.getInt(R.styleable.CircularSeekBar_progress, DEFAULT_PROGRESS);
        mCustomRadii = attrArray.getBoolean(R.styleable.CircularSeekBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII);
        mMaintainEqualCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE);
        mMoveOutsideCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE);
        lockEnabled = attrArray.getBoolean(R.styleable.CircularSeekBar_lock_enabled, DEFAULT_LOCK_ENABLED);
        mStartAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_start_angle), DEFAULT_START_ANGLE) % 360f)) % 360f);
        mEndAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_end_angle), DEFAULT_END_ANGLE) % 360f)) % 360f);
        if (mStartAngle == mEndAngle) {
            mEndAngle = mEndAngle - .1f;
        }
    }
    private void initPaints() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCircleFillPaint = new Paint();
        mCircleFillPaint.setAntiAlias(true);
        mCircleFillPaint.setDither(true);
        mCircleFillPaint.setColor(mCircleFillColor);
        mCircleFillPaint.setStyle(Paint.Style.FILL);
        mCircleProgressPaint = new Paint();
        mCircleProgressPaint.setAntiAlias(true);
        mCircleProgressPaint.setDither(true);
        mCircleProgressPaint.setColor(mCircleProgressColor);
        mCircleProgressPaint.setStrokeWidth(mCircleStrokeWidth);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);
        mCircleProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mCircleProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mCircleProgressGlowPaint = new Paint();
        mCircleProgressGlowPaint.set(mCircleProgressPaint);
        mCircleProgressGlowPaint.setMaskFilter(new BlurMaskFilter((5f * DPTOPX_SCALE), BlurMaskFilter.Blur.NORMAL));
        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setDither(true);
        mPointerPaint.setStyle(Paint.Style.FILL);
        mPointerPaint.setColor(mPointerColor);
        mPointerPaint.setStrokeWidth(mPointerRadius);
        mPointerHaloPaint = new Paint();
        mPointerHaloPaint.set(mPointerPaint);
        mPointerHaloPaint.setColor(mPointerHaloColor);
        mPointerHaloPaint.setAlpha(mPointerAlpha);
        mPointerHaloPaint.setStrokeWidth(mPointerRadius + mPointerHaloWidth);
        mPointerHaloBorderPaint = new Paint();
        mPointerHaloBorderPaint.set(mPointerPaint);
        mPointerHaloBorderPaint.setStrokeWidth(mPointerHaloBorderWidth);
        mPointerHaloBorderPaint.setStyle(Paint.Style.STROKE);
    }
    private void calculateTotalDegrees() {
        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f; // Length of the entire circle/arc
        if (mTotalCircleDegrees <= 0f) {
            mTotalCircleDegrees = 360f;
        }
    }
    private void calculateProgressDegrees() {
        mProgressDegrees = mPointerPosition - mStartAngle; // Verified
        mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
    }
    private void calculatePointerAngle() {
        float progressPercent = ((float) mProgress / (float) mMax);
        mPointerPosition = (progressPercent * mTotalCircleDegrees) + mStartAngle;
        mPointerPosition = mPointerPosition % 360f;
    }

    private void calculatePointerXYPosition() {
        PathMeasure pm = new PathMeasure(mCircleProgressPath, false);
        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
        if (!returnValue) {
            pm = new PathMeasure(mCirclePath, false);
            returnValue = pm.getPosTan(0, mPointerPositionXY, null);
        }
    }
    private void initPaths() {
        mCirclePath = new Path();
        mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees);
        mCircleProgressPath = new Path();
        mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees);
    }
    private void initRects() {
        mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);
        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressGlowPaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);
        canvas.drawPath(mCirclePath, mCircleFillPaint);
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth, mPointerHaloPaint);
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius, mPointerPaint);
        if (mUserIsMovingPointer) {
            canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth + (mPointerHaloBorderWidth / 2f), mPointerHaloBorderPaint);
        }
    }
    public int getProgress() {
        int progress = Math.round((float) mMax * mProgressDegrees / mTotalCircleDegrees);
        return progress;
    }
    public void setProgress(int progress) {
        if (mProgress != progress) {
            mProgress = progress;
            if (mOnCircularSeekBarChangeListener != null) {
                mOnCircularSeekBarChangeListener.onProgressChanged(this, progress, false);
            }
            recalculateAll();
            invalidate();
        }
    }
    private void setProgressBasedOnAngle(float angle) {
        mPointerPosition = angle;
        calculateProgressDegrees();
        mProgress = Math.round((float) mMax * mProgressDegrees / mTotalCircleDegrees);
    }
    private void recalculateAll() {
        calculateTotalDegrees();
        calculatePointerAngle();
        calculateProgressDegrees();
        initRects();
        initPaths();
        calculatePointerXYPosition();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (mMaintainEqualCircle) {
            int min = Math.min(width, height);
            setMeasuredDimension(min, min);
        } else {
            setMeasuredDimension(width, height);
        }
        mCircleHeight = (float) height / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
        mCircleWidth = (float) width / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
        if (mCustomRadii) {
            if ((mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleHeight) {
                mCircleHeight = mCircleYRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
            }
            if ((mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleWidth) {
                mCircleWidth = mCircleXRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
            }
        }
        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
            float min = Math.min(mCircleHeight, mCircleWidth);
            mCircleHeight = min;
            mCircleWidth = min;
       }
        recalculateAll();
    }
    public boolean isLockEnabled() {
        return lockEnabled;
    }
    public void setLockEnabled(boolean lockEnabled) {
        this.lockEnabled = lockEnabled;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Convert coordinates to our internal coordinate system
        float x = event.getX() - getWidth() / 2;
        float y = event.getY() - getHeight() / 2;
        // Get the distance from the center of the circle in terms of x and y
        float distanceX = mCircleRectF.centerX() - x;
        float distanceY = mCircleRectF.centerY() - y;
        // Get the distance from the center of the circle in terms of a radius
        float touchEventRadius = (float) Math.sqrt((Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        float minimumTouchTarget = MIN_TOUCH_TARGET_DP * DPTOPX_SCALE; // Convert minimum touch target into px
        float additionalRadius; // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger
        if (mCircleStrokeWidth < minimumTouchTarget) { // If the width is less than the minimumTouchTarget, use the minimumTouchTarget
            additionalRadius = minimumTouchTarget / 2;
        } else {
            additionalRadius = mCircleStrokeWidth / 2; // Otherwise use the width
        }
        float outerRadius = Math.max(mCircleHeight, mCircleWidth) + additionalRadius; // Max outer radius of the circle, including the minimumTouchTarget or wheel width
        float innerRadius = Math.min(mCircleHeight, mCircleWidth) - additionalRadius; // Min inner radius of the circle, including the minimumTouchTarget or wheel width
        if (mPointerRadius < (minimumTouchTarget / 2)) { // If the pointer radius is less than the minimumTouchTarget, use the minimumTouchTarget
            additionalRadius = minimumTouchTarget / 2;
        } else {
            additionalRadius = mPointerRadius; // Otherwise use the radius
        }
        float touchAngle;
        touchAngle = (float) ((java.lang.Math.atan2(y, x) / Math.PI * 180) % 360);
        touchAngle = (touchAngle < 0 ? 360 + touchAngle : touchAngle);
        cwDistanceFromStart = touchAngle - mStartAngle;
        cwDistanceFromStart = (cwDistanceFromStart < 0 ? 360f + cwDistanceFromStart : cwDistanceFromStart);
        ccwDistanceFromStart = 360f - cwDistanceFromStart;
        cwDistanceFromEnd = touchAngle - mEndAngle;
        cwDistanceFromEnd = (cwDistanceFromEnd < 0 ? 360f + cwDistanceFromEnd : cwDistanceFromEnd);
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched
                float pointerRadiusDegrees = (float) ((mPointerRadius * 180) / (Math.PI * Math.max(mCircleHeight, mCircleWidth)));
                cwDistanceFromPointer = touchAngle - mPointerPosition;
                cwDistanceFromPointer = (cwDistanceFromPointer < 0 ? 360f + cwDistanceFromPointer : cwDistanceFromPointer);
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer;
                // This is for if the first touch is on the actual pointer.
                if (((touchEventRadius >= innerRadius) && (touchEventRadius <= outerRadius)) && ((cwDistanceFromPointer <= pointerRadiusDegrees) || (ccwDistanceFromPointer <= pointerRadiusDegrees))) {
                    setProgressBasedOnAngle(mPointerPosition);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    mIsMovingCW = true;
                    mPointerHaloPaint.setAlpha(mPointerAlphaOnTouch);
                    mPointerHaloPaint.setColor(mPointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                    mUserIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;
                } else if (cwDistanceFromStart > mTotalCircleDegrees) { // If the user is touching outside of the start AND end
                    mUserIsMovingPointer = false;
                    return false;
                } else if ((touchEventRadius >= innerRadius) && (touchEventRadius <= outerRadius)) { // If the user is touching near the circle
                    setProgressBasedOnAngle(touchAngle);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    mIsMovingCW = true;
                    mPointerHaloPaint.setAlpha(mPointerAlphaOnTouch);
                    mPointerHaloPaint.setColor(mPointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener.onStartTrackingTouch(this);
                        mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                    }
                    mUserIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;
                } else { // If the user is not touching near the circle
                    mUserIsMovingPointer = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mUserIsMovingPointer) {
                    if (lastCWDistanceFromStart < cwDistanceFromStart) {
                        if ((cwDistanceFromStart - lastCWDistanceFromStart) > 180f && !mIsMovingCW) {
                            lockAtStart = true;
                            lockAtEnd = false;
                        } else {
                            mIsMovingCW = true;
                        }
                    } else {
                        if ((lastCWDistanceFromStart - cwDistanceFromStart) > 180f && mIsMovingCW) {
                            lockAtEnd = true;
                            lockAtStart = false;
                        } else {
                            mIsMovingCW = false;
                        }
                    }

                    if (lockAtStart && mIsMovingCW) {
                        lockAtStart = false;
                    }
                    if (lockAtEnd && !mIsMovingCW) {
                        lockAtEnd = false;
                    }
                    if (lockAtStart && !mIsMovingCW && (ccwDistanceFromStart > 90)) {
                        lockAtStart = false;
                    }
                    if (lockAtEnd && mIsMovingCW && (cwDistanceFromEnd > 90)) {
                        lockAtEnd = false;
                    }
                    // Fix for passing the end of a semi-circle quickly
                    if (!lockAtEnd && cwDistanceFromStart > mTotalCircleDegrees && mIsMovingCW && lastCWDistanceFromStart < mTotalCircleDegrees) {
                        lockAtEnd = true;
                    }

                    if (lockAtStart && lockEnabled) {
                        // TODO: Add a check if mProgress is already 0, in which case don't call the listener
                        mProgress = 0;
                        recalculateAll();
                        invalidate();
                        if (mOnCircularSeekBarChangeListener != null) {
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                        }

                    } else if (lockAtEnd && lockEnabled) {
                        mProgress = mMax;
                        recalculateAll();
                        invalidate();
                        if (mOnCircularSeekBarChangeListener != null) {
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                        }
                    } else if ((mMoveOutsideCircle) || (touchEventRadius <= outerRadius)) {
                        if (!(cwDistanceFromStart > mTotalCircleDegrees)) {
                            setProgressBasedOnAngle(touchAngle);
                        }
                        recalculateAll();
                        invalidate();
                        if (mOnCircularSeekBarChangeListener != null) {
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                        }
                    } else {
                        break;
                    }

                    lastCWDistanceFromStart = cwDistanceFromStart;
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mPointerHaloPaint.setAlpha(mPointerAlpha);
                mPointerHaloPaint.setColor(mPointerHaloColor);
                if (mUserIsMovingPointer) {
                    mUserIsMovingPointer = false;
                    invalidate();
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener.onStopTrackingTouch(this);
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL: // Used when the parent view intercepts touches for things like scrolling
                mPointerHaloPaint.setAlpha(mPointerAlpha);
                mPointerHaloPaint.setColor(mPointerHaloColor);
                mUserIsMovingPointer = false;
                invalidate();
                break;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return true;
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0);

        initAttributes(attrArray);

        attrArray.recycle();

        initPaints();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("PARENT", superState);
        state.putInt("MAX", mMax);
        state.putInt("PROGRESS", mProgress);
        state.putInt("mCircleColor", mCircleColor);
        state.putInt("mCircleProgressColor", mCircleProgressColor);
        state.putInt("mPointerColor", mPointerColor);
        state.putInt("mPointerHaloColor", mPointerHaloColor);
        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch);
        state.putInt("mPointerAlpha", mPointerAlpha);
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch);
        state.putBoolean("lockEnabled", lockEnabled);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable("PARENT");
        super.onRestoreInstanceState(superState);

        mMax = savedState.getInt("MAX");
        mProgress = savedState.getInt("PROGRESS");
        mCircleColor = savedState.getInt("mCircleColor");
        mCircleProgressColor = savedState.getInt("mCircleProgressColor");
        mPointerColor = savedState.getInt("mPointerColor");
        mPointerHaloColor = savedState.getInt("mPointerHaloColor");
        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch");
        mPointerAlpha = savedState.getInt("mPointerAlpha");
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch");
        lockEnabled = savedState.getBoolean("lockEnabled");

        initPaints();

        recalculateAll();
    }


    public void setOnSeekBarChangeListener(OnCircularSeekBarChangeListener l) {
        mOnCircularSeekBarChangeListener = l;
    }

    /**
     * Gets the circle color.
     *
     * @return An integer color value for the circle
     */
    public int getCircleColor() {
        return mCircleColor;
    }

    /**
     * Sets the circle color.
     *
     * @param color the color of the circle
     */
    public void setCircleColor(int color) {
        mCircleColor = color;
        mCirclePaint.setColor(mCircleColor);
        invalidate();
    }

    /**
     * Gets the circle progress color.
     *
     * @return An integer color value for the circle progress
     */
    public int getCircleProgressColor() {
        return mCircleProgressColor;
    }

    /**
     * Sets the circle progress color.
     *
     * @param color the color of the circle progress
     */
    public void setCircleProgressColor(int color) {
        mCircleProgressColor = color;
        mCircleProgressPaint.setColor(mCircleProgressColor);
        invalidate();
    }

    /**
     * Gets the pointer color.
     *
     * @return An integer color value for the pointer
     */
    public int getPointerColor() {
        return mPointerColor;
    }

    /**
     * Sets the pointer color.
     *
     * @param color the color of the pointer
     */
    public void setPointerColor(int color) {
        mPointerColor = color;
        mPointerPaint.setColor(mPointerColor);
        invalidate();
    }

    /**
     * Gets the pointer halo color.
     *
     * @return An integer color value for the pointer halo
     */
    public int getPointerHaloColor() {
        return mPointerHaloColor;
    }

    /**
     * Sets the pointer halo color.
     *
     * @param color the color of the pointer halo
     */
    public void setPointerHaloColor(int color) {
        mPointerHaloColor = color;
        mPointerHaloPaint.setColor(mPointerHaloColor);
		mPointerHaloColorOnTouch=color;
        invalidate();
    }

    /**
     * Gets the pointer alpha value.
     *
     * @return An integer alpha value for the pointer (0..255)
     */
    public int getPointerAlpha() {
        return mPointerAlpha;
    }

    /**
     * Sets the pointer alpha.
     *
     * @param alpha the alpha of the pointer
     */
    public void setPointerAlpha(int alpha) {
        if (alpha >= 0 && alpha <= 255) {
            mPointerAlpha = alpha;
            mPointerHaloPaint.setAlpha(mPointerAlpha);
            invalidate();
        }
    }

    /**
     * Gets the pointer alpha value when touched.
     *
     * @return An integer alpha value for the pointer (0..255) when touched
     */
    public int getPointerAlphaOnTouch() {
        return mPointerAlphaOnTouch;
    }

    /**
     * Sets the pointer alpha when touched.
     *
     * @param alpha the alpha of the pointer (0..255) when touched
     */
    public void setPointerAlphaOnTouch(int alpha) {
        if (alpha >= 0 && alpha <= 255) {
            mPointerAlphaOnTouch = alpha;
        }
    }

    /**
     * Gets the circle fill color.
     *
     * @return An integer color value for the circle fill
     */
    public int getCircleFillColor() {
        return mCircleFillColor;
    }

    /**
     * Sets the circle fill color.
     *
     * @param color the color of the circle fill
     */
    public void setCircleFillColor(int color) {
        mCircleFillColor = color;
        mCircleFillPaint.setColor(mCircleFillColor);
        invalidate();
    }

    /**
     * Get the current max of the CircularSeekBar.
     *
     * @return Synchronized integer value of the max.
     */
    public synchronized int getMax() {
        return mMax;
    }

    /**
     * Set the max of the CircularSeekBar.
     * If the new max is less than the current progress, then the progress will be set to zero.
     * If the progress is changed as a result, then any listener will receive a onProgressChanged event.
     *
     * @param max The new max for the CircularSeekBar.
     */
    public void setMax(int max) {
        if (!(max <= 0)) { // Check to make sure it's greater than zero
            if (max <= mProgress) {
                mProgress = 0; // If the new max is less than current progress, set progress to zero
                if (mOnCircularSeekBarChangeListener != null) {
                    mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
                }
            }
            mMax = max;

            recalculateAll();
            invalidate();
        }
    }

    /**
     * Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
     */
    public interface OnCircularSeekBarChangeListener {

        void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser);

        void onStopTrackingTouch(CircularSeekBar seekBar);

        void onStartTrackingTouch(CircularSeekBar seekBar);
    }

}

