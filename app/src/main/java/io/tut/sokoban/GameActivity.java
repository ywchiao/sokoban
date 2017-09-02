package io.tut.sokoban;

import android.app.DialogFragment;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * GameActivity，負責遊戲關卡進行的 Activity 物件。每個關卡都會建立自己的 GameActivity
 * 物件。
 */
public class GameActivity extends AppCompatActivity {
    public static final String KEY_SELECTED_LEVEL = "Selected_Level";

    // 操作 _計時器_ 的 Handler 物件
    private final Handler mTimerHandler = new Handler();

    // 計時器；定時更新關卡使用時間
    private final Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            mCurrentState.updateElapsedTime(
                SystemClock.uptimeMillis() - mStartTime
            );

            mView.postInvalidate();

            mTimerHandler.postDelayed(this, 37);
        }
    };

    private long mStartTime;

    private GameState mCurrentState;
    private MediaPlayer mMediaPlayer;
    private SoundEffect mSoundEffect;
    private View mView;

    /**
     * 遊戲關卡啟動；初始化 GameState 物件，SoundEffect 物件，和 GameView 物件。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int selected_level = getIntent().getIntExtra(KEY_SELECTED_LEVEL, 1);

        mCurrentState = new GameState(GameLevels.getInstance().getLevel(selected_level));
        //mCurrentState.updateElapsedTime(0l);

        mSoundEffect = new SoundEffect(this);

        mView = new GameView(this);

        setContentView(mView);
    }

    /**
     * App 失去執行權，中止_背景音樂_，釋放 MediaPlayer 資源；中止 _計時_ 器。
     */
    @Override
    public void onPause() {
        super.onPause();

        mMediaPlayer.stop();
        mMediaPlayer.release();

        mMediaPlayer = null;

        mTimerHandler.removeCallbacks(mTimer);
    }

    /**
     * App 重新取得執行權，起動 _背景音樂_，起動 _計時_ 器。
     */
    @Override
    public void onResume() {
        super.onResume();

        mMediaPlayer = MediaPlayer.create(this, R.raw.background);
        mMediaPlayer.setLooping(true);

        mMediaPlayer.start();

        if (mCurrentState.getGameStatus() == GameState.GAMING) {
            mTimerHandler.postDelayed(mTimer, 37);
        }
    }

    /**
     * TouchEvent 由 View 物件傳回後，作最後的遊戲狀態檢查以控制遊戲流程。
     *
     * @param event
     *
     * @return true 消耗掉 event。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        // 使用者按下 _開始_ 按鈕，進入計時模式。
        if (mCurrentState.getGameStatus() == GameState.STARTED) {
            mCurrentState.setGameStatus(GameState.GAMING);

            mStartTime = SystemClock.uptimeMillis();

            mTimerHandler.postDelayed(mTimer, 37);
        }

        // 檢查遊戲是否 _通關_ 或者 _放棄_ 顯示相應的 _對話框_ (dialog)。
        if ((mCurrentState.getGameStatus() == GameState.SOLVED) || (mCurrentState.getGameStatus() == GameState.STUCK)) {
            mTimerHandler.removeCallbacks(mTimer);

            DialogFragment dialog = new LevelClosingDialog();

            // 取得 _通關步驟_ 長度，與 _通關時間_ 傳遞給 Dialog
            Bundle args = new Bundle();

            args.putBoolean("solved", mCurrentState.getGameStatus() == GameState.SOLVED);

            args.putInt("steps", mCurrentState.getSolvingSteps().length());

            args.putString("elapsed", ((GameView) mView).getElapsedTime());

            dialog.setArguments(args);

            dialog.show(getFragmentManager(), "tag");
        }

        return true;
    }

    /**
     * 傳回目前關卡的 GameState (遊戲狀態) 物件。
     *
     * @return GameState 物件。
     */
    public GameState getCurrentState() {
        return mCurrentState;
    }

    /**
     * 傳回目前關卡的 SoundEffect (音效) 物件。
     *
     * @return SoundEffect 物件。
     */
    public SoundEffect getSoundEffect() {
        return mSoundEffect;
    }
}

// GameActivity.java