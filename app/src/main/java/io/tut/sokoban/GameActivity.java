package io.tut.sokoban;

import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class GameActivity extends AppCompatActivity {
    private static final String TAG="SSSSSSS";
    public static final String KEY_SELECTED_LEVEL = "Selected_Level";

    private GameState mCurrentState;
    private SoundEffect mSoundEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int selected_level = getIntent().getIntExtra(KEY_SELECTED_LEVEL, 1);

        mCurrentState = new GameState(GameLevels.getInstance().getLevel(selected_level));
        mSoundEffect = new SoundEffect(this);

        GameView gameView = new GameView(this);

        setContentView(gameView);
    }

    public GameState getCurrentState() {
        return mCurrentState;
    }

    public SoundEffect getSoundEffect() {
        return mSoundEffect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }


        if (mCurrentState.isPuzzleSolved()) {
            DialogFragment dialog = new PuzzleSolvedDialogFragment();

            dialog.show(getFragmentManager(), "tag");
        }

        return true;
    }
}
