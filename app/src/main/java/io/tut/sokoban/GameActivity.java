package io.tut.sokoban;

import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class GameActivity extends AppCompatActivity {
    public static final String KEY_SELECTED_LEVEL = "Selected_Level";

    private GameState mCurrentState;
    private MediaPlayer mMediaPlayer;
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

    @Override
    public void onPause() {
        super.onPause();

        mMediaPlayer.stop();
        mMediaPlayer.release();

        mMediaPlayer = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMediaPlayer = MediaPlayer.create(this, R.raw.background);
        mMediaPlayer.setLooping(true);

        mMediaPlayer.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }


        if (mCurrentState.isPuzzleSolved()) {
            DialogFragment dialog = new LevelSolvedDialog();

            // 取得 _通關步驟_ 長度傳遞給 Dialog
            Bundle args = new Bundle();

            args.putInt("steps", mCurrentState.getSolvingSteps().length());
            dialog.setArguments(args);

            dialog.show(getFragmentManager(), "tag");
        }

        return true;
    }

    public GameState getCurrentState() {
        return mCurrentState;
    }

    public SoundEffect getSoundEffect() {
        return mSoundEffect;
    }
}
