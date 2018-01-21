package com.example.android.androidquizapp.utils;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.androidquizapp.level.Level;
import com.example.android.androidquizapp.question.OnQuestionPassedListener;
import com.example.android.androidquizapp.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;

import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.EXTERNAL;
import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.INTERNAL;

/**
 * Created by Android on 20-12-2017.
 */

public class QueryUtils {

    //Level data persistent file name static placeholder.
    private static final String JSON_DATA_FILE = "questions.json";

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
    private static JSONObject jsonFileToJSONObject(Context context, StorageMode mode) throws IOException, JSONException{

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
     *
     * @param context
     * @param jsonObject
     * @throws IOException
     */
    private static void writePersistentJSONFile(Context context, JSONObject jsonObject) throws  IOException{

            OutputStream fileOut = context.openFileOutput(JSON_DATA_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");
            writer.write(jsonObject.toString());
            writer.close();
    }

    public static void createPersistentData(Context context, int levelNameResourceArray) throws  IOException, JSONException{
        JSONObject questionsObject = jsonFileToJSONObject(context, INTERNAL);
        String[] levelNameArray = context.getResources().getStringArray(levelNameResourceArray);

        //Create default question state values
        for (String name : levelNameArray) {
            JSONArray level = questionsObject.getJSONArray(name.toLowerCase());

            int numQuestions = level.length();
            for (int i = 0; i < numQuestions; i++) {
                level.getJSONObject(i).put("passed", false);
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
            JSONObject jsonObject = jsonFileToJSONObject(context, EXTERNAL);
            JSONArray questionArray = jsonObject.getJSONArray(level.toLowerCase());

            int numberOfQuestions = questionArray.length();
            for(int i = 0; i < numberOfQuestions; i++){
                JSONObject jsonQuestion = questionArray.getJSONObject(i);

                questionList.add(convertJsonObjectToQuestionObject(jsonQuestion));

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
     * The private convertJsonObjectToQuestionObject method helps converting a JSON object into a Question object.
     *
     * @param jsonQuestion - A {@link JSONObject}.
     *
     * @return Question - A {@link Question} object.
     *
     * @throws JSONException - A {@link JSONException} if something wrong happen with JSON process.
     *
     * @see Question
     */
    private static Question convertJsonObjectToQuestionObject(JSONObject jsonQuestion) throws JSONException {

        JSONArray possibleAnswersArray = jsonQuestion.getJSONArray("options");
        String question = jsonQuestion.getString("question");
        String tip = jsonQuestion.getJSONArray("tip").getString(0);
        boolean passed = jsonQuestion.getBoolean("passed");

        int numberOfPossibleAnswers = possibleAnswersArray.length();

        String[] possibleAnswers = new String[numberOfPossibleAnswers];
        boolean[] rightAnswersIndex = new boolean[numberOfPossibleAnswers];

        for (int i = 0; i < numberOfPossibleAnswers; i++) {
            possibleAnswers[i] = possibleAnswersArray.getJSONArray(i).getString(0);
            rightAnswersIndex[i] = possibleAnswersArray.getJSONArray(i).getBoolean(1);
        }

        return new Question(question, tip, possibleAnswers, rightAnswersIndex, passed).addOnQuestionPassedListener(new SaveOnQuestionPassedListener());
    }

    /**
     * The {@link SaveOnQuestionPassedListener} class is used to save passed question persistent state, this is possible trough the implementation of {@link OnQuestionPassedListener}.
     * The persistent question data is saved when this class receives a callback from {@link OnQuestionPassedListener} trough the override method onQuestionPassed().
     */
    private static class SaveOnQuestionPassedListener implements OnQuestionPassedListener{

        public SaveOnQuestionPassedListener(){}

        protected SaveOnQuestionPassedListener(Parcel in) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SaveOnQuestionPassedListener> CREATOR = new Creator<SaveOnQuestionPassedListener>() {
            @Override
            public SaveOnQuestionPassedListener createFromParcel(Parcel in) {
                return new SaveOnQuestionPassedListener(in);
            }

            @Override
            public SaveOnQuestionPassedListener[] newArray(int size) {
                return new SaveOnQuestionPassedListener[size];
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
                JSONObject jsonQuestionsObject = jsonFileToJSONObject(context, StorageMode.EXTERNAL);
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                //Update question passed state.
                jsonQuestionsArray.getJSONObject(questionIndex).put("passed", true);

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
            JSONObject jsonQuestionsObject = jsonFileToJSONObject(context, StorageMode.EXTERNAL);

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
                }

            }

            //Write persistent data.
            writePersistentJSONFile(context, jsonQuestionsObject);

        }catch(IOException | JSONException e){e.printStackTrace();}
    }
}
