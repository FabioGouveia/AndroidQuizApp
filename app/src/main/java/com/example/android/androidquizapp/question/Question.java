package com.example.android.androidquizapp.question;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.androidquizapp.level.Level;

import java.util.Arrays;

/**
 * Created by Android on 20-12-2017.
 */

public class Question implements Parcelable {

    //Class members
    public final static String QUESTION_KEY = "question";

    enum Type{ONE_CHOICE, MULTIPLE_CHOICE, TEXTUAL}


    //Instance members
    private String question, questionTip;
    private String[] possibleAnswers;
    private boolean[] rightAnswersState;
    private boolean passed;
    private OnQuestionPassedListener onQuestionPassedListener;

    //Question object constructor
    public Question(String question, String questionTip, String[] possibleAnswers, boolean[] rightAnswersState, boolean passed) {
        this.question = question;
        this.questionTip = questionTip;
        this.possibleAnswers = possibleAnswers;
        this.rightAnswersState = rightAnswersState;
        this.passed = passed;
    }

    protected Question(Parcel in) {
        question = in.readString();
        questionTip = in.readString();
        possibleAnswers = in.createStringArray();
        rightAnswersState = in.createBooleanArray();
        passed = in.readByte() != 0;
        onQuestionPassedListener = in.readParcelable(getClass().getClassLoader());
    }

    /**
     *
     * @return String representation of the question.
     */
    public String getQuestion() {
        return question;
    }


    public String getQuestionTip(){
        return questionTip;
    }

    /**
     *
     * @return String array representation of this question possible answers
     */
    public String[] getPossibleAnswers(){
        return  possibleAnswers;
    }

    public int getPossibleAnswersLength(){
        return possibleAnswers.length;
    }

    public boolean[] getRightAnswersState(){
        return rightAnswersState;
    }

    public  Type getType(){
        if(possibleAnswers.length == 1){
            return Type.TEXTUAL;
        }else{
            int numRightAnswers = 0;
            for(boolean rightAnswer : rightAnswersState){
                if(rightAnswer)
                    numRightAnswers++;
            }
            if(numRightAnswers > 1)
                return  Type.MULTIPLE_CHOICE;
            else
                return Type.ONE_CHOICE;
        }
    }

    boolean checkAnswer(Context context, Level level, int questionIndex, boolean[] answerState){

        if(passed = Arrays.equals(answerState, rightAnswersState)){
            onQuestionPassedListener.onQuestionPassed(context, level, questionIndex);
        }

        return passed;
    }

    //TODO: Comment this method...
    boolean checkAnswer(Context context, Level level, int questionIndex, String textualAnswer){
        String[] possibleCorrectAnswers = possibleAnswers[0].split(",");

        for (String possibleCorrectAnswer : possibleCorrectAnswers) {
            passed = textualAnswer.trim().equalsIgnoreCase(possibleCorrectAnswer.trim());
            if(passed){
                onQuestionPassedListener.onQuestionPassed(context, level, questionIndex);
                break;
            }
        }

        return passed;
    }

    /**
     * The passed method returns a true if the answer was passed or
     * false if don't.
     *
     * @return Boolean - If the question was passed.
     */
    public boolean passed(){
        return passed;
    }

    //TODO: Comment this method
    public Question addOnQuestionPassedListener(OnQuestionPassedListener onQuestionPassedListener){
        this.onQuestionPassedListener = onQuestionPassedListener;
        return this;
    }

    //Parcelable implementation
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(questionTip);
        dest.writeStringArray(possibleAnswers);
        dest.writeBooleanArray(rightAnswersState);
        dest.writeByte((byte) (passed ? 1 : 0));
        dest.writeParcelable(onQuestionPassedListener, 0);
    }

    //This method resets the progress
    public void resetProgress(){
        passed = false;
    }

}
