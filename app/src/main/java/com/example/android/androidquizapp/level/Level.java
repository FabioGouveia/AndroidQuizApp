package com.example.android.androidquizapp.level;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.android.androidquizapp.question.Question;

import java.util.ArrayList;

/**
 * The class {@link Level} represents a difficulty level in game.
 * The level have unlimited number of questions.
 *
 * You can add more levels or remove as you want, see assets/questions.json and res/value/array.xml to find out how.
 *
 * This class implements {@link Parcelable} so we can pass it trough Activities using a {@link android.content.Intent} and a {@link android.os.Bundle} object
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class Level implements Parcelable {

    //Class members
    public final static String LEVEL_KEY = "level";
    /*
     * Parcelable implementation below, see documentation android.os.Parcelable and android.os.Parcel classes.
     *
     * The implementation below help us when we need to pass objects through activities.
     */
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
    public String getRequiredToUnlock(){
        return requiredToUnlock;
    }

    //TODO: Comment this method...
    public double getPercentageRequiredToPass(){
        return percentageRequiredToPass;
    }

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
     * The isUnlocked method let us know if this level is locked or unlocked.
     *
     * @param levels - {@link ArrayList} of levels.
     * @param position - The level position in this array.
     * @return boolean - true if the level is unlocked and false if not.
     *
     * @see ArrayList
     */
    public boolean isUnlocked(ArrayList<Level> levels, int position){
        /*
        If levelBefore it's the first level return true because first level is never locked, if not
        return if level before score is higher then score required to pass.
        */
        return position == 0 || levels.get(position-1).getScore() >= levels.get(position-1).getPercentageRequiredToPass();
    }

    public boolean isPassed() {
        return getScore() >= getPercentageRequiredToPass();
    }

    //Private method to process the score
    private int processScore(){

        //Helper variable to count the score.
        int score = 0;

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(earnings);
        dest.writeString(requiredToUnlock);
        dest.writeDouble(percentageRequiredToPass);
        dest.writeList(questions);
        dest.writeInt(failedQuestionIcon);
    }
}
