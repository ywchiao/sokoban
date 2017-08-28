package io.tut.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {
    private static final String TAG = "SOKOBAN";

    private static final int CELL_NUM_PER_LINE = 12;
    private float mCellWidth;

    private int mManFacing = GameBitmaps.FACE_RIGHT;

    private GameActivity mGameActivity;
    private GameBitmaps tileSheet = null;

    public GameView(Context context) {
        super(context);

        mGameActivity = (GameActivity) context;

        tileSheet = BitmapManager.getSokobanSkin(getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint background = new Paint();

        // 背景色
        background.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        // 繪製遊戲局面
        drawGameBoard(canvas);
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

        GameState gameState = mGameActivity.getCurrentState();

        int manRow = gameState.getManRow();
        int manColumn = gameState.getManColumn();

        int touch_x = (int)event.getX(); // 觸摸點的 x 坐標
        int touch_y = (int)event.getY(); // 觸摸點的 y 坐標

        if (touch_above_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleUp();
        }

        if (touch_below_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleDown();
        }

        if (touch_left_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleLeft();
        }

        if (touch_right_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleRight();
        }

        postInvalidate();

        return true;
    }

    private void drawGameBoard(Canvas canvas) {
        Rect srcRect;
        Rect destRect;

        StringBuffer[] labelInCells = mGameActivity.getCurrentState().getLabelInCells();

        for (int r = 0; r < labelInCells.length; r ++) {
            for (int c = 0; c < labelInCells[r].length(); c ++) {
                destRect = getRect(r, c);

                switch (labelInCells[r].charAt(c)) {
                    case GameLevels.BOX:
                        srcRect = tileSheet.getTileBoxOnFloor();

                        break;

                    case GameLevels.BOX_ON_GOAL:
                        srcRect = tileSheet.getTileBoxOnGoal();

                        break;

                    case GameLevels.EMPTY:
                    case GameLevels.EMPTY_ALT:
                        srcRect = tileSheet.getTileEmpty();

                        break;

                    case GameLevels.FLOOR:
                        srcRect = tileSheet.getTileFloor();

                        break;

                    case GameLevels.GOAL:
                        srcRect = tileSheet.getTileGoal();

                        break;

                    case GameLevels.MAN:
                        srcRect = tileSheet.getTileFloor();
                        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);

                        srcRect = tileSheet.getTileMan(mManFacing);

                        break;

                    case GameLevels.MAN_ON_GOAL:
                        srcRect = tileSheet.getTileGoal();
                        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);

                        srcRect = tileSheet.getTileMan(mManFacing);

                        break;

                    case GameLevels.WALL:
                        srcRect = tileSheet.getTileWall();

                        break;

                    default:
                        // 不應該會到這裡，記錄一下
                        Log.d(TAG, "drawGameBoard: (r, c) = (" + r + ", " + c + ")");

                        // 放個空白的 Tile，提醒使用者
                        srcRect = tileSheet.getTileBlank();

                        break;
                }

                canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);
            }
        }
    }

    private Rect getRect(int row, int column) {
        int left = (int)(column * mCellWidth);
        int top = (int)(row * mCellWidth);
        int right = (int)((column + 1) * mCellWidth);
        int bottom = (int)((row + 1) * mCellWidth);

        return new Rect(left, top, right, bottom);
    }

    private void handleDown() {
        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.isBoxBelowToMan()) {
            gameState.pushBoxDown();
        }
        else {
            gameState.moveManDown();
        }

        mManFacing = GameBitmaps.FACE_DOWN;
    }

    private void handleLeft() {
        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.isBoxLeftToMan()) {
            gameState.pushBoxLeft();
        }
        else {
            gameState.moveManLeft();
        }

        mManFacing = GameBitmaps.FACE_LEFT;
    }

    private void handleRight() {
        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.isBoxRightToMan()) {
            gameState.pushBoxRight();
        }
        else {
            gameState.moveManRight();
        }

        mManFacing = GameBitmaps.FACE_RIGHT;
    }

    private void handleUp() {
        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.isBoxAboveToMan()) {
            gameState.pushBoxUp();
        }
        else {
            gameState.moveManUp();
        }

        mManFacing = GameBitmaps.FACE_UP;
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