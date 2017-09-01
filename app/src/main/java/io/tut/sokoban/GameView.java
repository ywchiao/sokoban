package io.tut.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class GameView extends View {
    private static final String TAG = "SOKOBAN";

    private float mCellWidth;

    private int mPaddingTop;
    private int mPaddingLeft;

    private float mButtonGap;
    private float mButtonWidth;

    private float mTimerTextSize;

    private int mManFacing = GameBitmaps.FACE_RIGHT;

    private GameActivity mGameActivity;
    private GameBitmaps tileSheet = null;

    private Paint mPaint;

    private Rect mTimerBounds;

    private SoundEffect mSoundEffect;

    private TuTButton[] mButtons;

    public GameView(Context context) {
        super(context);

        mGameActivity = (GameActivity) context;

        mSoundEffect = mGameActivity.getSoundEffect();

        mButtons = new TuTButton[] {
            new TuTButton(getResources().getString(R.string.str_btn_undo), false),
            new TuTButton(getResources().getString(R.string.str_btn_start), true),
            new TuTButton(getResources().getString(R.string.str_btn_redo), false)
        };

        TuTButton.setTextColor(ContextCompat.getColor(context, R.color.colorMidnightBlue));
        TuTButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorMintCream));

        mPaint = new Paint();

        mTimerBounds = new Rect();

        tileSheet = BitmapManager.getSokobanSkin(getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 背景色
        mPaint.setColor(ContextCompat.getColor(mGameActivity, R.color.colorBackground));
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        // 繪製遊戲局面
        drawGameBoard(canvas);

        // 繪製遊戲時間
        drawGameElapsedTime(canvas);

        drawGameButton(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (h > w) {
            mCellWidth = w / mGameActivity.getCurrentState().NUM_COLUMN;

            mPaddingTop = (int) Math.floor(((h / mCellWidth) - mGameActivity.getCurrentState().NUM_ROW) / 2);
            mPaddingLeft = 0;

            mButtonWidth = w / 4;
            mButtonGap = mButtonWidth / 6;

            setTimerBounds(w);
        }
        else {
            mCellWidth = h / mGameActivity.getCurrentState().NUM_ROW;

            mPaddingTop = 0;
            mPaddingLeft = (int) Math.floor(((w / mCellWidth) - mGameActivity.getCurrentState().NUM_COLUMN) / 2);

            mButtonWidth = h / 5;
            mButtonGap = mButtonWidth / 3;

            setTimerBounds(h);
        }

        setButtonSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        int touch_x = (int)event.getX(); // 觸摸點的 x 坐標
        int touch_y = (int)event.getY(); // 觸摸點的 y 坐標

        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.getState() == GameState.GAMING) {
            handleBoardPane(gameState, touch_x, touch_y);
        }

        handleButtonPane(gameState, touch_x, touch_y);

        refreshButtonPane(gameState);

        playSoundEffect(gameState);

        gameState.updateState();

        postInvalidate();

        return false;
    }

    private void drawGameBoard(Canvas canvas) {
        Rect srcRect;
        Rect destRect;

        StringBuffer[] labelInCells = mGameActivity.getCurrentState().getLabelInCells();

        for (int r = 0; r < labelInCells.length; r ++) {
            for (int c = 0; c < labelInCells[r].length(); c ++) {
                destRect = getRect(r, c);

                switch (labelInCells[r].charAt(c)) {
                    case Sokoban.BOX:
                        srcRect = tileSheet.getTileBoxOnFloor();

                        break;

                    case Sokoban.BOX_ON_GOAL:
                        srcRect = tileSheet.getTileBoxOnGoal();

                        break;

                    case Sokoban.EMPTY:
                    case Sokoban.EMPTY_ALT:
                        srcRect = tileSheet.getTileEmpty();

                        break;

                    case Sokoban.FLOOR:
                        srcRect = tileSheet.getTileFloor();

                        break;

                    case Sokoban.GOAL:
                        srcRect = tileSheet.getTileGoal();

                        break;

                    case Sokoban.MAN:
                        srcRect = tileSheet.getTileFloor();
                        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);

                        srcRect = tileSheet.getTileMan(mManFacing);

                        break;

                    case Sokoban.MAN_ON_GOAL:
                        srcRect = tileSheet.getTileGoal();
                        canvas.drawBitmap(GameBitmaps.tileSheet, srcRect, destRect, null);

                        srcRect = tileSheet.getTileMan(mManFacing);

                        break;

                    case Sokoban.WALL:
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

    private void drawGameButton(Canvas canvas) {
        for (TuTButton button : mButtons) {
            button.draw(canvas);
        }
    }

    private void drawGameElapsedTime(Canvas canvas) {
        // 文字顏色
        mPaint.setColor(ContextCompat.getColor(mGameActivity, R.color.colorMintCream));

        canvas.drawText(
            mGameActivity.getCurrentState().getElapsedTime(),
            mTimerBounds.left,
            mTimerBounds.top + (mTimerBounds.height() - (mPaint.ascent() + mPaint.descent())) / 2,
            mPaint
        );
    }

    private Rect getRect(int row, int column) {
        int left = (int)((mPaddingLeft + column) * mCellWidth);
        int top = (int)((mPaddingTop + row) * mCellWidth);
        int right = (int)((mPaddingLeft + column + 1) * mCellWidth);
        int bottom = (int)((mPaddingTop + row + 1) * mCellWidth);

        return new Rect(left, top, right, bottom);
    }

    private void handleBoardPane(GameState gameState, int touch_x, int touch_y) {
        int manRow = gameState.getManRow();
        int manColumn = gameState.getManColumn();

        if (touch_above_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleUp(gameState);
        }

        if (touch_below_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleDown(gameState);
        }

        if (touch_left_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleLeft(gameState);
        }

        if (touch_right_to_man(touch_x, touch_y, manRow, manColumn)) {
            handleRight(gameState);
        }

        if ((manRow != gameState.getManRow()) || (manColumn != gameState.getManColumn())) {
            gameState.resetUndoHistory();
        }
    }

    private void handleButtonPane(GameState gameState, int touch_x, int touch_y) {
        for (TuTButton button : mButtons) {
            if (button.isActivated() && button.isPressed(touch_x, touch_y)) {
                if (getResources().getString(R.string.str_btn_quit).equals(button.getLabel())) {
                    gameState.setState(GameState.STUCK);

                    break;
                }

                if (getResources().getString(R.string.str_btn_redo).equals(button.getLabel())) {
                    gameState.redoStep(Sokoban.REDO_STEP);

                    break;
                }

                if (getResources().getString(R.string.str_btn_start).equals(button.getLabel())) {
                    button.setLabel(getResources().getString(R.string.str_btn_quit));

                    gameState.setState(GameState.STARTED);

                    break;
                }

                if (getResources().getString(R.string.str_btn_undo).equals(button.getLabel())) {
                    gameState.undoStep();

                    break;
                }
            }
        }
    }

    private void handleDown(GameState gameState) {
        if (gameState.isBoxBelowToMan()) {
            gameState.redoStep(Sokoban.PUSH_DOWN);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_DOWN);
        }

        mManFacing = GameBitmaps.FACE_DOWN;
    }

    private void handleLeft(GameState gameState) {
        if (gameState.isBoxLeftToMan()) {
            gameState.redoStep(Sokoban.PUSH_LEFT);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_LEFT);
        }

        mManFacing = GameBitmaps.FACE_LEFT;
    }

    private void handleRight(GameState gameState) {
        if (gameState.isBoxRightToMan()) {
            gameState.redoStep(Sokoban.PUSH_RIGHT);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_RIGHT);
        }

        mManFacing = GameBitmaps.FACE_RIGHT;
    }

    private void handleUp(GameState gameState) {
        if (gameState.isBoxAboveToMan()) {
            gameState.redoStep(Sokoban.PUSH_UP);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_UP);
        }

        mManFacing = GameBitmaps.FACE_UP;
    }

    private void playSoundEffect(GameState gameState) {
        if (gameState.getLastStep() == GameState.STEP_PUSHING) {
            mSoundEffect.playPushingEffect();
        }

        if (gameState.getLastStep() == GameState.STEP_MOVING) {
            mSoundEffect.playWalkingEffect();
        }
    }

    private void refreshButtonPane(GameState gameState) {
        for (TuTButton button : mButtons) {
            if (getResources().getString(R.string.str_btn_redo).equals(button.getLabel())) {
                if (gameState.isRedoable()) {
                    button.activate();
                }
                else {
                    button.deactivate();
                }
            }

            if (getResources().getString(R.string.str_btn_undo).equals(button.getLabel())) {
                if (gameState.isUndoable()) {
                    button.activate();
                }
                else {
                    button.deactivate();
                }
            }
        }
    }

    private void setTimerBounds(int width) {
        String elapsedTime = mGameActivity.getCurrentState().getElapsedTime();

        mPaint.setTextSize(48f);
        mPaint.getTextBounds(elapsedTime, 0, elapsedTime.length(), mTimerBounds);

        mTimerTextSize = 24f * (mCellWidth / mTimerBounds.height());

        mPaint.setTextSize(mTimerTextSize);
        mPaint.getTextBounds(elapsedTime, 0, elapsedTime.length(), mTimerBounds);

        mTimerBounds = new Rect(
            (int) (width - (mTimerBounds.width() + mButtonGap)),
            (int) (mCellWidth * mPaddingTop),
            (int) (width - mButtonGap),
            (int) (mCellWidth * (mPaddingTop + 1))
        );
    }

    private void setButtonSize() {
        for (int i = 0; i < mButtons.length; i ++) {
            mButtons[i].setBounds(
                (int) (mButtonGap * 2 + i * (mButtonWidth + mButtonGap)),
                (int) (mCellWidth * (mPaddingTop + mGameActivity.getCurrentState().NUM_ROW + 1)),
                (int) (mButtonGap * 2 + i * (mButtonWidth + mButtonGap) + mButtonWidth),
                (int) (mCellWidth * (mPaddingTop + mGameActivity.getCurrentState().NUM_ROW + 2))
            );
        }
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