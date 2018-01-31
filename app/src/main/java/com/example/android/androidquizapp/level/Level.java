package com.example.android.androidquizapp.level;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.android.androidquizapp.question.Question;

import java.util.ArrayList;

/**
 * The class {@link Level} represents a difficulty level in game.
 * This level can have an unlimited number of questions.
 *
 * <p>You can add or remove levels, questions, warnings, sonds, etc...
 * Change the app has you want, sky is the limite, go to <b><strong>assets/questions.json</strong></b> and <b><strong>res/value/array.xml</strong></b> to find out how.
 * </p>
 *
 * <p>This class implements {@link Parcelable} so we can pass it trough Activities using a {@link android.content.Intent} and a {@link android.os.Bundle} object</p>
 *
 * @author Fábio Gouveia
 * @version 1.0
 * @see Parcelable
 * @see Question
 *
 */

public class Level implements Parcelable {

    //Class members
    public final static String LEVEL_KEY = "level";
    /**
     * Essential creator to create levels from parcels
     **/
    public static final Creator<Level> CREATOR = new Creator<Level>() {
        @Override
        public Level createFromParcel(Parcel in) {
            return new Level(in);
        }

        @Override
        public Level[] newArray(int size) {
            return new Level[size];
        }
    };
    //Instance members
    private String name, earnings, requiredToUnlock;
    private int failedQuestionIcon;
    private double percentageRequiredToPass;
    private ArrayList<Question> questions;

    /**
     * The {@link Level} constructor method allow us to construct a new Level object.
     *
     * @param name - Level name -> Edit on res/values/arrays.xml -> level_name string array.
     * @param earnings - Level earnings -> Edit on res/values/arrays.xml -> level_earnings string array.
     * @param requiredToUnlock - Required to unlock level -> Edit on res/values/arrays.xml -> required_to_unlock_level string array.
     * @param percentageRequiredToPass - Minimum score to unlock the level -> Edit on res/values/arrays.xml -> score_required_to_unlock integer array.
     * @param questions - ArrayList with this level questions -> Edit on assets/questions.json
     *
     * @see Question
     */
    public Level(String name, String earnings, String requiredToUnlock, int percentageRequiredToPass, ArrayList<Question> questions, int failedQuestionIcon) {
        this.name = name;
        this.earnings = earnings;
        this.requiredToUnlock = requiredToUnlock;
        this.percentageRequiredToPass = percentageRequiredToPass;//Implicit cast "int 32 bits to double 64 bits"
        this.questions = questions;
        this.failedQuestionIcon = failedQuestionIcon;
    }

    /**
     * The {@link Level(Parcel)} constructor is exclusively used in this app with the propose to pass level objects trough activities.
     *
     * @param in - Parcel object with this object state.
     *
     * @see Parcel
     * @see Parcelable
     */
    protected Level(Parcel in) {
        name = in.readString();
        earnings = in.readString();
        requiredToUnlock = in.readString();
        percentageRequiredToPass = in.readDouble();

        //Create an ArrayList to store the question loaded through a Parcel
        questions = new ArrayList<>();
        in.readList(questions, Question.class.getClassLoader());

        failedQuestionIcon = in.readInt();
    }

    /**
     * The getName method allow us to get the level name.
     *
     * @return String - The textual representation of level name.
     *
     */
    @NonNull
    public String getName(){
        return name;
    }

    /**
     *
     * @return Integer - The number of questions for this level.
     */
    public int getNumberOfQuestions(){
        return  questions.size();
    }

    public int getNumberOfPassedQuestions(){
        int numPassedQuestions = 0;

        for (Question question: questions) {
            if(question.passed())
                numPassedQuestions++;
        }

        return numPassedQuestions;
    }

    /**
     * The getQuestions method return a list of questions pertenc
     *
     * @return ArrayList - A List of questions belonging to this level.
     */
    public ArrayList<Question> getQuestions(){
        return questions;
    }

    /**
     * The getQuestion method returns a question in this level.
     *
     * @param index - An integer number representing the question position in questions ArrayList.
     * @return Question - A Question object instance belonging to this level.
     */
    public Question getQuestion(int index){
        return questions.get(index);
    }

    public int getTotalWrongAnswers() {
        int wrongAnswers = 0;
        for (Question question : questions) {
            wrongAnswers += question.getWrongAnswers();
        }

        return wrongAnswers;
    }

    /**
     * The getEarnings method allow us to get the level earnings.
     *
     * @return String - The textual representation of level earnings.
     */
    public String getEarnings(){
        return earnings;
    }

    /**
     * The getRequiredToUnlock method allow us to get what user status is required to unlock this level.
     *
     * @return String - The textual representation of what is necessary to unlock this level.
     */
    String getRequiredToUnlock(){
        return requiredToUnlock;
    }

    //TODO: Comment this method...
    double getPercentageRequiredToPass(){
        return percentageRequiredToPass;
    }

    /**
     * Return the <tt>int</tt> representation for failed question icon resource.
     *
     * @return the <tt>int</tt> representation for failed question icon resource
     */
    public int getFailedQuestionIconResource() {
        return failedQuestionIcon;
    }

    /**
     * The getScore method allow us to get the score made in this level.
     *
     * @return Integer - The numeric representation of this level score.
     */
    public int getScore(){
        return processScore();
    }

    /**
     * Return {@code boolean} representing the locked state, {@code true} if is unlocked and [@code false}
     * if not.
     *
     * @param levels
     *          {@link ArrayList} of levels.
     *
     * @param position
     *          A {@code int} representing the level position in this array.
     *
     * @return {@code true} if the level is unlocked and {@code false} if not.
     *
     * @see ArrayList
     */
    boolean isUnlocked(ArrayList<Level> levels, int position){
        /*
        If levelBefore it's the first level, return true because first level is never locked.
        If not, return if the level before has been passed.
        */
        return position == 0 || levels.get(position-1).getScore() >= levels.get(position-1).getPercentageRequiredToPass();
    }

    /**
     * This method returns a {@code boolean} representation for this level passed state.
     * Returns {@code true} if the level has been passed and {@code false} if not.
     *
     * @return <tt>boolean</tt> representing if the level has been passed
     */
    public boolean isPassed() {
        return getScore() >= getPercentageRequiredToPass();
    }

    /**
     * Return a {@code int} representing the score for this level.
     *
     * @return a {@code int} representing the score for this level
     */
    private int processScore(){

        //Helper variable to count the score.
        int score = 0;

        //Iterate over all questions
        for (Question question : questions) {
            //If question is passed.
            if(question.passed())
                //Increment score variable
                score += percentagePerQuestion();
        }

        //Return the score
        return score;
    }

    //Private method to help us know the value per question in percentage.
    private int percentagePerQuestion(){
        return questions.size() <= 100 ? 100 / questions.size() : (int)(questions.size() * 0.100f);}

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * This method saves this state in a {@link android.os.Parcel} object.
     *
     * @param parcel
     *          A {@link Parcel} object to store the state
     *
     * @param flags
     *          Not used in this implementation see <a href="https://developer.android.com/reference/android/os/Parcelable.html#PARCELABLE_WRITE_RETURN_VALUE">Parcelable</a> docs
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(earnings);
        parcel.writeString(requiredToUnlock);
        parcel.writeDouble(percentageRequiredToPass);
        parcel.writeList(questions);
        parcel.writeInt(failedQuestionIcon);
    }
}
