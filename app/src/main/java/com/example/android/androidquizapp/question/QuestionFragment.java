package com.example.android.androidquizapp.question;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;
import com.example.android.androidquizapp.level.LevelSelectionActivity;
import com.example.android.androidquizapp.utils.DialogUtils;
import com.example.android.androidquizapp.utils.QueryUtils;
import com.example.android.androidquizapp.utils.SoundUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * The {@link QuestionFragment} behaves like a single question page in the
 * questions activity.
 *
 * @see Fragment
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class QuestionFragment extends Fragment {

    //Question fragment class members
    private static final String QUESTION_INDEX_KEY = "question_index_key";

    //Utilities for sound
    private SoundUtils soundUtils;

    //Question fragment instance members.
    private View rootView;
    private Level level;
    private Question question;
    private int questionIndex;
    private String[] possibleAnswers;
    private TextView multipleChoiceQuestionHint, passedQuestionLabel;
    private RadioGroup oneChoiceAnswerRadioGroup;
    private LinearLayout checkBoxesHolder;
    private TextInputLayout answerTextInputLayout;
    private TextInputEditText textualAnswerInputField;
    private Button answerButton;
    private boolean[] answersState;//For non textual answers
    private PopupWindow clickableToast;

    public QuestionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.question_fragment, container, false);

        //Get sound utilities
        soundUtils = new SoundUtils(getActivity());

        if(savedInstanceState != null){
            savedInstanceState.setClassLoader(Level.class.getClassLoader());
            level = savedInstanceState.getParcelable(Level.LEVEL_KEY);
            questionIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY);
            savedInstanceState.setClassLoader(Question.class.getClassLoader());
            question = savedInstanceState.getParcelable(Question.QUESTION_KEY);
        }

        //Initialize our views
        TextView questionTextView = rootView.findViewById(R.id.question_text_view);
        multipleChoiceQuestionHint = rootView.findViewById(R.id.multiple_choice_question_hint_textView);
        oneChoiceAnswerRadioGroup = rootView.findViewById(R.id.answers_radio_group);
        checkBoxesHolder = rootView.findViewById(R.id.checkBoxes_holder);
        answerTextInputLayout = rootView.findViewById(R.id.answer_text_input_layout);
        textualAnswerInputField = rootView.findViewById(R.id.textual_answer_text_input_field);
        answerButton = rootView.findViewById(R.id.answers_btn);
        passedQuestionLabel = rootView.findViewById(R.id.passed_question_label_text_view);


        questionTextView.setText(question.getQuestion());

        possibleAnswers = question.getPossibleAnswers();

        //Start the answer fragment layout construction.
        startAnswerPanelConstruction();

        if (question.passed()) {
            showQuestionPassedLayoutMode();
        }

        //Add an on click listener to the button so the user can check the answer.
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check user answer
                checkAnswer();
            }
        });

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();

        //Release the audio
        soundUtils.releaseMedia();
    }

    /*
        This method is used exclusively to hide the hard key board from the screen
        if the user scroll the view pager without explicitly hide the key board.
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        //Get input method manager system service
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {

            //Hide key board
            inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);

    }

    /**
     * This method prepares the fragment state.
     *
     * @param level         - The level this question fragment belongs.
     * @param questionIndex - The position of the question.
     */
    public void prepareFragmentState(Level level, int questionIndex){
        this.level = level;
        this.questionIndex = questionIndex;
        this.question = level.getQuestion(questionIndex);
    }

    private void startAnswerPanelConstruction(){
        switch(question.getType()){
            case TEXTUAL:
                constructTextualAnswerPanelLayout();
                break;
            case ONE_CHOICE:
                constructOneChoiceAnswerPanelLayout();
                break;
            case MULTIPLE_CHOICE:
                constructMultiChoiceAnswerPanelLayout();
                break;
        }
    }

    //This method constructs a multi choice select box panel for the answers and shows it on this fragment layout.
    private void constructMultiChoiceAnswerPanelLayout(){
        final int numPossibleAnswers = question.getPossibleAnswersLength();
        answersState = new boolean[numPossibleAnswers];

        //Show hint to inform multiple choice answer.
        multipleChoiceQuestionHint.setVisibility(View.VISIBLE);

        //Show checkboxes to the user
        checkBoxesHolder.setVisibility(View.VISIBLE);

        //Fill checkboxes with possible answers.
        CheckBox possibleAnswer;
        for (int i = 0; i < numPossibleAnswers; i++) {
            possibleAnswer = (CheckBox) checkBoxesHolder.getChildAt(i);
            possibleAnswer.setText(possibleAnswers[i]);
            if(question.passed()){
                possibleAnswer.setEnabled(question.getRightAnswersState()[i]);
                possibleAnswer.setChecked(question.getRightAnswersState()[i]);
            }else{
                possibleAnswer.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < numPossibleAnswers; j++) {
                            answersState[j] = ((CheckBox) checkBoxesHolder.getChildAt(j)).isChecked();
                        }
                    }
                });
            }

            possibleAnswer.setVisibility(View.VISIBLE);
        }
    }

    //This method constructs a textual answer panel and shows it on this fragment layout.
    private void constructTextualAnswerPanelLayout(){
        answerTextInputLayout.setVisibility(View.VISIBLE);

        if (question.passed()) {
            //Format final answer string
            String userRightAnswer = getResources().getString(R.string.user_final_correct_textual_answer, question.getTextualUserAnswer());

            //Hide the hint
            answerTextInputLayout.setHintEnabled(false);
            textualAnswerInputField.setText(userRightAnswer);
            textualAnswerInputField.setGravity(Gravity.CENTER_HORIZONTAL);
            textualAnswerInputField.setEnabled(false);
        } else {

            textualAnswerInputField.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    //If the user presses down a key
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        //If enter key was pressed
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {

                            //Check user answer
                            checkAnswer();
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
            });
        }
    }


    /**
     * This method constructs a one choice radio button panel for the answers and shows it on this fragment layout
     **/
    private void constructOneChoiceAnswerPanelLayout(){
        final int numPossibleAnswers = question.getPossibleAnswersLength();
        answersState = new boolean[numPossibleAnswers];

        oneChoiceAnswerRadioGroup.setVisibility(View.VISIBLE);

        RadioButton possibleAnswer;
        for (int i = 0; i < numPossibleAnswers; i++) {
            possibleAnswer = (RadioButton) oneChoiceAnswerRadioGroup.getChildAt(i);
            possibleAnswer.setText(possibleAnswers[i]);
            if (question.passed()) {
                possibleAnswer.setEnabled(question.getRightAnswersState()[i]);
                possibleAnswer.setChecked(question.getRightAnswersState()[i]);
            }else {
                possibleAnswer.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < numPossibleAnswers; j++) {
                            answersState[j] = ((RadioButton) oneChoiceAnswerRadioGroup.getChildAt(j)).isChecked();
                        }
                    }
                });
            }

            possibleAnswer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method checks an answer
     **/
    private void checkAnswer() {
        if (question.getType() == Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, textualAnswerInputField.getText().toString())) {
            showQuestionPassedLayoutMode();

            //Go to the next question
            nextQuestion();

            if (level.isPassed()) {
                //Show level passed notification
                showLevelPassedNotification();
            }
        } else if (question.getType() != Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, answersState)) {
            showQuestionPassedLayoutMode();

            //Go to the next question
            nextQuestion();

            if (level.isPassed()) {
                //Show level passed notification
                showLevelPassedNotification();
            }
        } else {
            //Play wrong answer sound
            soundUtils.playSound(Question.Sound.WRONG_ANSWER);

            //Wrong answer, show a tip to help the user and increment static wrong answers on QuestionActivity
            showTip();
            QuestionsActivity.NUMBER_OF_WRONG_ANSWERS++;
        }
    }

    /**
     * This method turns the layout to passed mode
     **/
    private void showQuestionPassedLayoutMode(){
        answerButton.setVisibility(View.GONE);
        passedQuestionLabel.setVisibility(View.VISIBLE);
    }

    /** This method plays a sound, updates the layout and sends the user to the next question **/
    private void nextQuestion() {

        //Play a sound
        soundUtils.playSound(Question.Sound.CORRECT_ANSWER);

        //Notify questions adapter of data changes
        ((QuestionsActivity) getActivity()).viewPagerRefreshQuestionLayout();

        //Wait half a second before send the user to the next question
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Go to the next question
                ((QuestionsActivity) getActivity()).viewPagerNextQuestion();
            }
        }, 500);
    }

    /**
     * This method refresh the layout and plays a sound when this question is changed
     **/
    public void questionChanged() {
        startAnswerPanelConstruction();
    }

    /** This method shows a tip to help answering the question **/
    private void showTip() {

        //Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        //Get te root view group to use in inflater, inflate
        ViewGroup failedQuestionRoot = getActivity().findViewById(R.id.question_fragment_layout);

        //Inflate custom popup window layout
        View failedQuestionToastRoot = inflater.inflate(R.layout.failed_question_toast, failedQuestionRoot, false);

        //Find the failed image view on the inflated layout
        ImageView alertIcon = failedQuestionToastRoot.findViewById(R.id.toast_failed_image_view);
        alertIcon.setImageResource(level.getFailedQuestionIconResource());

        //Find the failed text view on the inflated layout
        TextView tip = failedQuestionToastRoot.findViewById(R.id.toast_failed_text_view);
        tip.setText(question.getQuestionTip());

        //Find help link text view on the inflated layout
        TextView link = failedQuestionToastRoot.findViewById(R.id.toast_failed_link_view);
        link.setText(question.getQuestionTipEmail());

        //Initialize a new PopupWindow object
        clickableToast = new PopupWindow(failedQuestionToastRoot, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        //Set up the location where the popup will appear
        clickableToast.showAtLocation(failedQuestionToastRoot, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);

        //Add a click listener to the root layout
        failedQuestionToastRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickableToast.dismiss();
            }
        });

        //Dismiss the popUp window after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickableToast.dismiss();
            }
        }, 3000);
    }

    /** This method shows a notification when the level is passed for the first time **/
    private void showLevelPassedNotification() {

        //Get preferences
        SharedPreferences preferences = getActivity().getSharedPreferences(QueryUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);

        //Create a notification key
        String NOTIFICATION_KEY = level.getName().toLowerCase() + "_notification_already_showed";

        //If level passed notification was already showed don't show it again.
        if (preferences.getBoolean(NOTIFICATION_KEY, false)) {
            return;
        }

        //Play a sound for level passed alert
        soundUtils.playSound(Question.Sound.LEVEL_PASSED);

        //Get shared preferences editor and edit preferences
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(NOTIFICATION_KEY, true);

        //Apply the changes
        prefsEditor.apply();

        //Prepare a bundle with dialog arguments
        Bundle levelPassedArguments = new Bundle();
        levelPassedArguments.putString(DialogUtils.LEVEL_EARNINGS_KEY, level.getEarnings());

        //Create the dialog to show
        DialogUtils levelPassedDialog = new DialogUtils();
        levelPassedDialog.setType(DialogUtils.Type.LEVEL_PASSED_ALERT_DIALOG);
        levelPassedDialog.setArguments(levelPassedArguments);

        //Show the dialog
        levelPassedDialog.show(getFragmentManager(), null);

        //Prepare intent to be triggered if the notification is clicked.
        Intent notificationIntent = new Intent(getContext(), LevelSelectionActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), notificationIntent, 0);

        //Build the notification.
        Notification levelPassedNotification = new Notification.Builder(getContext())
                .setSmallIcon(R.drawable.ic_level_done)
                .setContentTitle(getResources().getString(R.string.level_passed_dialog_title))
                .setStyle(new Notification.BigTextStyle().bigText(String.format(getResources().getString(R.string.level_passed_dialog_msg), level.getEarnings())))
                .setContentIntent(pIntent).build();

        //Hide the notification after its clicked
        levelPassedNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        //Get the notification manager.
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

        //Notify the notification manager that you have a notification to show.
        if (notificationManager != null) {
            notificationManager.notify(0, levelPassedNotification);
        }

    }

    /** Any moment the app dismiss from the screen, save this instance state **/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //If clickable toast exists, dismiss
        if (clickableToast != null) {
            clickableToast.dismiss();
        }

        outState.putParcelable(Level.LEVEL_KEY, level);
        outState.putInt(QUESTION_INDEX_KEY, questionIndex);
        outState.putParcelable(Question.QUESTION_KEY, question);

        super.onSaveInstanceState(outState);
    }
}
