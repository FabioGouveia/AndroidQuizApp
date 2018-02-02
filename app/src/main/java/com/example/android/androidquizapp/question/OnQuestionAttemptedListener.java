package com.example.android.androidquizapp.question;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.android.androidquizapp.level.Level;

/**
 * The {@link OnQuestionAttemptedListener} acts as a contract between
 * classes that implement it and the classes that calls it.
 * This listener is used basically for callbacks between objects.
 */

public interface OnQuestionAttemptedListener extends Parcelable {
    /**
     * This method is called when a question is passed.
     *
     * @param context
     *          Application context.
     *
     * @param level
     *          This question level.
     *
     * @param questionIndex
     *          This question index in json questions array.
     */
    void onQuestionPassed(Context context, @NonNull Level level, int questionIndex);

    /**
     * This method is called when a question is passed.
     *
     * @param context       Application context.
     * @param level         This question level.
     * @param questionIndex This question index in json questions array.
     * @param answer        The correct answer.
     */
    void onQuestionPassed(Context context, @NonNull Level level, int questionIndex, String answer);

    /**
     * This method is called when a question is failed.
     *
     * @param context       Application context.
     * @param level         This question level.
     * @param questionIndex This question index in json questions array.
     */
    void onQuestionFailed(Context context, @NonNull Level level, int questionIndex);
}
