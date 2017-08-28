package io.tut.sokoban;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

class BitmapManager {
    static GameBitmaps getSokobanSkin(Resources res) {
        GameBitmaps.tileSheet = BitmapFactory.decodeResource(res, R.drawable.sokoban);

        return new GameBitmaps() {
            private final int TILE_PER_LINE = 8;
            private final float TILE_WIDTH = GameBitmaps.tileSheet.getWidth() / TILE_PER_LINE;

            private Rect tileBlank = null;
            private Rect tileBoxOnFloor = null;
            private Rect tileBoxOnGoal = null;
            private Rect tileEmpty = null;
            private Rect tileFloor = null;
            private Rect tileGoal = null;
            private Rect tileWall = null;

            private Rect tileManFaceDown = null;
            private Rect tileManFaceLeft = null;
            private Rect tileManFaceRight = null;
            private Rect tileManFaceUp = null;

            private final Rect[] tileManFacing = {
                getTileManFaceDown(),
                getTileManFaceLeft(),
                getTileManFaceRight(),
                getTileManFaceUp()
            };

            public Rect getTileBlank() {
                if (tileBlank == null) {
                    int left = (int) (TILE_WIDTH * 3);
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 4) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    tileBlank = new Rect(left, top, right, bottom);
                }

                return tileBlank;
            }

            public Rect getTileBoxOnFloor() {
                if (tileBoxOnFloor == null) {
                    int left = 0;
                    int top = 0;
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    tileBoxOnFloor = new Rect(left, top, right, bottom);
                }

                return tileBoxOnFloor;
            }

            public Rect getTileBoxOnGoal() {
                if (tileBoxOnGoal == null) {
                    int left = (int) TILE_WIDTH;
                    int top = 0;
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    tileBoxOnFloor = new Rect(left, top, right, bottom);
                }

                return tileBoxOnFloor;
            }

            public Rect getTileEmpty() {
                if (tileEmpty == null) {
                    int left = (int) (TILE_WIDTH * 2);
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 3) - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    tileEmpty = new Rect(left, top, right, bottom);
                }

                return tileEmpty;
            }

            public Rect getTileFloor() {
                if (tileFloor == null) {
                    int left = 0;
                    int top = (int) TILE_WIDTH;
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    tileFloor = new Rect(left, top, right, bottom);
                }

                return tileFloor;
            }

            public Rect getTileGoal() {
                if (tileGoal == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    tileGoal = new Rect(left, top, right, bottom);
                }

                return tileGoal;
            }

            public Rect getTileMan(int to) {
                return tileManFacing[to];
            }

            public Rect getTileWall() {
                if (tileWall == null) {
                    int left = (int) (TILE_WIDTH * 2);
                    int top = 0;
                    int right = (int) (TILE_WIDTH * 3) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    tileWall = new Rect(left, top, right, bottom);
                }

                return tileWall;
            }

            private Rect getTileManFaceDown() {
                if (tileManFaceDown == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) (TILE_WIDTH * 2);
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 3) - 1;

                    tileManFaceDown = new Rect(left, top, right, bottom);
                }

                return tileManFaceDown;
            }

            private Rect getTileManFaceLeft() {
                if (tileManFaceLeft == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) (TILE_WIDTH * 3);
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 4) - 1;

                    tileManFaceLeft = new Rect(left, top, right, bottom);
                }

                return tileManFaceLeft;
            }

            private Rect getTileManFaceRight() {
                if (tileManFaceRight == null) {
                    int left = 0;
                    int top = (int) (TILE_WIDTH * 2);
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 3) - 1;

                    tileManFaceRight = new Rect(left, top, right, bottom);
                }

                return tileManFaceRight;
            }

            private Rect getTileManFaceUp() {
                if (tileManFaceUp == null) {
                    int left = 0;
                    int top = (int) (TILE_WIDTH * 3);
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 4) - 1;

                    tileManFaceUp = new Rect(left, top, right, bottom);
                }

                return tileManFaceUp;
            }
        };
    }
}