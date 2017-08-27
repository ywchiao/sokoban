package io.tut.sokoban;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {
    private float mCellWidth;
    public static final int CELL_NUM_PER_LINE = 12;
    private Bitmap tsBitmap = null;

    private int mManRow = 0;
    private int mManColumn = 0;

    // Tile Size
    private static final int TILE_WIDTH = 64;

    public GameView(Context context) {
        super(context);

        tsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sokoban);
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

        // 複製搬運工
        Rect srcRect = new Rect(0, TILE_WIDTH * 2, TILE_WIDTH, TILE_WIDTH * 3);

        Rect destRect = getRect(mManRow, mManColumn);

        canvas.drawBitmap(tsBitmap, srcRect, destRect, null);
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

        if (touch_below_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            mManRow ++;
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

    private boolean touch_below_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int belowRow = manRow + 1;

        Rect belowRect = getRect(belowRow, manColumn);

        return belowRect.contains(touch_x, touch_y);
    }
}