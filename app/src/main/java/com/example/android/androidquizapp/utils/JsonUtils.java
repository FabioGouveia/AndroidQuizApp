package com.example.android.androidquizapp.utils;

import android.content.Context;

import com.example.android.androidquizapp.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 24-01-2018.
 */

public final class JsonUtils {

    //Level data persistent file name static placeholder.
    static final String JSON_DATA_FILE = "questions.json";

    /**
     * Create a private constructor because no one should ever create a {@link JsonUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name JsonUtils (and an object instance of JsonUtils is not needed).
     */
    private JsonUtils(){}

    /**
     *
     * The jsonFileToJSONObject method help us get a JSONObject from
     * a JSON string stored on a json file.
     * You need to declare a storage mode using QueryUtils.StorageMode.INTERNAL to use
     * json internal file or QueryUtils.StorageMode.External to use the external json file.
     *
     * @param context - The application context.
     * @param mode . The storage.
     *
     * @return JSONObject - A JSONObject representing the level questions.
     */
    public static JSONObject jsonFileToJSONObject(Context context, QueryUtils.StorageMode mode) throws IOException, JSONException {

        //An input stream to help us read json files
        InputStream inputStream = null;

        //Verify the storage mode
        switch(mode){
            case INTERNAL: inputStream = context.getAssets().open(JSON_DATA_FILE); break;
            case EXTERNAL: inputStream = context.openFileInput(JSON_DATA_FILE);    break;
        }

        int size = inputStream.available();//The number of bytes that can be read from this input stream
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();

        String jsonString = new String(buffer, "UTF-8");
        return new JSONObject(jsonString);
    }

    /**
     * The convertJsonObjectToQuestionObject method is used to convert a JSON object into a Question object.
     *
     * @param jsonQuestion - A {@link JSONObject}.
     *
     * @return Question - A {@link Question} object.
     *
     * @throws JSONException - A {@link JSONException} if something wrong happen with JSON process.
     *
     * @see Question
     */
    public static Question convertJsonObjectToQuestionObject(JSONObject jsonQuestion) throws JSONException {

        JSONArray possibleAnswersArray = jsonQuestion.getJSONArray("options");
        String question = jsonQuestion.getString("question");
        String tip = jsonQuestion.getJSONArray("tip").getString(0);
        boolean passed = jsonQuestion.getBoolean("passed");
        int wrongAnswers = jsonQuestion.getInt("wrongAnswers");

        int numberOfPossibleAnswers = possibleAnswersArray.length();

        String[] possibleAnswers = new String[numberOfPossibleAnswers];
        boolean[] rightAnswersIndex = new boolean[numberOfPossibleAnswers];

        for (int i = 0; i < numberOfPossibleAnswers; i++) {
            possibleAnswers[i] = possibleAnswersArray.getJSONArray(i).getString(0);
            rightAnswersIndex[i] = possibleAnswersArray.getJSONArray(i).getBoolean(1);
        }

        return new Question(question, tip, possibleAnswers, rightAnswersIndex, passed, wrongAnswers).addOnQuestionPassedListener(new QueryUtils.SaveOnQuestionAttemptedListener());
    }
}
