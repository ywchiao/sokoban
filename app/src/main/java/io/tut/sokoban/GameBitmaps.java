package io.tut.sokoban;

import android.graphics.Bitmap;
import android.graphics.Rect;

abstract class GameBitmaps {
    static final int FACE_DOWN = 0;
    static final int FACE_LEFT = 1;
    static final int FACE_RIGHT = 2;
    static final int FACE_UP = 3;

    static Bitmap tileSheet = null;

    abstract public Rect getTileBoxOnFloor();

    abstract public Rect getTileBoxOnGoal();

    abstract public Rect getTileEmpty();

    abstract public Rect getTileFloor();

    abstract public Rect getTileGoal();

    abstract public Rect getTileWall();

    abstract public Rect getTileMan(int facing);
}