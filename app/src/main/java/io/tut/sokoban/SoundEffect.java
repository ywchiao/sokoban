package io.tut.sokoban;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 負責音效播放的類別。
 */
class SoundEffect {
    private final int mSoundPushing;
    private final int mSoundWalking;

    private final SoundPool mSoundPool;

    /**
     * 建構子。
     *
     * @param context 擁有這個 SoundEffect 物件的 Activity。
     */
    SoundEffect(Context context) {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);

        mSoundWalking = mSoundPool.load(context, R.raw.walking, 1);
        mSoundPushing = mSoundPool.load(context, R.raw.pushing, 1);
    }

    /**
     * 播放走路的音效。
     */
    void playWalkingEffect() {
        mSoundPool.play(mSoundWalking, 1.0f, 1.0f, 1, 0, 1);
    }

    /**
     * 播放推箱子的音效。
     */
    void playPushingEffect() {
        mSoundPool.play(mSoundWalking, 1.0f, 1.0f, 1, 0, 1);
        mSoundPool.play(mSoundPushing, 1.0f, 1.0f, 1, 0, 1);
    }
}
