package io.tut.sokoban;

import android.graphics.Point;
import android.util.Log;

import java.util.HashSet;

/**
 * GameState 實際紀錄遊戲盤面的變化。
 */
class GameState {
    private static final String TAG = "SOKOBAN";

    static final int PAUSED = 0;
    static final int STARTED = 1;
    static final int GAMING = 2;
    static final int STUCK = 3;
    static final int SOLVED = 4;

    static final int STEP_BLOCKED = 0;
    static final int STEP_MOVING = 1;
    static final int STEP_PUSHING = 2;

    final int NUM_ROW;
    final int NUM_COLUMN;

    private final StringBuffer mUndoHistory;
    private final StringBuffer mSolvingSteps;

    private final StringBuffer[] mLabelInCells;

    private final HashSet<Point> mGoalCells;

    private long mElapsedTime;

    private int mGameStatus;

    private int mManRow;
    private int mManColumn;

    private int mStepType;

    /**
     * 建構子。
     *
     * @param initialState 記錄遊戲 _初始盤面_ 的字串陣列。
     */
    GameState(String[] initialState) {
        mGoalCells = new HashSet<>();

        mUndoHistory = new StringBuffer();
        mSolvingSteps = new StringBuffer();

        NUM_ROW = initialState.length;
        NUM_COLUMN = initialState[0].length();

        mLabelInCells = new StringBuffer[NUM_ROW];

        for (int r = 0; r < NUM_ROW; r++) {
            mLabelInCells[r] = new StringBuffer(initialState[r]);

            // 搜尋 _搬運工_ 和 _目標點_ 在關卡內的初始位置
            for (int c = 0; c < NUM_COLUMN; c++) {
                if (isGoal(c, r)) {
                    mGoalCells.add(new Point(c, r));
                }

                if (isMan(c, r)) {
                    mManColumn = c;
                    mManRow = r;
                }
            }
        }
    }

    /**
     * 取得目前的遊戲 _進行時間_。遊戲進行時間的估算由 GameActivity 負責，顯示
     * 由 GameView 負責，這裡只負責 _保留_ (記錄)。
     *
     * @return long _已進行_ 的 _遊戲時間_。
     */
    long getElapsedTime() {
        return mElapsedTime;
    }

    /**
     * 取得 _目前_ 的 _遊戲狀態_。
     *
     * @return int 遊戲狀態。
     */
    int getGameStatus() {
        return mGameStatus;
    }

    /**
     * 傳回 _目前_ 的 _遊戲盤面_ 狀態。
     *
     * @return StringBuffer[] _遊戲盤面_。
     */
    StringBuffer[] getLabelInCells() {
        return mLabelInCells;
    }

    /**
     * 接收遊戲進行的 _指令_ (人物移動，推箱移動，重覆之前動作等），分派執行，並在執行
     * 成功後 ( _done_ == true) 記錄。
     *
     * @param step 要進行的動作。
     *
     * @return boolean: true 指令成功執行並紀錄； false otherwise。
     */
    boolean redoStep(char step) {
        boolean done;

        switch (step) {
            case Sokoban.REDO_STEP:
                done = redoUndoneStep();

                break;

            case Sokoban.MOVE_DOWN:
                done = moveManDown();

                break;

            case Sokoban.MOVE_LEFT:
                done = moveManLeft();

                break;

            case Sokoban.MOVE_RIGHT:
                done = moveManRight();

                break;

            case Sokoban.MOVE_UP:
                done = moveManUp();

                break;

            case Sokoban.PUSH_DOWN:
                done = pushBoxDown();

                break;

            case Sokoban.PUSH_LEFT:
                done = pushBoxLeft();

                break;

            case Sokoban.PUSH_RIGHT:
                done = pushBoxRight();

                break;

            case Sokoban.PUSH_UP:
                done = pushBoxUp();

                break;

            default:
                // 不應該到這兒，記錄一下
                Log.d(TAG, "redoStep: " + step);

                done = false;

                break;
        }

        if (done) {
            setStepType(step);

            mSolvingSteps.append(step);
        }

        return done;
    }

    /**
     * _清空_ 目前的 _悔棋_ 記錄。
     */
    void resetUndoHistory() {
        mUndoHistory.setLength(0);
    }

    /**
     * 設定目前的 _遊戲狀態_。
     *
     * @param gameStatus 遊戲狀態
     */
    void setGameStatus(int gameStatus) {
        mGameStatus = gameStatus;
    }

