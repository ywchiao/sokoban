package io.tut.sokoban;

class GameState {
    private int mManRow;
    private int mManColumn;

    private StringBuffer[] mLabelInCells;

    GameState(String[] initialState) {
        mLabelInCells = new StringBuffer[initialState.length];

        for (int r = 0; r < initialState.length; r ++) {
            mLabelInCells[r] = new StringBuffer(initialState[r]);

            for (int c = 0; c < mLabelInCells[r].length(); c ++) {
                switch (mLabelInCells[r].charAt(c)) {
                    // 找到 _搬運工_ 在關卡內的初始位置
                    case GameLevels.MAN:
                        mManRow = r;
                        mManColumn = c;

                        break;

                    default:
                        // 刻意留白
                        break;
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

    void moveManDown() {
        int belowRow = mManRow + 1;

        if ((belowRow < GameLevels.DEFAULT_ROW_NUM) && isFloorOrGoal(belowRow, mManColumn)) {
            moveManOut();
            mManRow = belowRow;
            moveManIn();
        }
    }

    void moveManLeft() {
        int leftColumn = mManColumn - 1;

        if ((leftColumn >= 0) && isFloorOrGoal(mManRow, leftColumn)) {
            moveManOut();
            mManColumn = leftColumn;
            moveManIn();
        }
    }

    void moveManRight() {
        int rightColumn = mManColumn + 1;

        if ((rightColumn < GameLevels.DEFAULT_COLUMN_NUM) && isFloorOrGoal(mManRow, rightColumn)) {
            moveManOut();
            mManColumn = rightColumn;
            moveManIn();
        }
    }

    void moveManUp() {
        int aboveRow = mManRow - 1;

        if ((aboveRow >= 0) && isFloorOrGoal(aboveRow, mManColumn)) {
            moveManOut();
            mManRow = aboveRow;
            moveManIn();
        }
    }

    void pushBoxDown() {
        int belowBoxRow = mManRow + 2;

        if ((belowBoxRow < GameLevels.DEFAULT_ROW_NUM) && isFloorOrGoal(belowBoxRow, mManColumn)) {
            moveBoxDown(mManRow + 1, mManColumn);
            moveManDown();
        }
    }

    void pushBoxLeft() {
        int leftBoxColumn = mManColumn - 2;

        if ((leftBoxColumn >= 0) && isFloorOrGoal(mManRow, leftBoxColumn)) {
            moveBoxLeft(mManRow, mManColumn - 1);
            moveManLeft();
        }
    }

    void pushBoxRight() {
        int rightBoxColumn = mManColumn + 2;

        if ((rightBoxColumn < GameLevels.DEFAULT_COLUMN_NUM) && isFloorOrGoal(mManRow, rightBoxColumn)) {
            moveBoxRight(mManRow, mManColumn + 1);
            moveManRight();
        }
    }

    void pushBoxUp() {
        int aboveBoxRow = mManRow - 2;

        if ((aboveBoxRow >= 0) && isFloorOrGoal(aboveBoxRow, mManColumn)) {
            moveBoxUp(mManRow - 1, mManColumn);
            moveManUp();
        }
    }

    private boolean isBox(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.BOX) || (label == GameLevels.BOX_ON_GOAL);
    }

    private boolean isFloorOrGoal(int row, int column) {
        char label = mLabelInCells[row].charAt(column);

        return (label == GameLevels.FLOOR || label == GameLevels.GOAL);
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