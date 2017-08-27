package io.tut.sokoban;

import java.util.ArrayList;
import java.util.List;

class GameLevels {
    private static final GameLevels INSTANCE = new GameLevels();

    public static final int DEFAULT_ROW_NUM = 12;
    public static final int DEFAULT_COLUMN_NUM = 12;

    public static final char BOX = '$';
    public static final char FLOOR = ' ';
    public static final char GOAL = '.';
    public static final char MAN = '@';
    public static final char WALL = '#';
    public static final char EMPTY = '-'; // or '_'

    public static final char BOX_ON_GOAL = '*';
    public static final char MAN_ON_GOaL = '+';

    private static final String[] LEVEL_I = {
            "############",
            "#         .#",
            "#          #",
            "#          #",
            "#   ####   #",
            "#          #",
            "#          #",
            "#    $     #",
            "#    @     #",
            "#        . #",
            "#          #",
            "############"
    };

    private static final String[] LEVEL_II = {
            "------------",
            "------------",
            "--#######---",
            "--# ..$ #---",
            "--# # $ #---",
            "--# # # #---",
            "--# $@# #---",
            "--#.$   #---",
            "--#.#####---",
            "--###-------",
            "------------",
            "------------"
    };

    private final ArrayList<String[]> originalLevels = new ArrayList<>();

    public static GameLevels getInstance() {
        return INSTANCE;
    }

    public String[] getLevel(int level) {
        return originalLevels.get(level - 1);
    }

    public List<String> getLevelList() {
        List<String> levelList = new ArrayList<>();

        int numOfLevels = originalLevels.size();

        for (int i = 1; i <= numOfLevels; i ++) {
            levelList.add("第" + i + "關");
        }

        return levelList;
    }

    private GameLevels() {
        originalLevels.add(LEVEL_I);
        originalLevels.add(LEVEL_II);
    }
}