    /**
     * 取消 (undo) 遊戲進行的上一步驟；同時將它取消的步驟保留下來，如果萬一又反悔想要
     * redo。
     */
    void undoStep() {
        char step = mSolvingSteps.charAt(mSolvingSteps.length() - 1);

        switch (step) {
            case Sokoban.MOVE_DOWN:
                moveManUp();

                break;

            case Sokoban.MOVE_LEFT:
                moveManRight();

                break;

            case Sokoban.MOVE_RIGHT:
                moveManLeft();

                break;

            case Sokoban.MOVE_UP:
                moveManDown();

                break;

            case Sokoban.PUSH_DOWN:
                moveManRight();
                moveBoxUp(mManColumn, mManRow + 2);

                break;

            case Sokoban.PUSH_LEFT:
                moveManRight();
                moveBoxRight(mManColumn - 2, mManRow);

                break;

            case Sokoban.PUSH_RIGHT:
                moveManLeft();
                moveBoxLeft(mManColumn + 2, mManRow);

                break;

            case Sokoban.PUSH_UP:
                moveManDown();
                moveBoxDown(mManColumn, mManRow - 2);

                break;

            default:
                // 不應該到這兒，記錄一下
                Log.d(TAG, "undoStep: " + step);

                break;
        }

        setStepType(step);

        mUndoHistory.append(step);

        mSolvingSteps.deleteCharAt(mSolvingSteps.length() - 1);
    }

    /**
     * 更新 _已進行_ 的 _遊戲時間_ 為 elapsedTime。遊戲進行時間的估算由 GameActivity 負責，顯示
     * 由 GameView 負責，這裡只負責 _保留_ (記錄)。
     *
     * @param elapsedTime GameActivity 估算後傳入的遊戲進行時間。
     */
    void updateElapsedTime(long elapsedTime) {
        mElapsedTime = elapsedTime;
    }

    /**
     * 檢查遊戲謎題是否已被解開。
     */
    void updateState() {
        if (isPuzzleSolved()) {
            mGameStatus = SOLVED;
        }
    }

    /**
     * 傳回工人目前所在位置的 column (x) 座標。
     *
     * @return int 工人目前所在的 column (x) 座標。
     */
    int getManColumn() {
        return mManColumn;
    }

    /**
     * 傳回工人目前所在位置的 row (y) 座標。
     *
     * @return int 工人目前所在位置的 row (y) 座標。
     */
    int getManRow() {
        return mManRow;
    }

    /**
     * 傳回解謎步驟的字串 。
     *
     * @return String 由解謎步驟組成的字串。
     */
    String getSolvingSteps() {
        return mSolvingSteps.toString();
    }

    /**
     * 傳回 _最近_ 執行的棋步 _類型_。
     *
     * @return int 棋步類型。
     */
    int getStepType() {
        return mStepType;
    }

    /**
     * 傳回箱子是否在工人正 _上_ 方一格。
     *
     * @return true: 箱子在工人正 _上_ 方一格； false: otherwise。
     */
    boolean isBoxAboveToMan() {
        return (mManRow > 0) && isBox(mManColumn, mManRow - 1);
    }

    /**
     * 傳回箱子是否在箱子正 _下_ 方一格。
     *
     * @return true: 工人在箱子正 _下_ 方一格； false: otherwise。
     */
    boolean isBoxBelowToMan() {
        return ((mManRow + 1) < NUM_ROW) && isBox(mManColumn, mManRow + 1);
    }

    /**
     * 傳回箱子是否在工人正 _左_ 方一格。
     *
     * @return true: 箱子在工人正 _左_ 方一格； false: otherwise。
     */
    boolean isBoxLeftToMan() {
        return (mManColumn > 0) && isBox(mManColumn - 1, mManRow);
    }

    /**
     * 傳回工人是否在工人正 _右_ 方一格。
     *
     * @return true: 箱子在工人正 _右_ 方一格； false: otherwise。
     */
    boolean isBoxRightToMan() {
        return ((mManColumn + 1) < NUM_COLUMN) && isBox(mManColumn + 1, mManRow);
    }

    /**
     * 之前是否有 _悔棋_ 可以再後悔， _重覆_ (redo) 悔棋的棋步。
     *
     * @return true: 有棋步可以 redo; false: otherwise。
     */
    boolean isRedoable() {
        return (mUndoHistory.length() > 0);
    }

