package com.example.android.androidquizapp.question;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;

import java.util.Arrays;

/**
 * @author Fábio Gouveia
 * @version 1.0
 */

public final class Question implements Parcelable {

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
    //Class members
    final static String QUESTION_KEY = "question";
    //Instance members
    private String question, questionTip, questionTipWebAddress, textualUserAnswer;
    private String[] possibleAnswers;
    private int wrongAnswers;
    private boolean[] rightAnswersState;
    private boolean passed;
    private OnQuestionAttemptedListener onQuestionAttemptedListener;


    /**
     * @param question              - A string containing a textual question.
     * @param questionTip           - A string containing a help to the question.
     * @param questionTipWebAddress - A string containing a link to help answer the question.
     * @param possibleAnswers       - A string array containing the possible
     * @param rightAnswersState
     * @param passed                - A boolean representing the question state, true if passed, false if not.
     * @param wrongAnswers          - Number of wrong answers
     * @param textualUserAnswer
     */
    public Question(String question, String questionTip, String questionTipWebAddress, String[] possibleAnswers, boolean[] rightAnswersState, boolean passed, int wrongAnswers, String textualUserAnswer) {
        this.question = question;
        this.questionTip = questionTip;
        this.questionTipWebAddress = questionTipWebAddress;
        this.textualUserAnswer = textualUserAnswer;
        this.possibleAnswers = possibleAnswers;
        this.wrongAnswers = wrongAnswers;
        this.rightAnswersState = rightAnswersState;
        this.passed = passed;
    }

    //Question constructor with Parcel
    protected Question(Parcel in) {
        question = in.readString();
        questionTip = in.readString();
        questionTipWebAddress = in.readString();
        textualUserAnswer = in.readString();
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

    String getQuestionTip() {
        return questionTip;
    }

    String getQuestionTipEmail() {
        return questionTipWebAddress;
    }

    /**
     *
     * @return String array representation of this question possible answers
     */
    String[] getPossibleAnswers() {
        return possibleAnswers;
    }

    int getPossibleAnswersLength() {
        return possibleAnswers.length;
    }

    boolean[] getRightAnswersState() {
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

    String getTextualUserAnswer() {
        return textualUserAnswer;
    }

    boolean checkAnswer(Context context, Level level, int questionIndex, boolean[] answerState){

        if(passed = Arrays.equals(answerState, rightAnswersState)){
            onQuestionAttemptedListener.onQuestionPassed(context, level, questionIndex);
        } else {
            onQuestionAttemptedListener.onQuestionFailed(context, level, questionIndex);
        }

        return passed;
    }

    /**
     * This method checks if a textual answer is correct
     *
     * @param context       - Application context.
     * @param level         - The level which this question belongs.
     * @param questionIndex - The position of the question on level questions array.
     * @param textualAnswer - A String with the textual answer given to answer this question.
     * @return boolean - True if the question has been passed.
     */
    boolean checkAnswer(Context context, Level level, int questionIndex, String textualAnswer){
        //Count possible answers.
        String[] possibleCorrectAnswers = possibleAnswers[0].split(",");

        //Read all the possible answers.
        for (String possibleCorrectAnswer : possibleCorrectAnswers) {
            passed = textualAnswer.trim().equalsIgnoreCase(possibleCorrectAnswer.trim());
            //If answer is correct...
            if (passed) {
                //Set the user right answer
                this.textualUserAnswer = textualAnswer;
                //Call on question passed
                onQuestionAttemptedListener.onQuestionPassed(context, level, questionIndex, textualAnswer);
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
        parcel.writeString(questionTipWebAddress);
        parcel.writeString(textualUserAnswer);
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

    //Sound types
    enum Sound {
        LEVEL_PASSED(R.raw.sound_level_passed),
        CORRECT_ANSWER(R.raw.sound_question_passed),
        WRONG_ANSWER(R.raw.sound_question_failed);

        private int resourceID;

        //Enum constructor
        Sound(int resourceID) {
            this.resourceID = resourceID;
        }

        public int getResourceID() {
            return resourceID;
        }
    }

    enum Type {ONE_CHOICE, MULTIPLE_CHOICE, TEXTUAL}

}
