package io.tut.sokoban;

import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class GameActivity extends AppCompatActivity {
    public static final String KEY_SELECTED_LEVEL = "Selected_Level";

    private GameState mCurrentState;
    private MediaPlayer mMediaPlayer;
    private SoundEffect mSoundEffect;
    private View mView;

    private long mStartTime;

    private Handler mTimerHandler = new Handler();

    Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            mCurrentState.updateElapsedTime(
                getElapsedTimeString(SystemClock.uptimeMillis() - mStartTime)
            );

            mView.postInvalidate();

            mTimerHandler.postDelayed(this, 37);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int selected_level = getIntent().getIntExtra(KEY_SELECTED_LEVEL, 1);

        mCurrentState = new GameState(GameLevels.getInstance().getLevel(selected_level));
        mCurrentState.updateElapsedTime(getElapsedTimeString(0));

        mSoundEffect = new SoundEffect(this);

        mView = new GameView(this);

        setContentView(mView);
    }

    @Override
    public void onPause() {
        super.onPause();

        mMediaPlayer.stop();
        mMediaPlayer.release();

        mMediaPlayer = null;

        mTimerHandler.removeCallbacks(mTimer);
    }

    @Override
    public void onResume() {
        super.onResume();

        mMediaPlayer = MediaPlayer.create(this, R.raw.background);
        mMediaPlayer.setLooping(true);

 //       mMediaPlayer.start();

        if (mCurrentState.getState() == GameState.GAMING) {
            mTimerHandler.postDelayed(mTimer, 37);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        if (mCurrentState.getState() == GameState.STARTED) {
            mCurrentState.setState(GameState.GAMING);

            mStartTime = SystemClock.uptimeMillis();

            mTimerHandler.postDelayed(mTimer, 37);
        }

        if ((mCurrentState.getState() == GameState.SOLVED) || (mCurrentState.getState() == GameState.STUCK)) {
            mTimerHandler.removeCallbacks(mTimer);

            DialogFragment dialog = new LevelSolvedDialog();

            // 取得 _通關步驟_ 長度傳遞給 Dialog
            Bundle args = new Bundle();

            args.putBoolean("solved", mCurrentState.getState() == GameState.SOLVED);

            args.putInt("steps", mCurrentState.getSolvingSteps().length());

            args.putString("elapsed", mCurrentState.getElapsedTime());

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

    private String getElapsedTimeString(long elapsed) {
        int millis = (int) (elapsed % 1000);
        int seconds = (int) (elapsed / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        minutes = minutes % 60;
        seconds = seconds % 60;

        return getResources().getString(R.string.str_elapsed_time, hours, minutes, seconds, millis);
    }
}
