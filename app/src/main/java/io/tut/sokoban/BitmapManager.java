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

            private Rect mTileBlank = null;
            private Rect mTileBoxOnFloor = null;
            private Rect mTileBoxOnGoal = null;
            private Rect mTileEmpty = null;
            private Rect mTileFloor = null;
            private Rect mTileGoal = null;
            private Rect mTileWall = null;

            private Rect mTileManFaceDown = null;
            private Rect mTileManFaceLeft = null;
            private Rect mTileManFaceRight = null;
            private Rect mTileManFaceUp = null;

            private final Rect[] tileManFacing = {
                getTileManFaceDown(),
                getTileManFaceLeft(),
                getTileManFaceRight(),
                getTileManFaceUp()
            };

            /**
             * 傳回 bitmap 上 Blank 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileBlank() {
                if (mTileBlank == null) {
                    int left = (int) (TILE_WIDTH * 3);
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 4) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    mTileBlank = new Rect(left, top, right, bottom);
                }

                return mTileBlank;
            }

            /**
             * 傳回 bitmap 上 BoxOnFloor 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileBoxOnFloor() {
                if (mTileBoxOnFloor == null) {
                    int left = 0;
                    int top = 0;
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    mTileBoxOnFloor = new Rect(left, top, right, bottom);
                }

                return mTileBoxOnFloor;
            }

            /**
             * 傳回 bitmap 上 BoxOnGoal 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileBoxOnGoal() {
                if (mTileBoxOnGoal == null) {
                    int left = (int) TILE_WIDTH;
                    int top = 0;
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    mTileBoxOnGoal = new Rect(left, top, right, bottom);
                }

                return mTileBoxOnGoal;
            }

            /**
             * 傳回 bitmap 上 Empty 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileEmpty() {
                if (mTileEmpty == null) {
                    int left = (int) (TILE_WIDTH * 2);
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 3) - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    mTileEmpty = new Rect(left, top, right, bottom);
                }

                return mTileEmpty;
            }

            /**
             * 傳回 bitmap 上 Floor 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileFloor() {
                if (mTileFloor == null) {
                    int left = 0;
                    int top = (int) TILE_WIDTH;
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    mTileFloor = new Rect(left, top, right, bottom);
                }

                return mTileFloor;
            }

            /**
             * 傳回 bitmap 上 Goal 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileGoal() {
                if (mTileGoal == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) TILE_WIDTH;
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 2) - 1;

                    mTileGoal = new Rect(left, top, right, bottom);
                }

                return mTileGoal;
            }

            /**
             * 傳回 bitmap 上 Man 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileMan(int to) {
                return tileManFacing[to];
            }

            /**
             * 傳回 bitmap 上 Wall 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            public Rect getTileWall() {
                if (mTileWall == null) {
                    int left = (int) (TILE_WIDTH * 2);
                    int top = 0;
                    int right = (int) (TILE_WIDTH * 3) - 1;
                    int bottom = (int) TILE_WIDTH - 1;

                    mTileWall = new Rect(left, top, right, bottom);
                }

                return mTileWall;
            }

            /**
             * 傳回 bitmap 上 ManFaceDown 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            private Rect getTileManFaceDown() {
                if (mTileManFaceDown == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) (TILE_WIDTH * 2);
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 3) - 1;

                    mTileManFaceDown = new Rect(left, top, right, bottom);
                }

                return mTileManFaceDown;
            }

            /**
             * 傳回 bitmap 上 ManFaceLeft 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            private Rect getTileManFaceLeft() {
                if (mTileManFaceLeft == null) {
                    int left = (int) TILE_WIDTH;
                    int top = (int) (TILE_WIDTH * 3);
                    int right = (int) (TILE_WIDTH * 2) - 1;
                    int bottom = (int) (TILE_WIDTH * 4) - 1;

                    mTileManFaceLeft = new Rect(left, top, right, bottom);
                }

                return mTileManFaceLeft;
            }

            /**
             * 傳回 bitmap 上 ManFaceRight 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            private Rect getTileManFaceRight() {
                if (mTileManFaceRight == null) {
                    int left = 0;
                    int top = (int) (TILE_WIDTH * 2);
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 3) - 1;

                    mTileManFaceRight = new Rect(left, top, right, bottom);
                }

                return mTileManFaceRight;
            }

            /**
             * 傳回 bitmap 上 ManFaceUp 的 Tile 區域 Rect 物件。
             *
             * @return Rect 物件。
             */
            private Rect getTileManFaceUp() {
                if (mTileManFaceUp == null) {
                    int left = 0;
                    int top = (int) (TILE_WIDTH * 3);
                    int right = (int) TILE_WIDTH - 1;
                    int bottom = (int) (TILE_WIDTH * 4) - 1;

                    mTileManFaceUp = new Rect(left, top, right, bottom);
                }

                return mTileManFaceUp;
            }
        };
    }
}