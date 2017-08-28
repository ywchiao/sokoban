package io.tut.sokoban;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {
    public static final String KEY_SELECTED_LEVEL = "Selected_Level";

    private GameState mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int selected_level = getIntent().getIntExtra(KEY_SELECTED_LEVEL, 1);

        mCurrentState = new GameState(GameLevels.getInstance().getLevel(selected_level));

        GameView gameView = new GameView(this);

        setContentView(gameView);
    }

    public GameState getCurrentState() {
        return mCurrentState;
    }
}