    /**
     * 之前是否有棋步可以 _悔棋_。
     *
     * @return true: 有棋步可以 undo; false: otherwise。
     */
    boolean isUndoable() {
        return (mSolvingSteps.length() > 0);
    }

    /**
     * 指定的棋盤位置，是否由 _箱子_ 佔據。
     *
     * @param column 行
     * @param row    列
     *
     * @return true: 指定的位置有箱子； false: otherwise。
     */
    private boolean isBox(int column, int row) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.BOX) || (label == Sokoban.BOX_ON_GOAL);
    }

    /**
     * 指定的棋盤位置，是否 _可以_ 被佔據。
     *
     * @param column 行
     * @param row    列
     *
     * @return true: 指定的位置 _可以_ 被佔據； false: otherwise。
     */
    private boolean isFloor(int column, int row) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.FLOOR) || (label == Sokoban.GOAL);
    }

    /**
     * 指定的棋盤位置，是否為 _目標_ (goal) 位置。
     *
     * @param column 行
     * @param row    列
     *
     * @return trur: 指定的位置是 _目標_ 位置； false: otherwise。
     */
    private boolean isGoal(int column, int row) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.GOAL) || (label == Sokoban.BOX_ON_GOAL) || (label == Sokoban.MAN_ON_GOAL);
    }

    /**
     * 指定的棋盤位置，是否為 _未完成_ 的目標位置。
     *
     * @param column 行
     * @param row    列
     *
     * @return true: 指定的位置是 _未完成_ 的目標位置； false: otherwise。
     */
    private boolean isGoalUnachived(int column, int row) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.GOAL) || (label == Sokoban.MAN_ON_GOAL);
    }

    /**
     * 指定的棋盤位置，是否為 _工人_ 所在位置。
     *
     * @param column 行
     * @param row    列
     *
     * @return true: 指定的位置是 _工人_ 所在位置； false: otherwise。
     */
    private boolean isMan(int column, int row) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.MAN) || (label == Sokoban.MAN_ON_GOAL);
    }

    /**
     * 是否 _所有_ 的 _箱子_ 都已經移到 _目標_ 格子。
     *
     * @return true: _所有_ 箱子都在目標格子； false: otherwise。
     */
    private boolean isPuzzleSolved() {
        boolean solved = true;

        for (Point cell : mGoalCells) {
            if (isGoalUnachived(cell.x, cell.y)) {
                solved = false;

                break;
            }
        }

        return solved;
    }

    /**
     * 將指定 _位置_ 的箱子往 _下_ 移一格。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxDown(int column, int row) {
        moveBoxOut(column, row);
        moveBoxIn(column, row + 1);
    }

    /**
     * 將指定 _位置_ 的箱子往 _左_ 移一格。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxLeft(int column, int row) {
        moveBoxOut(column, row);
        moveBoxIn(column - 1, row);
    }

    /**
     * 將指定 _位置_ 的箱子往 _右_ 移一格。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxRight(int column, int row) {
        moveBoxOut(column, row);
        moveBoxIn(column + 1, row);
    }

    /**
     * 將指定 _位置_ 的箱子往 _上_ 移一格。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxUp(int column, int row) {
        moveBoxOut(column, row);
        moveBoxIn(column, row - 1);
    }

    /**
     * 將箱子 _移入_ 指定 _位置_。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxIn(int column, int row) {
        if (mLabelInCells[row].charAt(column) == Sokoban.GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.BOX_ON_GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.BOX);
        }
    }

    /**
     * 將箱子 _移出_ 指定 _位置_。
     *
     * @param column 行
     * @param row    列
     */
    private void moveBoxOut(int column, int row) {
        if (mLabelInCells[row].charAt(column) == Sokoban.BOX_ON_GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.FLOOR);
        }
    }

    /**
     * 將 _工人_ 往 _下移_ 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean moveManDown() {
        boolean done = false;

        int belowRow = mManRow + 1;

        if ((belowRow < NUM_ROW) && isFloor(mManColumn, belowRow)) {
            moveManOut(mManColumn, mManRow);
            mManRow = belowRow;
            moveManIn(mManColumn, mManRow);

            done = true;
        }

        return done;
    }

    /**
     * 將 _工人_ 往 _左移_ 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean moveManLeft() {
        boolean done = false;

        int leftColumn = mManColumn - 1;

        if ((leftColumn >= 0) && isFloor(leftColumn, mManRow)) {
            moveManOut(mManColumn, mManRow);
            mManColumn = leftColumn;
            moveManIn(mManColumn, mManRow);

            done = true;
        }

        return done;
    }

    /**
     * 將 _工人_ 往 _右移_ 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean moveManRight() {
        boolean done = false;

        int rightColumn = mManColumn + 1;

        if ((rightColumn < NUM_COLUMN) && isFloor(rightColumn, mManRow)) {
            moveManOut(mManColumn, mManRow);
            mManColumn = rightColumn;
            moveManIn(mManColumn, mManRow);

            done = true;
        }

        return done;
    }

    /**
     * 將 _工人_ 往 _上移_ 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean moveManUp() {
        boolean done = false;

        int aboveRow = mManRow - 1;

        if ((aboveRow >= 0) && isFloor(mManColumn, aboveRow)) {
            moveManOut(mManColumn, mManRow);
            mManRow = aboveRow;
            moveManIn(mManColumn, mManRow);

            done = true;
        }

        return done;
    }

    /**
     * 將工人 _移入_ 指定 _位置_。
     *
     * @param column 行
     * @param row    列
     */
    private void moveManIn(int column, int row) {
        if (mLabelInCells[row].charAt(column) == Sokoban.GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.MAN_ON_GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.MAN);
        }
    }

    /**
     * 將工人 _移出_ 指定 _位置_。
     *
     * @param column 行
     * @param row    列
     */
    private void moveManOut(int column, int row) {
        if (mLabelInCells[row].charAt(column) == Sokoban.MAN_ON_GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.FLOOR);
        }
    }

    /**
     * 將 _箱子_ 往 _下推_ (工人 + 箱子) 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean pushBoxDown() {
        boolean done = false;

        int belowBoxRow = mManRow + 2;

        if ((belowBoxRow < NUM_ROW) && isFloor(mManColumn, belowBoxRow)) {
            moveBoxDown(mManColumn, mManRow + 1);
            moveManDown();

            done = true;
        }

        return done;
    }

    /**
     * 將 _箱子_ 往 _左推_ (工人 + 箱子) 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean pushBoxLeft() {
        boolean done = false;

        int leftBoxColumn = mManColumn - 2;

        if ((leftBoxColumn >= 0) && isFloor(leftBoxColumn, mManRow)) {
            moveBoxLeft(mManColumn - 1, mManRow);
            moveManLeft();

            done = true;
        }

        return done;
    }

    /**
     * 將 _箱子_ 往 _右推_ (工人 + 箱子) 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean pushBoxRight() {
        boolean done = false;

        int rightBoxColumn = mManColumn + 2;

        if ((rightBoxColumn < NUM_COLUMN) && isFloor(rightBoxColumn, mManRow)) {
            moveBoxRight(mManColumn + 1, mManRow);
            moveManRight();

            done = true;
        }

        return done;
    }

    /**
     * 將 _箱子_ 往 _上推_ (工人 + 箱子) 一格。
     *
     * @return true: 移動成功； false: otherwise。
     */
    private boolean pushBoxUp() {
        boolean done = false;

        int aboveBoxRow = mManRow - 2;

        if ((aboveBoxRow >= 0) && isFloor(mManColumn, aboveBoxRow)) {
            moveBoxUp(mManColumn, mManRow - 1);
            moveManUp();

            done = true;
        }

        return done;
    }

    /**
     * _重覆_ (redo) 之前 _悔棋_ (undo) 的棋步。
     *
     * @return false: 固定傳回 false; 以 _避免_ redo 指令 _進入_ 解題步驟內。
     */
    private boolean redoUndoneStep() {
        char step = mUndoHistory.charAt(mUndoHistory.length() - 1);

        mUndoHistory.deleteCharAt(mUndoHistory.length() - 1);

        redoStep(step);

        return false;
    }

    /**
     * 依據 _最後_ 執行 (redo/undo) 的 _棋步_ 類型 (moving/pushing) 設定棋步
     * _類型_。
     *
     * @param step 棋步字元。
     */
    private void setStepType(char step) {
        if (Sokoban.STEP_MOVING.indexOf(step) > -1) {
            mStepType = STEP_MOVING;
        }

        if (Sokoban.STEP_PUSHING.indexOf(step) > -1) {
            mStepType = STEP_PUSHING;
        }
    }
}