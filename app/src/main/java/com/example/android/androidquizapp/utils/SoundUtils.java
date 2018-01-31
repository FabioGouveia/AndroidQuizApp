package com.example.android.androidquizapp.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.android.androidquizapp.question.Question;

/**
 * Created by Android on 31-01-2018.
 */

public final class SoundUtils {

    //Application context
    Context context;

    //Use media player for sound effect
    private MediaPlayer mediaPlayer;
    //Handles audio focus when playing a sound
    private AudioManager audioManager;
    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMedia();
            }
        }
    };
    //On completion listener
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Clean up media player for the next file
            releaseMedia();
        }
    };

    public SoundUtils(Context context) {
        //Set up the context
        this.context = context;

        //Set up system audio manager
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /** This method plays a sound
     *
     */

    /**
     * <h1>playSound({@link Question.Sound} sound)</h1>
     * This method request audio focus to the android system
     * and plays a sound.
     *
     * @param sound A sound to play, this sound is represented by a {@link Question.Sound} constant, the possible values are
     *              "Question.Sound.CORRECT_ANSWER"
     *              "Question.Sound.WRONG_ANSWER"
     *              "Question.Sound.LEVEL_PASSED"
     * @see Question
     */
    public void playSound(Question.Sound sound) {
        // Request audio focus so in order to play the audio file. The app needs to play a
        // short audio file, so we will request audio focus with a short amount of time
        // with AUDIOFOCUS_GAIN_TRANSIENT.
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // We have audio focus now.

            // Create and setup the {@link MediaPlayer} for the audio resource associated
            // with the current word
            mediaPlayer = MediaPlayer.create(context, sound.getResourceID());

            // Start the audio file
            mediaPlayer.start();

            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sound has finished playing.
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    public void releaseMedia() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}
