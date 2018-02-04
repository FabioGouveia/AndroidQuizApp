package com.example.android.androidquizapp.utils;

import android.content.Context;

import com.example.android.androidquizapp.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.INTERNAL;

/**
 * {@link JsonUtils} class works as a helper to JSON jobs in the app.
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

final class JsonUtils {

    //Level data persistent file name static placeholder.
    private static final String JSON_DATA_FILE = "questions.json";

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
    static JSONObject jsonFileToJSONObject(Context context, QueryUtils.StorageMode mode) throws IOException, JSONException {

        //An input stream to help us read json files
        InputStream inputStream = null;

        //Verify the storage mode
        switch(mode){
            case INTERNAL: inputStream = context.getAssets().open(JSON_DATA_FILE); break;
            case EXTERNAL: inputStream = context.openFileInput(JSON_DATA_FILE);    break;
        }

        //Byte array to work with input stream
        ByteArrayOutputStream jsonByteArray = new ByteArrayOutputStream();

        //Create a buffer
        byte[] buffer = new byte[inputStream.available()];

        int character;
        //Will input stream have characters
        while ((character = inputStream.read(buffer)) != -1) {
            jsonByteArray.write(buffer, 0, character);
        }

        //Close the input stream after work with it
        inputStream.close();

        return new JSONObject(jsonByteArray.toString("UTF-8"));
    }

    /**
     * The convertJsonObjectToQuestionObject method is used to convert a JSON object into a Question object.
     *
     * @param jsonQuestion - A {@link JSONObject}.
     * @return Question - A {@link Question} object.
     * @throws JSONException - A {@link JSONException} if something wrong happen with JSON process.
     *
     * @see Question
     */
    static Question convertJsonObjectToQuestionObject(JSONObject jsonQuestion) throws JSONException {

        JSONArray possibleAnswersArray = jsonQuestion.getJSONArray("options");
        String question = jsonQuestion.getString("question");
        String tip = jsonQuestion.getJSONArray("tip").getString(0);
        String tipWebAddress = jsonQuestion.getJSONArray("tip").getString(1);
        boolean passed = jsonQuestion.getBoolean("passed");
        int wrongAnswers = jsonQuestion.getInt("wrongAnswers");

        int numberOfPossibleAnswers = possibleAnswersArray.length();

        String[] possibleAnswers = new String[numberOfPossibleAnswers];
        boolean[] rightAnswersIndex = new boolean[numberOfPossibleAnswers];

        for (int i = 0; i < numberOfPossibleAnswers; i++) {
            possibleAnswers[i] = possibleAnswersArray.getJSONArray(i).getString(0);
            rightAnswersIndex[i] = possibleAnswersArray.getJSONArray(i).getBoolean(1);
        }

        String textualUserAnswer = null;

        //If the number of possible answers is equal to one, this is a textual answer
        if (numberOfPossibleAnswers == 1) {
            textualUserAnswer = jsonQuestion.getString("rightAnswer");
        }

        //return a new question
        return new Question(question, tip, tipWebAddress, possibleAnswers, rightAnswersIndex, passed, wrongAnswers, textualUserAnswer).addOnQuestionPassedListener(new QueryUtils.SaveOnQuestionAttemptedListener());
    }

    private static JSONObject constructInitialJSONPersistentObject(Context context, int levelNameResourceArray) throws IOException, JSONException {

        JSONObject questionsObject = JsonUtils.jsonFileToJSONObject(context, INTERNAL);
        String[] levelNameArray = context.getResources().getStringArray(levelNameResourceArray);

        //Create default question state values
        for (String name : levelNameArray) {
            JSONArray level = questionsObject.getJSONArray(name.toLowerCase());

            int numQuestions = level.length();
            for (int i = 0; i < numQuestions; i++) {

                //Check if is a textual answer question
                if (level.getJSONObject(i).getJSONArray("options").length() == 1) {
                    level.getJSONObject(i).put("rightAnswer", "null");
                }

                level.getJSONObject(i).put("passed", false);
                level.getJSONObject(i).put("wrongAnswers", 0);
            }
        }

        return questionsObject;
    }

    /**
     * The writePersistentJSONFile method is used to write persistent JSON data
     * to a file on private app data folder.
     *
     * @param context                - The application context.
     * @param levelNameResourceArray - Level names resource array, this level is maintained in arrays.xml file
     * @throws IOException - Input / Output exceptions is thrown.
     * @see IOException
     */
    static void writePersistentJSONObject(Context context, int levelNameResourceArray) throws IOException, JSONException {

        OutputStream fileOut = context.openFileOutput(JsonUtils.JSON_DATA_FILE, MODE_PRIVATE);
        OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");
        writer.write(constructInitialJSONPersistentObject(context, levelNameResourceArray).toString());
        writer.close();
    }

    /**
     * The writePersistentJSONFile method is used to write persistent JSON data
     * to a file on private app data folder.
     *
     * @param context      - The application context.
     * @param objectToSave - The JSONObject up to date and ready for save.
     * @throws IOException - Input / Output exceptions is thrown.
     * @see IOException
     */
    static void writePersistentJSONObject(Context context, JSONObject objectToSave) throws IOException, JSONException {

        OutputStream fileOut = context.openFileOutput(JsonUtils.JSON_DATA_FILE, MODE_PRIVATE);
        OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");
        writer.write(objectToSave.toString());
        writer.close();
    }
}
