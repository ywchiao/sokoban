package io.tut.sokoban;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

class TuTButton {
    private static Paint mPaint = new Paint();

    private static int colorText;
    private static int colorBackground;

    private boolean mActivated;

    private int mColorMask;

    private float mTextSize;
    private String mLabel;

    private Rect mBounds;
    private Rect mTextBounds;

    TuTButton(String label, boolean activated) {
        mLabel = label;

        if (activated) {
            activate();
        }
        else {
            deactivate();
        }

        mTextBounds = new Rect();
    }

    void setBounds(int left, int top, int right, int bottom) {
        mBounds = new Rect(left, top, right, bottom);

        setTextSize();
    }

    static void setTextColor(int color) {
        colorText = color;
    }

    static void setBackgroundColor(int color) {
        colorBackground = color;
    }

    void deactivate() {
        mColorMask = 0x3fffffff;

        mActivated = false;
    }

    void draw(Canvas canvas) {
        mPaint.setColor(colorBackground);
        mPaint.setStrokeWidth(0);

        canvas.drawRect(mBounds, mPaint);

        mPaint.setColor(colorText & mColorMask);
        mPaint.setTextSize(mTextSize);
        mPaint.setStrokeWidth(4);

        canvas.drawText(
            mLabel,
            mBounds.left + (mBounds.width() - mTextBounds.width()) / 2,
            mBounds.top + (mBounds.height() - (mPaint.ascent() + mPaint.descent())) / 2,
            mPaint
        );
    }

    void activate() {
        mColorMask = 0xffffffff;

        mActivated = true;
    }

    boolean isActivated() {
        return mActivated;
    }

    String getLabel() {
        return mLabel;
    }

    boolean pressed(int x, int y) {
        boolean isPressed = false;

        if (mActivated) {
            isPressed = mBounds.contains(x, y);
        }

        return isPressed;
    }

    void setLabel(String label) {
        mLabel = label;
    }

    private void setTextSize() {
        float testSize = 48f;

        mPaint.setTextSize(testSize);
        mPaint.getTextBounds(mLabel, 0, mLabel.length(), mTextBounds);

        mTextSize = 24f * (mBounds.width() / mTextBounds.width());
        mPaint.getTextBounds(mLabel, 0, mLabel.length(), mTextBounds);
    }
}
