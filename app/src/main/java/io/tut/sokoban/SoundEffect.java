package io.tut.sokoban;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class SoundEffect {
    private int mSoundWalking;
    private int mSoundPushing;

    private SoundPool mSoundPool;

    SoundEffect(Context context) {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);

        mSoundWalking = mSoundPool.load(context, R.raw.walking, 1);
        mSoundPushing = mSoundPool.load(context, R.raw.pushing, 1);
    }

    void playWalkingEffect() {
        mSoundPool.play(mSoundWalking, 0.9f, 0.9f, 1, 0, 1);
    }

    void playPushingEffect() {
        mSoundPool.play(mSoundWalking, 0.9f, 0.9f, 1, 0, 1);
        mSoundPool.play(mSoundPushing, 0.9f, 0.9f, 1, 0, 1);
    }
}
