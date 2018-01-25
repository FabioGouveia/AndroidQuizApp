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
    //Instance members
    private String question, questionTip;
    private String[] possibleAnswers;
    private int wrongAnswers;
    private boolean[] rightAnswersState;
    private boolean passed;
    private OnQuestionAttemptedListener onQuestionAttemptedListener;

    //Question object constructor
    public Question(String question, String questionTip, String[] possibleAnswers, boolean[] rightAnswersState, boolean passed, int wrongAnswers) {
        this.question = question;
        this.questionTip = questionTip;
        this.possibleAnswers = possibleAnswers;
        this.wrongAnswers = wrongAnswers;
        this.rightAnswersState = rightAnswersState;
        this.passed = passed;
    }

    protected Question(Parcel in) {
        question = in.readString();
        questionTip = in.readString();
        possibleAnswers = in.createStringArray();
        wrongAnswers = in.readInt();
        rightAnswersState = in.createBooleanArray();
        passed = in.readByte() != 0;
        onQuestionAttemptedListener = in.readParcelable(getClass().getClassLoader());
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
            onQuestionAttemptedListener.onQuestionPassed(context, level, questionIndex);
        } else {
            onQuestionAttemptedListener.onQuestionFailed(context, level, questionIndex);
        }

        return passed;
    }

    //TODO: Comment this method...
    boolean checkAnswer(Context context, Level level, int questionIndex, String textualAnswer){
        //Count possible answers.
        String[] possibleCorrectAnswers = possibleAnswers[0].split(",");

        //Read all the possible answers.
        for (String possibleCorrectAnswer : possibleCorrectAnswers) {
            passed = textualAnswer.trim().equalsIgnoreCase(possibleCorrectAnswer.trim());
            //If answer is correct...
            if(passed){
                //Call passed...Yeahhh
                //and stop the loop
                onQuestionAttemptedListener.onQuestionPassed(context, level, questionIndex);
                break;
            }
        }

        //If is the wrong answer. :(
        if (!passed) {
            onQuestionAttemptedListener.onQuestionFailed(context, level, questionIndex);
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

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    //TODO: Comment this method
    public Question addOnQuestionPassedListener(OnQuestionAttemptedListener onQuestionAttemptedListener) {
        this.onQuestionAttemptedListener = onQuestionAttemptedListener;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(question);
        parcel.writeString(questionTip);
        parcel.writeStringArray(possibleAnswers);
        parcel.writeInt(wrongAnswers);
        parcel.writeBooleanArray(rightAnswersState);
        parcel.writeByte((byte) (passed ? 1 : 0));
        parcel.writeParcelable(onQuestionAttemptedListener, 0);
    }

    //This method resets the progress.
    public void resetProgress(){
        passed = false;
        wrongAnswers = 0;
    }

    enum Type {ONE_CHOICE, MULTIPLE_CHOICE, TEXTUAL}

}
