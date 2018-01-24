package com.example.android.androidquizapp.utils;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.androidquizapp.level.Level;
import com.example.android.androidquizapp.question.OnQuestionAttemptedListener;
import com.example.android.androidquizapp.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;

import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.EXTERNAL;
import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.INTERNAL;

/**
 * Created by Android on 20-12-2017.
 */

public final class QueryUtils {

    //Type of file storage mode.
    enum StorageMode{
        INTERNAL,
        EXTERNAL
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){}



    /**
     * The writePersistentJSONFile method is used to write persistent JSON data
     * to a file on private app data folder.
     *
     * @param context - The application context.
     * @param jsonObject - A JSONObject to write.
     * @throws IOException - Input / Output exceptions is thrown.
     *
     * @see IOException
     */
    private static void writePersistentJSONFile(Context context, JSONObject jsonObject) throws  IOException{

            OutputStream fileOut = context.openFileOutput(JsonUtils.JSON_DATA_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");
            writer.write(jsonObject.toString());
            writer.close();
    }

    public static void createPersistentData(Context context, int levelNameResourceArray) throws  IOException, JSONException{
        JSONObject questionsObject = JsonUtils.jsonFileToJSONObject(context, INTERNAL);
        String[] levelNameArray = context.getResources().getStringArray(levelNameResourceArray);

        //Create default question state values
        for (String name : levelNameArray) {
            JSONArray level = questionsObject.getJSONArray(name.toLowerCase());

            int numQuestions = level.length();
            for (int i = 0; i < numQuestions; i++) {
                level.getJSONObject(i).put("passed", false);
                level.getJSONObject(i).put("wrongAnswers", 0);
            }
        }

        writePersistentJSONFile(context, questionsObject);

    }


    public static ArrayList<Question> extractQuestions(Context context, String level){

        // Create an empty ArrayList to store the questions.
        ArrayList<Question> questionList = new ArrayList<>();

        //Get questions state.


        try{
            //Create a JSON object to deal with question data
            JSONObject jsonObject = JsonUtils.jsonFileToJSONObject(context, EXTERNAL);
            JSONArray questionArray = jsonObject.getJSONArray(level.toLowerCase());

            int numberOfQuestions = questionArray.length();
            for(int i = 0; i < numberOfQuestions; i++){
                JSONObject jsonQuestion = questionArray.getJSONObject(i);

                questionList.add(JsonUtils.convertJsonObjectToQuestionObject(jsonQuestion));

            }

        }catch(IOException | JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the question JSON results", e);
        }

        return questionList;
    }


    /**
     * The {@link SaveOnQuestionAttemptedListener} class is used to save passed question persistent state, this is possible trough the implementation of {@link OnQuestionAttemptedListener}.
     * The persistent question data is saved when this class receives a callback from {@link OnQuestionAttemptedListener} trough the override method onQuestionPassed().
     */
    public static class SaveOnQuestionAttemptedListener implements OnQuestionAttemptedListener {

        public SaveOnQuestionAttemptedListener(){}

        protected SaveOnQuestionAttemptedListener(Parcel in) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SaveOnQuestionAttemptedListener> CREATOR = new Creator<SaveOnQuestionAttemptedListener>() {
            @Override
            public SaveOnQuestionAttemptedListener createFromParcel(Parcel in) {
                return new SaveOnQuestionAttemptedListener(in);
            }

            @Override
            public SaveOnQuestionAttemptedListener[] newArray(int size) {
                return new SaveOnQuestionAttemptedListener[size];
            }
        };

        /**
         * This method is called when a question is passed.
         *
         * @param context - Application context.
         * @param level - This question level.
         * @param questionIndex - This question index in json questions array.
         */
        @Override
        public void onQuestionPassed(Context context, @NonNull Level level, int questionIndex) {
            try {
                JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                //Update question passed state.
                jsonQuestionsArray.getJSONObject(questionIndex).put("passed", true);

                //Write persistent data.
                writePersistentJSONFile(context, jsonQuestionsObject);

            }catch(IOException | JSONException e){e.printStackTrace();}
        }

        @Override
        public void onQuestionFailed(Context context, @NonNull Level level, int questionIndex) {
            //Increment wrong answers.
            try {
                JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                int wrongAnswers = jsonQuestionsArray.getJSONObject(questionIndex).getInt("wrongAnswers");
                wrongAnswers++;

                //Update question passed state.
                jsonQuestionsArray.getJSONObject(questionIndex).put("wrongAnswers", wrongAnswers);

                //Write persistent data.
                writePersistentJSONFile(context, jsonQuestionsObject);

            }catch(IOException | JSONException e){e.printStackTrace();}
        }
    }

    /**
     * This method clean all levels progress.
     *
     * @param context - Application context.
     * @param levels - Collection with levels to clean.
     */
    public static void cleanProgress(Context context, Collection<Level> levels){
        try{
            //JSON main object containing all level questions.
            JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);

            //Iterate over all levels.
            for (Level level: levels) {

                //JSONArray with this level questions inside.
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                //Number of questions this level have.
                int numQuestion = jsonQuestionsArray.length();
                //Iterate over all questions.
                for (int i = 0; i < numQuestion; i++) {
                    //Update question passed state to false.
                    jsonQuestionsArray.getJSONObject(i).put("passed", false);
                    jsonQuestionsArray.getJSONObject(i).put("wrongAnswers", 0);
                }

            }

            //Write persistent data.
            writePersistentJSONFile(context, jsonQuestionsObject);

        }catch(IOException | JSONException e){e.printStackTrace();}
    }
}
