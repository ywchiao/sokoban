package io.tut.sokoban;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

class BitmapManager {
    static GameBitmaps getSokobanSkin(Resources res) {
        GameBitmaps.tileSheet = BitmapFactory.decodeResource(res, R.drawable.sokoban);

        return new GameBitmaps() {
            private Rect boxOnFloor = null;
            private Rect boxOnGoal = null;
            private Rect floorBlocked = null;
            private Rect floorGoal = null;
            private Rect floorNormal = null;
            private Rect floorOutside = null;

            private Rect manFaceDown = null;
            private Rect manFaceLeft = null;
            private Rect manFaceRight = null;
            private Rect manFaceUp = null;

            private Rect[] manFacing = {
                getManFaceDown(),
                getManFaceLeft(),
                getManFaceRight(),
                getManFaceUp()
            };

            public Rect getBoxOnFloor() {
                if (boxOnFloor == null) {
                    int left = 0;
                    int top = 0;
                    int right = 63;
                    int bottom = 63;

                    boxOnFloor = new Rect(left, top, right, bottom);
                }

                return boxOnFloor;
            }

            public Rect getBoxOnGoal() {
                if (boxOnGoal == null) {
                    int left = 64;
                    int top = 0;
                    int right = 127;
                    int bottom = 63;

                    boxOnFloor = new Rect(left, top, right, bottom);
                }

                return boxOnFloor;
            }

            public Rect getFloorBlocked() {
                if (floorBlocked == null) {
                    int left = 128;
                    int top = 0;
                    int right = 191;
                    int bottom = 63;

                    floorBlocked = new Rect(left, top, right, bottom);
                }

                return floorBlocked;
            }

            public Rect getFloorGoal() {
                if (floorGoal == null) {
                    int left = 64;
                    int top = 64;
                    int right = 127;
                    int bottom = 127;

                    floorGoal = new Rect(left, top, right, bottom);
                }

                return floorGoal;
            }

            public Rect getFloorNormal() {
                if (floorNormal == null) {
                    int left = 0;
                    int top = 64;
                    int right = 63;
                    int bottom = 127;

                    floorNormal = new Rect(left, top, right, bottom);
                }

                return floorNormal;
            }

            public Rect getFloorOutside() {
                if (floorOutside == null) {
                    int left = 0;
                    int top = 64;
                    int right = 63;
                    int bottom = 127;

                    floorOutside = new Rect(left, top, right, bottom);
                }

                return floorOutside;
            }

            public Rect getMan(int to) {
                return manFacing[to];
            }

            private Rect getManFaceDown() {
                if (manFaceDown == null) {
                    int left = 64;
                    int top = 128;
                    int right = 127;
                    int bottom = 191;

                    manFaceDown = new Rect(left, top, right, bottom);
                }

                return manFaceDown;
            }

            private Rect getManFaceLeft() {
                if (manFaceLeft == null) {
                    int left = 64;
                    int top = 192;
                    int right = 127;
                    int bottom = 255;

                    manFaceLeft = new Rect(left, top, right, bottom);
                }

                return manFaceLeft;
            }

            private Rect getManFaceRight() {
                if (manFaceRight == null) {
                    int left = 0;
                    int top = 128;
                    int right = 63;
                    int bottom = 191;

                    manFaceRight = new Rect(left, top, right, bottom);
                }

                return manFaceRight;
            }

            private Rect getManFaceUp() {
                if (manFaceUp == null) {
                    int left = 0;
                    int top = 192;
                    int right = 63;
                    int bottom = 255;

                    manFaceUp = new Rect(left, top, right, bottom);
                }

                return manFaceUp;
            }
        };
    }
}