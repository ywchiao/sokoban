package io.tut.sokoban;

import android.graphics.Point;

import java.util.HashSet;
import java.util.Iterator;

class GameState {
    private int mManRow;
    private int mManColumn;

    private boolean mSolved;

    private HashSet<Point> mGoalCells;

    private StringBuffer[] mLabelInCells;

    GameState(String[] initialState) {
        mGoalCells = new HashSet<>();

        mLabelInCells = new StringBuffer[initialState.length];

        for (int r = 0; r < initialState.length; r ++) {
            mLabelInCells[r] = new StringBuffer(initialState[r]);

            // 搜尋 _搬運工_ 和 _目標點_ 在關卡內的初始位置
            for (int c = 0; c < mLabelInCells[r].length(); c ++) {
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

    int getManColumn() {
        return mManColumn;
    }

    int getManRow() {
        return mManRow;
    }

    boolean isBoxAboveToMan() {
        return (mManRow > 0) && isBox(mManRow - 1, mManColumn);
    }

    boolean isBoxBelowToMan() {
        return ((mManRow + 1) < GameLevels.DEFAULT_ROW_NUM) && isBox(mManRow + 1, mManColumn);
    }

    boolean isBoxLeftToMan() {
        return (mManColumn > 0) && isBox(mManRow, mManColumn - 1);
    }

    boolean isBoxRightToMan() {
        return ((mManColumn + 1) < GameLevels.DEFAULT_COLUMN_NUM) && isBox(mManRow, mManColumn + 1);
    }

    boolean isPuzzleSolved() {
        return mSolved;
    }

    void moveManDown() {
        int belowRow = mManRow + 1;

        if ((belowRow < GameLevels.DEFAULT_ROW_NUM) && isFloor(belowRow, mManColumn)) {
            moveManOut();
            mManRow = belowRow;
            moveManIn();
        }
    }

    void moveManLeft() {
        int leftColumn = mManColumn - 1;

        if ((leftColumn >= 0) && isFloor(mManRow, leftColumn)) {
            moveManOut();
            mManColumn = leftColumn;
            moveManIn();
        }
    }

    void moveManRight() {
        int rightColumn = mManColumn + 1;

        if ((rightColumn < GameLevels.DEFAULT_COLUMN_NUM) && isFloor(mManRow, rightColumn)) {
            moveManOut();
            mManColumn = rightColumn;
            moveManIn();
        }
    }

    void moveManUp() {
        int aboveRow = mManRow - 1;

        if ((aboveRow >= 0) && isFloor(aboveRow, mManColumn)) {
            moveManOut();
            mManRow = aboveRow;
            moveManIn();
        }
    }

    void pushBoxDown() {
        int belowBoxRow = mManRow + 2;

        if ((belowBoxRow < GameLevels.DEFAULT_ROW_NUM) && isFloor(belowBoxRow, mManColumn)) {
            moveBoxDown(mManRow + 1, mManColumn);
            moveManDown();

            checkPuzzleSolved();
        }
    }

    void pushBoxLeft() {
        int leftBoxColumn = mManColumn - 2;

        if ((leftBoxColumn >= 0) && isFloor(mManRow, leftBoxColumn)) {
            moveBoxLeft(mManRow, mManColumn - 1);
            moveManLeft();

            checkPuzzleSolved();
        }
    }

    void pushBoxRight() {
        int rightBoxColumn = mManColumn + 2;

        if ((rightBoxColumn < GameLevels.DEFAULT_COLUMN_NUM) && isFloor(mManRow, rightBoxColumn)) {
            moveBoxRight(mManRow, mManColumn + 1);
            moveManRight();

            checkPuzzleSolved();
        }
    }

    void pushBoxUp() {
        int aboveBoxRow = mManRow - 2;

        if ((aboveBoxRow >= 0) && isFloor(aboveBoxRow, mManColumn)) {
            moveBoxUp(mManRow - 1, mManColumn);
            moveManUp();

            checkPuzzleSolved();
        }
    }

    private void checkPuzzleSolved() {
        mSolved = true;

        for (Point cell : mGoalCells) {
            if (isGoalUnachived(cell.x, cell.y)) {
                mSolved = false;

                break;
            }
        }
    }

    private boolean isBox(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.BOX) || (label == GameLevels.BOX_ON_GOAL);
    }

    private boolean isFloor(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.FLOOR) || (label == GameLevels.GOAL);
    }

    private boolean isGoal(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.GOAL) || (label == GameLevels.BOX_ON_GOAL) || (label == GameLevels.MAN_ON_GOAL);
    }
    
    private boolean isGoalUnachived(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.GOAL) || (label == GameLevels.MAN_ON_GOAL);
    }
    
    private boolean isMan(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.MAN) || (label == GameLevels.MAN_ON_GOAL);
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
        if (mLabelInCells[row].charAt(column) == GameLevels.GOAL) {
            mLabelInCells[row].setCharAt(column, GameLevels.BOX_ON_GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, GameLevels.BOX);
        }
    }

    private void moveBoxOut(int row, int column) {
        if (mLabelInCells[row].charAt(column) == GameLevels.BOX_ON_GOAL) {
            mLabelInCells[row].setCharAt(column, GameLevels.GOAL);
        }
        else {
            mLabelInCells[row].setCharAt(column, GameLevels.FLOOR);
        }
    }

    private void moveManIn() {
        if (mLabelInCells[mManRow].charAt(mManColumn) == GameLevels.GOAL) {
            mLabelInCells[mManRow].setCharAt(mManColumn, GameLevels.MAN_ON_GOAL);
        }
        else {
            mLabelInCells[mManRow].setCharAt(mManColumn, GameLevels.MAN);
        }
    }

    private void moveManOut() {
        if (mLabelInCells[mManRow].charAt(mManColumn) == GameLevels.MAN_ON_GOAL) {
            mLabelInCells[mManRow].setCharAt(mManColumn, GameLevels.GOAL);
        }
        else {
            mLabelInCells[mManRow].setCharAt(mManColumn, GameLevels.FLOOR);
        }
    }
}