package io.tut.sokoban;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * GameView 負責畫面顯示，與使用者互動。
 */
class GameView extends View {
    private static final String TAG = "SOKOBAN";

    private float mButtonGap;
    private float mButtonWidth;

    private float mCellWidth;

    private int mPaddingTop;
    private int mPaddingLeft;

    private int mManFacing = GameBitmaps.FACE_RIGHT;

    private GameActivity mGameActivity;
    private GameBitmaps tileSheet = null;

    private Paint mPaint;

    private Rect mTimerBounds;

    private SoundEffect mSoundEffect;

    private TuTButton[] mButtons;

    /**
     * 建構子。
     *
     * @param context 擁有這個 View 的 Activity。
     */
    public GameView(Context context) {
        super(context);

        mGameActivity = (GameActivity) context;

        mSoundEffect = mGameActivity.getSoundEffect();

        mButtons = new TuTButton[]{
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

    /**
     * Draw 事件處理程序。
     *
     * @param canvas Canvas 物件。
     */
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

    /**
     * 螢幕大小改變處理程序。
     *
     * @param w    寬度
     * @param h    高度
     * @param oldW 原有寬度
     * @param oldH 原有高度
     */
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

    /**
     * TouchEvent 處理程序。
     *
     * @param event TouchEvent 物件。
     *
     * @return true: TouchEvent 已被處理； false: otherwise (回傳給 GameActivity 處理)。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        int touch_x = (int) event.getX(); // 觸摸點的 x 坐標
        int touch_y = (int) event.getY(); // 觸摸點的 y 坐標

        GameState gameState = mGameActivity.getCurrentState();

        if (gameState.getGameStatus() == GameState.GAMING) {
            handleBoardPane(gameState, touch_x, touch_y);
        }

        handleButtonPane(gameState, touch_x, touch_y);

        refreshButtonPane(gameState);

        playSoundEffect(gameState);

        gameState.updateState();

        postInvalidate();

        return false;
    }

    /**
     * 傳回使用者在當前關卡已使用的 _時間字串_ ；以 "00:00:00:000" 格式呈現。
     */
    public String getElapsedTime() {
        long elapsedTime = mGameActivity.getCurrentState().getElapsedTime();

        int millis = (int) (elapsedTime % 1000);
        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        minutes = minutes % 60;
        seconds = seconds % 60;

        return getResources().getString(R.string.str_formated_time, hours, minutes, seconds, millis);
    }

    /**
     * 將 _遊戲盤面_ 繪製到 Canvas 上。
     *
     * @param canvas Canvas 物件。
     */
    private void drawGameBoard(Canvas canvas) {
        Rect srcRect;
        Rect destRect;

        StringBuffer[] labelInCells = mGameActivity.getCurrentState().getLabelInCells();

        for (int r = 0; r < labelInCells.length; r++) {
            for (int c = 0; c < labelInCells[r].length(); c++) {
                destRect = getRect(c, r);

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

    /**
     * 將 _遊戲按鈕_ 繪製到 Canvas 上。
     *
     * @param canvas Canvas 物件。
     */
    private void drawGameButton(Canvas canvas) {
        for (TuTButton button : mButtons) {
            button.draw(canvas);
        }
    }

    /**
     * 將 _遊戲時間_ 繪製到 Canvas 上。
     *
     * @param canvas Canvas 物件。
     */
    private void drawGameElapsedTime(Canvas canvas) {
        // 文字顏色
        mPaint.setColor(ContextCompat.getColor(mGameActivity, R.color.colorMintCream));

        canvas.drawText(
            getElapsedTime(),
            mTimerBounds.left,
            mTimerBounds.top + (mTimerBounds.height() - (mPaint.ascent() + mPaint.descent())) / 2,
            mPaint
        );
    }

    /**
     * 依據指定的 _位置_ (column, row) 算出螢幕上的 Rect 座標範圍。
     *
     * @param column 行
     * @param row    列
     *
     * @return Rect (column, row) 對應的 Rect 物件。
     */
    private Rect getRect(int column, int row) {
        int left = (int) ((mPaddingLeft + column) * mCellWidth);
        int top = (int) ((mPaddingTop + row) * mCellWidth);
        int right = (int) ((mPaddingLeft + column + 1) * mCellWidth);
        int bottom = (int) ((mPaddingTop + row + 1) * mCellWidth);

        return new Rect(left, top, right, bottom);
    }

    /**
     * 處理 _遊戲盤面_ 上發生的 TouchEvent。
     *
     * @param gameState GameState 物件。
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     */
    private void handleBoardPane(GameState gameState, int touch_x, int touch_y) {
        int manColumn = gameState.getManColumn();
        int manRow = gameState.getManRow();

        if (touch_above_to_man(touch_x, touch_y, manColumn, manRow)) {
            handleUp(gameState);
        }

        if (touch_below_to_man(touch_x, touch_y, manColumn, manRow)) {
            handleDown(gameState);
        }

        if (touch_left_to_man(touch_x, touch_y, manColumn, manRow)) {
            handleLeft(gameState);
        }

        if (touch_right_to_man(touch_x, touch_y, manColumn, manRow)) {
            handleRight(gameState);
        }

        if ((manRow != gameState.getManRow()) || (manColumn != gameState.getManColumn())) {
            gameState.resetUndoHistory();
        }
    }

    /**
     * 處理 _按鈕區域_ 上發生的 TouchEvent。
     *
     * @param gameState GameState 物件。
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     */
    private void handleButtonPane(GameState gameState, int touch_x, int touch_y) {
        for (TuTButton button : mButtons) {
            if (button.isActivated() && button.isPressed(touch_x, touch_y)) {
                if (getResources().getString(R.string.str_btn_quit).equals(button.getLabel())) {
                    gameState.setGameStatus(GameState.STUCK);

                    break;
                }

                if (getResources().getString(R.string.str_btn_redo).equals(button.getLabel())) {
                    gameState.redoStep(Sokoban.REDO_STEP);

                    break;
                }

                if (getResources().getString(R.string.str_btn_start).equals(button.getLabel())) {
                    button.setLabel(getResources().getString(R.string.str_btn_quit));

                    gameState.setGameStatus(GameState.STARTED);

                    break;
                }

                if (getResources().getString(R.string.str_btn_undo).equals(button.getLabel())) {
                    gameState.undoStep();

                    break;
                }
            }
        }
    }

    /**
     * 處理 _工人_ 往 _下_ 推 _箱子_ 或者 _移動_。
     *
     * @param gameState GameState 物件。
     */
    private void handleDown(GameState gameState) {
        if (gameState.isBoxBelowToMan()) {
            gameState.redoStep(Sokoban.PUSH_DOWN);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_DOWN);
        }

        mManFacing = GameBitmaps.FACE_DOWN;
    }

    /**
     * 處理 _工人_ 往 _左_ 推 _箱子_ 或者 _移動_。
     *
     * @param gameState GameState 物件。
     */
    private void handleLeft(GameState gameState) {
        if (gameState.isBoxLeftToMan()) {
            gameState.redoStep(Sokoban.PUSH_LEFT);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_LEFT);
        }

        mManFacing = GameBitmaps.FACE_LEFT;
    }

    /**
     * 處理 _工人_ 往 _右_ 推 _箱子_ 或者 _移動_。
     *
     * @param gameState GameState 物件。
     */
    private void handleRight(GameState gameState) {
        if (gameState.isBoxRightToMan()) {
            gameState.redoStep(Sokoban.PUSH_RIGHT);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_RIGHT);
        }

        mManFacing = GameBitmaps.FACE_RIGHT;
    }

    /**
     * 處理 _工人_ 往 _上_ 推 _箱子_ 或者 _移動_。
     *
     * @param gameState GameState 物件。
     */
    private void handleUp(GameState gameState) {
        if (gameState.isBoxAboveToMan()) {
            gameState.redoStep(Sokoban.PUSH_UP);
        }
        else {
            gameState.redoStep(Sokoban.MOVE_UP);
        }

        mManFacing = GameBitmaps.FACE_UP;
    }

    /**
     * 播放音效 (SoundEffect)。
     *
     * @param gameState GameState 物件。
     */
    private void playSoundEffect(GameState gameState) {
        if (gameState.getStepType() == GameState.STEP_PUSHING) {
            mSoundEffect.playPushingEffect();
        }

        if (gameState.getStepType() == GameState.STEP_MOVING) {
            mSoundEffect.playWalkingEffect();
        }
    }

    /**
     * 更新 _遊戲按鈕_ 的 _狀態_ (in/active)。
     *
     * @param gameState GameState 物件。
     */
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

    /**
     * 計算並設置遊戲 _進行時間_ 顯示字串的 _字體大小_ 和 _顯示區域_ 大小。
     *
     * @param screenWidth 螢幕寬度。
     */
    private void setTimerBounds(int screenWidth) {
        float textSize;
        String elapsedTime = getElapsedTime();

        mPaint.setTextSize(48f);
        mPaint.getTextBounds(elapsedTime, 0, elapsedTime.length(), mTimerBounds);

        textSize = 24f * (mCellWidth / mTimerBounds.height());

        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(elapsedTime, 0, elapsedTime.length(), mTimerBounds);

        mTimerBounds = new Rect(
                                   (int) (screenWidth - (mTimerBounds.width() + mButtonGap)),
                                   (int) (mCellWidth * mPaddingTop),
                                   (int) (screenWidth - mButtonGap),
                                   (int) (mCellWidth * (mPaddingTop + 1))
        );
    }

    /**
     * 計算並設置 _遊戲按鈕_ 的 _顯示區域_ 大小。
     */
    private void setButtonSize() {
        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setBounds(
                (int) (mButtonGap * 2 + i * (mButtonWidth + mButtonGap)),
                (int) (mCellWidth * (mPaddingTop + mGameActivity.getCurrentState().NUM_ROW + 1)),
                (int) (mButtonGap * 2 + i * (mButtonWidth + mButtonGap) + mButtonWidth),
                (int) (mCellWidth * (mPaddingTop + mGameActivity.getCurrentState().NUM_ROW + 2))
            );
        }
    }

    /**
     * 傳回 TouchEvent _是否_ 發生在 _工人_ 正 _上方_ 一格。
     *
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     * @param manColumn _工人_ 所在 _行_ (column)。
     * @param manRow    _工人_ 所在 _列_ (row)。
     *
     * @return true: TouchEvent 在 _工人_ 的正 _上方_ 一格； false: otherwise。
     */
    private boolean touch_above_to_man(int touch_x, int touch_y, int manColumn, int manRow) {
        int aboveRow = manRow - 1;

        Rect aboveRect = getRect(manColumn, aboveRow);

        return aboveRect.contains(touch_x, touch_y);
    }

    /**
     * 傳回 TouchEvent _是否_ 發生在 _工人_ 正 _下方_ 一格。
     *
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     * @param manColumn _工人_ 所在 _行_ (column)。
     * @param manRow    _工人_ 所在 _列_ (row)。
     *
     * @return true: TouchEvent 在 _工人_ 的正 _下方_ 一格； false: otherwise。
     */
    private boolean touch_below_to_man(int touch_x, int touch_y, int manColumn, int manRow) {
        int belowRow = manRow + 1;

        Rect belowRect = getRect(manColumn, belowRow);

        return belowRect.contains(touch_x, touch_y);
    }

    /**
     * 傳回 TouchEvent _是否_ 發生在 _工人_ 正 _左方_ 一格。
     *
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     * @param manColumn _工人_ 所在 _行_ (column)。
     * @param manRow    _工人_ 所在 _列_ (row)。
     *
     * @return true: TouchEvent 在 _工人_ 的正 _左方_ 一格； false: otherwise。
     */
    private boolean touch_left_to_man(int touch_x, int touch_y, int manColumn, int manRow) {
        int leftColumn = manColumn - 1;

        Rect leftRect = getRect(leftColumn, manRow);

        return leftRect.contains(touch_x, touch_y);
    }

    /**
     * 傳回 TouchEvent _是否_ 發生在 _工人_ 正 _右方_ 一格。
     *
     * @param touch_x   TouchEvent 的 x 座標。
     * @param touch_y   TouchEvent 的 y 座標。
     * @param manColumn _工人_ 所在 _行_ (column)。
     * @param manRow    _工人_ 所在 _列_ (row)。
     *
     * @return true: TouchEvent 在 _工人_ 的正 _右方_ 一格； false: otherwise。
     */
    private boolean touch_right_to_man(int touch_x, int touch_y, int manColumn, int manRow) {
        int rightColumn = manColumn + 1;

        Rect rightRect = getRect(rightColumn, manRow);

        return rightRect.contains(touch_x, touch_y);
    }
}