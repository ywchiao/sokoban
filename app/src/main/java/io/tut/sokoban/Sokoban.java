package io.tut.sokoban;

abstract class Sokoban {
    // XSB 檔案格式
    static final char BOX = '$';
    static final char BOX_ON_GOAL = '*';
    static final char FLOOR = ' ';
    static final char GOAL = '.';
    static final char MAN = '@';
    static final char MAN_ON_GOAL = '+';
    static final char WALL = '#';
    static final char EMPTY = '-';
    static final char EMPTY_ALT = '_';

    // LURD 解答格式
    static final char MOVE_LEFT = 'l';
    static final char MOVE_UP = 'u';
    static final char MOVE_RIGHT = 'r';
    static final char MOVE_DOWN = 'd';
    static final char PUSH_LEFT = 'L';
    static final char PUSH_UP = 'U';
    static final char PUSH_RIGHT = 'R';
    static final char PUSH_DOWN = 'D';
}
