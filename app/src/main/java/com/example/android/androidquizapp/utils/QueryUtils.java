package com.example.android.androidquizapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;
import com.example.android.androidquizapp.question.OnQuestionAttemptedListener;
import com.example.android.androidquizapp.question.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.example.android.androidquizapp.utils.QueryUtils.StorageMode.EXTERNAL;

/**
 * {@link QueryUtils} class works as controller for all the queries in the app.
 *
 * @author Fábio Gouveia
 * @version 1.0
 */
public final class QueryUtils {

    public final static String PREFERENCES_FILE = "prefs";
    public final static String FIRST_TIME_USAGE_KEY = "app_first_time_used_key";
    public final static String PLAY_INITIAL_SLIDE_KEY = "play_initial_slide_key";
    public final static String PLAY_SOUND_EFFECTS_KEY = "play_sound_effects_key";
    public final static String PROGRESS_ERASED_KEY = "progress_erased_key";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){}

    /**
     * This method creates the persistent data of this app.
     *
     * @param context                Application context.
     * @param levelNameResourceArray Resource array with level names on it.
     * @param prefs                  SharedPreferences object to commit.
     * @throws IOException   throws a input output exception if something happen with IO process.
     * @throws JSONException throws a JSON exception if something happen with JSON process.
     */
    public static void createPersistentData(Context context, int levelNameResourceArray, SharedPreferences prefs) throws IOException, JSONException {
        //Create all the initial persistent stuff
        sharedPreferencesFirstCommit(prefs);
        JsonUtils.writePersistentJSONObject(context, levelNameResourceArray);
    }

    /**
     * This method represents make the first commit to shared preferences.
     *
     * @param prefs
     *          A {@link SharedPreferences} object.
     *
     */
    private static void sharedPreferencesFirstCommit(SharedPreferences prefs) {
        //Instantiate an SharedPreferences.Editor to edit user preferences
        SharedPreferences.Editor editor = prefs.edit();
        //App is used once
        editor.putBoolean(FIRST_TIME_USAGE_KEY, true);
        //Show initial slide when start
        editor.putBoolean(PLAY_INITIAL_SLIDE_KEY, true);
        //Play sound effects
        editor.putBoolean(PLAY_SOUND_EFFECTS_KEY, true);
        //Store progress erased boolean for later use
        editor.putBoolean(PROGRESS_ERASED_KEY, true);

        //Apply changes
        editor.apply();
    }

    /**
     * This method create all the level and question.
     *
     * This method obtain all levels and questions configuration from two distinct resource files, one is res/values/arrays.xml and here is where the level names, minimum scores, etc are stored,
     * the other is assets/questions.json and here is where all the questions, answers and a lot more are stored.
     * You can edit this two files to change level, add more level, remove levels etc...
     *
     * @param context - Application context.
     * @return ArrayList - A list of levels with all the levels and question obtained trough resources files res/values/arrays.xml and assets/questions.json.
     *
     * @see ArrayList
     * @see Level
     * @see Question
     */
    public static ArrayList<Level> createLevels(Context context) {

        //Create an array list to store the levels
        ArrayList<Level> levelsArrayList = new ArrayList<>();

        //Initialize our level string resources
        String[] levelNameArray = context.getResources().getStringArray(R.array.level_name);
        String[] levelEarningsArray = context.getResources().getStringArray(R.array.level_earnings);
        String[] levelRequiredToUnlockArray = context.getResources().getStringArray(R.array.required_to_unlock_level);
        int[] levelScoreNeededToUnlock = context.getResources().getIntArray(R.array.score_required_to_unlock);

        //Failed question icons
        TypedArray failedQuestionIconsArray = context.getResources().obtainTypedArray(R.array.failed_question_icon);


        //Store the number of levels into numLevels variable
        int numLevels = levelNameArray.length;
        //Create levels
        for (int i = 0; i < numLevels; i++) {
            levelsArrayList.add(new Level(levelNameArray[i], levelEarningsArray[i], levelRequiredToUnlockArray[i], levelScoreNeededToUnlock[i], extractQuestions(context, levelNameArray[i].toLowerCase()), failedQuestionIconsArray.getResourceId(i, R.drawable.ic_block)));
        }

        //Always recycle your TypedArrays
        failedQuestionIconsArray.recycle();

        //Return an ArrayList with levels
        return levelsArrayList;
    }

    /**
     * This is a helper method, helps to extract all the questions stored
     * in your phone data/data/com/example/android/androidquizzapp/files/questions.json
     *
     * @param context - Application context.
     * @param level   - The level we want for extract the questions.
     * @return ArrayList<Question> - A list with all questions belonging to this level.
     */
    private static ArrayList<Question> extractQuestions(Context context, String level) {

        // Create an empty ArrayList to store the questions.
        ArrayList<Question> questionList = new ArrayList<>();

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
     * This method clean all levels progress when called, uses a context and a collection of levels to clean.
     *
     * @param context
     *          Application context.
     *
     * @param levels
     *          Collection with levels to clean.
     */
    public static void cleanProgress(Context context, Collection<Level> levels) {

        //Get preferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        //get shared preferences editor
        SharedPreferences.Editor prefsEditor = preferences.edit();

        try {
            //JSON main object containing all level questions.
            JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);

            //Iterate over all levels.
            for (Level level : levels) {

                //Construct the notification key
                String NOTIFICATION_KEY = level.getName().toLowerCase() + "_notification_already_showed";

                //Change notification state preferences for each level
                prefsEditor.putBoolean(NOTIFICATION_KEY, false);

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

            //Commit the changes in preferences editor
            prefsEditor.apply();

            //Write persistent data.
            JsonUtils.writePersistentJSONObject(context, jsonQuestionsObject);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    //Type of file storage mode.
    enum StorageMode {
        INTERNAL,
        EXTERNAL
    }

    /**
     * The {@link SaveOnQuestionAttemptedListener} class is used to save passed question persistent state, this is possible trough the implementation of {@link OnQuestionAttemptedListener}.
     * The persistent question data is saved when this class receives a callback from {@link OnQuestionAttemptedListener} trough the override method onQuestionPassed().
     */
    public static class SaveOnQuestionAttemptedListener implements OnQuestionAttemptedListener {

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

        SaveOnQuestionAttemptedListener() {
        }

        SaveOnQuestionAttemptedListener(Parcel in) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        @Override
        public int describeContents() {
            return 0;
        }

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
                //Progress starts now
                context.getSharedPreferences(QueryUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putBoolean(Level.PROGRESS_ERASED_KEY, false).apply();

                //Get a json question object from external storage
                JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                //Update question passed state.
                jsonQuestionsArray.getJSONObject(questionIndex).put("passed", true);

                //Write persistent data.
                JsonUtils.writePersistentJSONObject(context, jsonQuestionsObject);

            } catch (IOException | JSONException e) {
                e.printStackTrace();}
        }

        /**
         * This method is called when a question is passed.
         * This method is just for textual responses.
         *
         * @param context       - Application context.
         * @param level         - This question level.
         * @param questionIndex - This question index in json questions array.
         * @param answer        - User textual answer.
         */
        @Override
        public void onQuestionPassed(Context context, @NonNull Level level, int questionIndex, String answer) {
            try {
                //Progress starts now
                context.getSharedPreferences(QueryUtils.PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putBoolean(Level.PROGRESS_ERASED_KEY, false).apply();

                //Get a json question object from external storage
                JSONObject jsonQuestionsObject = JsonUtils.jsonFileToJSONObject(context, StorageMode.EXTERNAL);
                JSONArray jsonQuestionsArray = jsonQuestionsObject.getJSONArray(level.getName().toLowerCase());

                jsonQuestionsArray.getJSONObject(questionIndex).put("rightAnswer", answer);

                //Update question passed state.
                jsonQuestionsArray.getJSONObject(questionIndex).put("passed", true);

                //Write persistent data.
                JsonUtils.writePersistentJSONObject(context, jsonQuestionsObject);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * This method is called when a question is failed.
         *
         * @param context       - Application context.
         * @param level         - This question level.
         * @param questionIndex - This question index in json questions array.
         */
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
                JsonUtils.writePersistentJSONObject(context, jsonQuestionsObject);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
