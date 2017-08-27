package io.tut.sokoban;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

class GameView extends View {
    private float mCellWidth;
    public static final int CELL_NUM_PER_LINE = 12;
    private Bitmap tsBitmap = null;

    // Tile Size
    private static final int TILE_WIDTH = 64;

    public GameView(Context context) {
        super(context);

        tsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sokoban);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mCellWidth = w / CELL_NUM_PER_LINE;
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

        Rect destRect = new Rect(0, 0, (int)mCellWidth, (int)mCellWidth);

        canvas.drawBitmap(tsBitmap, srcRect, destRect, null);
    }
}
