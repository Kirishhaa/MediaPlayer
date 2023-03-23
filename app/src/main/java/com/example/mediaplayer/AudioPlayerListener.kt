package com.example.mediaplayer

import android.media.MediaPlayer

class AudioPlayerListener: MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    override fun onPrepared(mp: MediaPlayer?) {
        (mp as AudioPlayer).playAudio()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        (mp as AudioPlayer).stopAudio()
        mp.reset()
        //transportControls?.skipToNext()
    }
}