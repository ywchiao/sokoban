package io.tut.sokoban;

class GameState {
    private int mManRow;
    private int mManColumn;
    private static final String TAG = "CYW";

    private StringBuffer[] mLabelInCells;

    GameState(String[] initialState) {
        mLabelInCells = new StringBuffer[initialState.length];

        for (int i = 0; i < initialState.length; i ++) {
            mLabelInCells[i] = new StringBuffer(initialState[i]);
        }
    }

    StringBuffer[] getLabelInCells() {
        return mLabelInCells;
    }
}