package io.tut.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {
    private float mCellWidth;
    public static final int CELL_NUM_PER_LINE = 12;

    private int mManRow = 0;
    private int mManColumn = 0;
    private int mManFacing = GameBitmaps.FACE_RIGHT;

    private int mBoxRow = 5;
    private int mBoxColumn = 5;

    private GameBitmaps tileSheet = null;

    public GameView(Context context) {
        super(context);

        tileSheet = BitmapManager.getSokobanSkin(getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint background = new Paint();

        // 背景色
        background.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        Paint linePaint = new Paint();

        // 繪制遊戲區域
        linePaint.setColor(Color.BLACK);

        for (int r = 0; r <= CELL_NUM_PER_LINE; r ++) {
            canvas.drawLine(0, r * mCellWidth, getWidth(), r * mCellWidth, linePaint);
        }

        for (int c = 0; c <= CELL_NUM_PER_LINE; c ++) {
            canvas.drawLine(c * mCellWidth, 0, c * mCellWidth, CELL_NUM_PER_LINE * mCellWidth, linePaint);
        }

        // 繪製搬運工
        Rect srcRect = tileSheet.getMan(mManFacing);

        Rect destRect = getRect(mManRow, mManColumn);

        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);

        srcRect = tileSheet.getBoxOnFloor();

        destRect = getRect(mBoxRow, mBoxColumn);

        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mCellWidth = w / CELL_NUM_PER_LINE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        int touch_x = (int)event.getX(); // 觸摸點的 x 坐標
        int touch_y = (int)event.getY(); // 觸摸點的 y 坐標

        if (touch_above_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            handleUp();
        }

        if (touch_below_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            handleDown();
        }

        if (touch_left_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            handleLeft();
        }

        if (touch_right_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            handleRight();
        }

        postInvalidate();

        return true;
    }

    private Rect getRect(int row, int column) {
        int left = (int)(column * mCellWidth);
        int top = (int)(row * mCellWidth);
        int right = (int)((column + 1) * mCellWidth);
        int bottom = (int)((row + 1) * mCellWidth);

        return new Rect(left, top, right, bottom);
    }

    private void handleDown() {
        if (isBoxBelowToMan()) {
            if ((mBoxRow + 1) < CELL_NUM_PER_LINE) {
                mBoxRow ++;
                mManRow ++;
            }
        }
        else {
            mManRow = ((mManRow + 1) == CELL_NUM_PER_LINE) ? mManRow : mManRow + 1;
        }

        mManFacing = GameBitmaps.FACE_DOWN;
    }

    private void handleLeft() {
        if (isBoxLeftToMan()) {
            if (mBoxColumn > 0) {
                mBoxColumn --;
                mManColumn --;
            }
        }
        else {
            mManColumn = (mManColumn == 0) ? mManColumn : mManColumn - 1;
        }

        mManFacing = GameBitmaps.FACE_LEFT;
    }

    private void handleRight() {
        if (isBoxRightToMan()) {
            if ((mBoxColumn + 1) < CELL_NUM_PER_LINE) {
                mBoxColumn ++;
                mManColumn ++;
            }
        }
        else {
            mManColumn = ((mManColumn + 1) == CELL_NUM_PER_LINE) ? mManColumn : mManColumn + 1;
        }

        mManFacing = GameBitmaps.FACE_RIGHT;
    }

    private void handleUp() {
        if (isBoxAboveToMan()) {
            if (mBoxRow > 0) {
                mBoxRow --;
                mManRow --;
            }
        }
        else {
            mManRow = (mManRow == 0) ? mManRow : mManRow - 1;
        }

        mManFacing = GameBitmaps.FACE_UP;
    }

    private boolean isBoxAboveToMan() {
        return mBoxColumn == mManColumn && mBoxRow == mManRow - 1;
    }

    private boolean isBoxBelowToMan() {
        return mBoxColumn == mManColumn && mBoxRow == mManRow + 1;
    }

    private boolean isBoxLeftToMan() {
        return mBoxColumn == mManColumn - 1 && mBoxRow == mManRow;
    }

    private boolean isBoxRightToMan() {
        return mBoxColumn == mManColumn + 1 && mBoxRow == mManRow;
    }

    private boolean touch_above_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int aboveRow = manRow - 1;

        Rect aboveRect = getRect(aboveRow, manColumn);

        return aboveRect.contains(touch_x, touch_y);
    }

    private boolean touch_below_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int belowRow = manRow + 1;

        Rect belowRect = getRect(belowRow, manColumn);

        return belowRect.contains(touch_x, touch_y);
    }

    private boolean touch_left_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int leftColumn = manColumn - 1;

        Rect leftRect = getRect(manRow, leftColumn);

        return leftRect.contains(touch_x, touch_y);
    }

    private boolean touch_right_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int rightColumn = manColumn + 1;

        Rect rightRect = getRect(manRow, rightColumn);

        return rightRect.contains(touch_x, touch_y);
    }
}