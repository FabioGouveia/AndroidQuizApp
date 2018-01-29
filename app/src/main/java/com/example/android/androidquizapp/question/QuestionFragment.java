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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    //Question fragment instance members.
    private Level level;
    private Question question;
    private int questionIndex;
    private String[] possibleAnswers;
    private TextView multipleChoiceQuestionHint, passedQuestionLabel;
    private RadioGroup oneChoiceAnswerRadioGroup;
    private LinearLayout checkBoxesHolder;
    private TextInputEditText textualAnswerInputField;
    private Button answerButton;
    private boolean[] answersState;//For non textual answers
    private PopupWindow clickableToast;

    public QuestionFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.question_fragment, container, false);

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
        textualAnswerInputField = rootView.findViewById(R.id.textual_answer_text_input_field);
        answerButton = rootView.findViewById(R.id.answers_btn);
        passedQuestionLabel = rootView.findViewById(R.id.passed_question_label_text_view);

        if(question.passed()){
            questionPassed();
        }

        questionTextView.setText(question.getQuestion());

        possibleAnswers = question.getPossibleAnswers();

        //Start the answer fragment layout construction.
        startAnswerPanelConstruction();

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        textualAnswerInputField.setVisibility(View.VISIBLE);

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

    //This method constructs a one choice radio button panel for the answers and shows it on this fragment layout.
    private void constructOneChoiceAnswerPanelLayout(){
        final int numPossibleAnswers = question.getPossibleAnswersLength();
        answersState = new boolean[numPossibleAnswers];

        oneChoiceAnswerRadioGroup.setVisibility(View.VISIBLE);

        RadioButton possibleAnswer;
        for (int i = 0; i < numPossibleAnswers; i++) {
            possibleAnswer = (RadioButton) oneChoiceAnswerRadioGroup.getChildAt(i);
            possibleAnswer.setText(possibleAnswers[i]);
            if(question.passed()){
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

    private void checkAnswer() {
        if (question.getType() == Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, textualAnswerInputField.getText().toString())) {
            questionPassed();
            if (level.isPassed()) {
                showLevelPassedNotification();
            }
            nextQuestion();
        } else if (question.getType() != Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, answersState)) {
            questionPassed();
            if (level.isPassed()) {
                showLevelPassedNotification();
            }
            nextQuestion();
        } else {
            //Wrong answer, show a tip to help the user and increment static wrong answers on QuestionActivity.
            showTip();
            QuestionsActivity.NUMBER_OF_WRONG_ANSWERS++;
        }
    }

    //Prepare the layout when the question is passed.
    private void questionPassed(){
        answerButton.setVisibility(View.GONE);
        passedQuestionLabel.setVisibility(View.VISIBLE);
    }

    //Change to the next question page when the user passes a question.
    private void nextQuestion() {
        ((QuestionsActivity) this.getActivity()).viewPagerNextQuestion();
    }

    //Show a tip to help answering the question.
    private void showTip() {

        LayoutInflater inflater = getLayoutInflater();

        //Inflate custom popup window layout
        View failedQuestionToastRoot = inflater.inflate(R.layout.failed_question_toast, null);

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

    //This method shows a notification when the level is passed for the first time.
    private void showLevelPassedNotification() {

        //Get preferences
        SharedPreferences preferences = getActivity().getSharedPreferences(QueryUtils.PREFERENCES_FILE, Context.MODE_PRIVATE);

        String NOTIFICATION_KEY = level.getName().toLowerCase() + "_notification_already_showed";

        //If level passed notification was already showed don't show it again.
        if (preferences.getBoolean(NOTIFICATION_KEY, false)) {
            return;
        }

        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(NOTIFICATION_KEY, true);
        prefsEditor.apply();


        //Prepare de bundle with dialog arguments
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
                .setSmallIcon(R.drawable.ic_failed_beginner_question)
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


    //Any moment the app dismiss from the screen, save the state of your instance.
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
