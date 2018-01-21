package com.example.android.androidquizapp.question;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.android.androidquizapp.level.Level;

/**
 * Created by Android on 05-01-2018.
 */

public interface OnQuestionPassedListener extends Parcelable {
    /**
     * This method is called when a question is passed.
     *
     * @param context - Application context.
     * @param level - This question level.
     * @param questionIndex - This question index in json questions array.
     */
    void onQuestionPassed(Context context, @NonNull Level level, int questionIndex);
}
