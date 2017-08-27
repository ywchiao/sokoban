package io.tut.sokoban;

import android.graphics.Bitmap;
import android.graphics.Rect;

abstract class GameBitmaps {
    static final int FACE_DOWN = 0;
    static final int FACE_LEFT = 1;
    static final int FACE_RIGHT = 2;
    static final int FACE_UP = 3;

    static Bitmap tileSheet = null;

    abstract public Rect getBoxOnFloor();

    abstract public Rect getBoxOnGoal();

    abstract public Rect getFloorBlocked();

    abstract public Rect getFloorGoal();

    abstract public Rect getFloorNormal();

    abstract public Rect getFloorOutside();

    abstract public Rect getMan(int facing);
}
