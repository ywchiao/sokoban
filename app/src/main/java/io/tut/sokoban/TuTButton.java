package io.tut.sokoban;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 在 Canvas 上繪出一個自定義 Button。
 */
class TuTButton {
    private static final Paint mPaint = new Paint();

    private static int colorText;
    private static int colorBackground;

    private final Rect mTextBounds;

    private Rect mBounds;

    private boolean mActivated;

    private int mColorMask;

    private float mTextSize;

    private String mLabel;

    /**
     * 建構子。
     *
     * @param label     按鈕上的提示訊息
     * @param activated 按鈕初始狀態
     */
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

    /**
     * 設定 TuTButton 共通的背景顏色。
     *
     * @param color 背景顏色。
     */
    static void setBackgroundColor(int color) {
        colorBackground = color;
    }

    /**
     * 設定 TuTButton 共通的文字顏色。
     *
     * @param color 文字顏色。
     */
    static void setTextColor(int color) {
        colorText = color;
    }

    /**
     * 設定 TuTButton 為 _啟動_ (active) 狀態。
     */
    void activate() {
        // 設定文字顏色為 _不透明_，達到 _正常_ (active) 的視覺效果
        mColorMask = 0xffffffff;

        mActivated = true;
    }

    /**
     * 設定 TuTButton 為 _無效_ (inactive) 狀態。
     */
    void deactivate() {
        // 設定文字顏色為 _半透明_，達到 _灰掉_ (inactive) 的視覺效果
        mColorMask = 0x3fffffff;

        mActivated = false;
    }

    /**
     * 在 Canvas 上繪出 TUTButton。
     *
     * @param canvas 繪圖用的 Canvas 物件。
     */
    void draw(Canvas canvas) {
        mPaint.setColor(colorBackground);
        mPaint.setStrokeWidth(0);

        canvas.drawRect(mBounds, mPaint);

        // 利用 _mColorMask_ 控制文字的 _透明度_。
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

    /**
     * 取得按鈕上的 _標籤_ (label)。
     *
     * @return String 按鈕上的 _標籤_ (label) 文字。
     */
    String getLabel() {
        return mLabel;
    }

    /**
     * 傳回按鈕的 _active_ 狀態。
     *
     * @return boolean true: activate; false: otherwise.
     */
    boolean isActivated() {
        return mActivated;
    }

    /**
     * 傳回按鈕的 _是否_ 被按下。
     *
     * @return boolean true: pressed; false: otherwise.
     */
    boolean isPressed(int x, int y) {
        boolean pressed = false;

        if (mActivated) {
            pressed = mBounds.contains(x, y);
        }

        return pressed;
    }

    /**
     * 設定 TuTButton 的 _rect_ 大小，同時設定相應的 TuTButton 文字大小。
     *
     * @param left   左上角 x 座標。
     * @param top    左上角 y 座標。
     * @param right  右下角 x 座標。
     * @param bottom 右下角 y 座標。
     */
    void setBounds(int left, int top, int right, int bottom) {
        mBounds = new Rect(left, top, right, bottom);

        setTextSize();
    }

    /**
     * 設定 TuTButton 的提示文字。
     *
     * @param label 提示文字
     */
    void setLabel(String label) {
        mLabel = label;
    }

    /**
     * 依據 TuTButton 的 _rect_ 大小計算適合的文字大小及文字 _rect_ 大小。
     */
    private void setTextSize() {
        mPaint.setTextSize(48f);
        mPaint.getTextBounds(mLabel, 0, mLabel.length(), mTextBounds);

        mTextSize = 24f * (mBounds.width() / mTextBounds.width());
        mPaint.getTextBounds(mLabel, 0, mLabel.length(), mTextBounds);
    }
}

// TuTButton.java