package io.tut.sokoban;

import java.util.ArrayList;
import java.util.List;

class GameLevels {
    private static final String[] LEVEL_I = new String[] {
            "############",
            "#         .#",
            "#          #",
            "#          #",
            "#   ####   #",
            "#          #",
            "#          #",
            "#    $     #",
            "#    @     #",
            "#          #",
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

    // 注意：因為 "初始化順序“ (initialization order) 的問題，
    // static final GameLevels INSTACE "一定" 放在最後。
    private static final GameLevels INSTANCE = new GameLevels();

    private final ArrayList<String[]> originalLevels = new ArrayList<>();

    static GameLevels getInstance() {
        return INSTANCE;
    }

    String[] getLevel(int level) {
        return originalLevels.get(level - 1);
    }

    List<String> getLevelList() {
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