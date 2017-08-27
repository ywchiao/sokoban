package io.tut.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

class GameView extends View {
    private float mCellWidth;
    public static final int CELL_NUM_PER_LINE = 12;

    public GameView(Context context) {
        super(context);
    }

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

        for (int r = 0; r <= CELL_NUM_PER_LINE; r++) {
            canvas.drawLine(0, r * mCellWidth, getWidth(), r * mCellWidth, linePaint);
        }

        for (int c = 0; c <= CELL_NUM_PER_LINE; c++) {
            canvas.drawLine(c * mCellWidth, 0, c * mCellWidth, CELL_NUM_PER_LINE * mCellWidth, linePaint);
        }
    }
}
