package io.tut.sokoban;

import android.graphics.Point;
import android.util.Log;

import java.util.HashSet;

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

    private int mLastStep;

    private int mManRow;
    private int mManColumn;

    private int mState;

    private String mElapsedTime;

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
                if (isGoal(r, c)) {
                    mGoalCells.add(new Point(r, c));
                }

                if (isMan(r, c)) {
                    mManRow = r;
                    mManColumn = c;
                }
            }
        }
    }

    StringBuffer[] getLabelInCells() {
        return mLabelInCells;
    }

    int getState() {
        return mState;
    }

    String getElapsedTime() {
        return mElapsedTime;
    }

    boolean redoStep(char step) {
        boolean done;

        switch (step) {
            case Sokoban.REDO_STEP:
                done = prevStep();

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
                // 不應該到這兒，記露一下
                Log.d(TAG, "redoStep: " + step);

                done = false;

                break;
        }

        if (done) {
            setLastStep(step);

            mSolvingSteps.append(step);
        }

        return done;
    }

    void resetUndoHistory() {
        mUndoHistory.setLength(0);
    }

    void setState(int state) {
        mState = state;
    }

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
                moveBoxUp(mManRow + 2, mManColumn);

                break;

            case Sokoban.PUSH_LEFT:
                moveManRight();
                moveBoxRight(mManRow, mManColumn - 2);

                break;

            case Sokoban.PUSH_RIGHT:
                moveManLeft();
                moveBoxLeft(mManRow, mManColumn + 2);

                break;

            case Sokoban.PUSH_UP:
                moveManDown();
                moveBoxDown(mManRow - 2, mManColumn);

                break;

            default:
                // 不應該到這兒，記錄一下
                Log.d(TAG, "undoStep: " + step);

                break;
        }

        setLastStep(step);

        mUndoHistory.append(step);

        mSolvingSteps.deleteCharAt(mSolvingSteps.length() - 1);
    }

    void updateElapsedTime(String elapsedTime) {
        mElapsedTime = elapsedTime;
    }

    void updateState() {
        if (isPuzzleSolved()) {
            mState = SOLVED;
        }
        else if (mState == SOLVED) {
            mState = GAMING;
        }
    }

    int getLastStep() {
        return mLastStep;
    }

    int getManColumn() {
        return mManColumn;
    }

    int getManRow() {
        return mManRow;
    }

    String getSolvingSteps() {
        return mSolvingSteps.toString();
    }

    boolean isBoxAboveToMan() {
        return (mManRow > 0) && isBox(mManRow - 1, mManColumn);
    }

    boolean isBoxBelowToMan() {
        return ((mManRow + 1) < NUM_ROW) && isBox(mManRow + 1, mManColumn);
    }

    boolean isBoxLeftToMan() {
        return (mManColumn > 0) && isBox(mManRow, mManColumn - 1);
    }

    boolean isBoxRightToMan() {
        return ((mManColumn + 1) < NUM_COLUMN) && isBox(mManRow, mManColumn + 1);
    }

    boolean isRedoable() {
        return (mUndoHistory.length() > 0);
    }

    boolean isUndoable() {
        return (mSolvingSteps.length() > 0);
    }

    private boolean isBox(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.BOX) || (label == Sokoban.BOX_ON_GOAL);
    }

    private boolean isFloor(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.FLOOR) || (label == Sokoban.GOAL);
    }

    private boolean isGoal(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.GOAL) || (label == Sokoban.BOX_ON_GOAL) || (label == Sokoban.MAN_ON_GOAL);
    }

    private boolean isGoalUnachived(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.GOAL) || (label == Sokoban.MAN_ON_GOAL);
    }

    private boolean isMan(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == Sokoban.MAN) || (label == Sokoban.MAN_ON_GOAL);
    }

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

    private void moveBoxDown(int row, int column) {
        moveBoxOut(row, column);
        moveBoxIn(row + 1, column);
    }

    private void moveBoxLeft(int row, int column) {
        moveBoxOut(row, column);
        moveBoxIn(row, column - 1);
    }

    private void moveBoxRight(int row, int column) {
        moveBoxOut(row, column);
        moveBoxIn(row, column + 1);
    }

    private void moveBoxUp(int row, int column) {
        moveBoxOut(row, column);
        moveBoxIn(row - 1, column);
    }

    private void moveBoxIn(int row, int column) {
        if (mLabelInCells[row].charAt(column) == Sokoban.GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.BOX_ON_GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.BOX);
        }
    }

    private void moveBoxOut(int row, int column) {
        if (mLabelInCells[row].charAt(column) == Sokoban.BOX_ON_GOAL) {
            mLabelInCells[row].setCharAt(column, Sokoban.GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, Sokoban.FLOOR);
        }
    }

    private boolean moveManDown() {
        boolean done = false;

        int belowRow = mManRow + 1;

        if ((belowRow < NUM_ROW) && isFloor(belowRow, mManColumn)) {
            moveManOut();
            mManRow = belowRow;
            moveManIn();

            done = true;
        }

        return done;
    }

    private boolean moveManLeft() {
        boolean done = false;

        int leftColumn = mManColumn - 1;

        if ((leftColumn >= 0) && isFloor(mManRow, leftColumn)) {
            moveManOut();
            mManColumn = leftColumn;
            moveManIn();

            done = true;
        }

        return done;
    }

    private boolean moveManRight() {
        boolean done = false;

        int rightColumn = mManColumn + 1;

        if ((rightColumn < NUM_COLUMN) && isFloor(mManRow, rightColumn)) {
            moveManOut();
            mManColumn = rightColumn;
            moveManIn();

            done = true;
        }

        return done;
    }

    private boolean moveManUp() {
        boolean done = false;

        int aboveRow = mManRow - 1;

        if ((aboveRow >= 0) && isFloor(aboveRow, mManColumn)) {
            moveManOut();
            mManRow = aboveRow;
            moveManIn();

            done = true;
        }

        return done;
    }

    private void moveManIn() {
        if (mLabelInCells[mManRow].charAt(mManColumn) == Sokoban.GOAL) {
            mLabelInCells[mManRow].setCharAt(mManColumn, Sokoban.MAN_ON_GOAL);
        }
        else {
            mLabelInCells[mManRow].setCharAt(mManColumn, Sokoban.MAN);
        }
    }

    private void moveManOut() {
        if (mLabelInCells[mManRow].charAt(mManColumn) == Sokoban.MAN_ON_GOAL) {
            mLabelInCells[mManRow].setCharAt(mManColumn, Sokoban.GOAL);
        }
        else {
            mLabelInCells[mManRow].setCharAt(mManColumn, Sokoban.FLOOR);
        }
    }

    private boolean prevStep() {
        char step = mUndoHistory.charAt(mUndoHistory.length() - 1);

        mUndoHistory.deleteCharAt(mUndoHistory.length() - 1);

        redoStep(step);

        return false;
    }

    private boolean pushBoxDown() {
        boolean done = false;

        int belowBoxRow = mManRow + 2;

        if ((belowBoxRow < NUM_ROW) && isFloor(belowBoxRow, mManColumn)) {
            moveBoxDown(mManRow + 1, mManColumn);
            moveManDown();

            done = true;
        }

        return done;
    }

    private boolean pushBoxLeft() {
        boolean done = false;

        int leftBoxColumn = mManColumn - 2;

        if ((leftBoxColumn >= 0) && isFloor(mManRow, leftBoxColumn)) {
            moveBoxLeft(mManRow, mManColumn - 1);
            moveManLeft();

            done = true;
        }

        return done;
    }

    private boolean pushBoxRight() {
        boolean done = false;

        int rightBoxColumn = mManColumn + 2;

        if ((rightBoxColumn < NUM_COLUMN) && isFloor(mManRow, rightBoxColumn)) {
            moveBoxRight(mManRow, mManColumn + 1);
            moveManRight();

            done = true;
        }

        return done;
    }

    private boolean pushBoxUp() {
        boolean done = false;

        int aboveBoxRow = mManRow - 2;

        if ((aboveBoxRow >= 0) && isFloor(aboveBoxRow, mManColumn)) {
            moveBoxUp(mManRow - 1, mManColumn);
            moveManUp();

            done = true;
        }

        return done;
    }

    private void setLastStep(char step) {
        if (Sokoban.STEP_MOVING.indexOf(step) > -1) {
            mLastStep = STEP_MOVING;
        }

        if (Sokoban.STEP_PUSHING.indexOf(step) > -1) {
            mLastStep = STEP_PUSHING;
        }
    }
}