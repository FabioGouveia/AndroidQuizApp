package com.example.android.androidquizapp.question;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;

/**
 * Created by Android on 20-12-2017.
 */

public class QuestionFragment extends Fragment {

    //Question fragment class members
    private static final String QUESTION_INDEX_KEY = "question_index_key";

    //Question fragment instance members.
    private Level level;
    private Question question;
    private int questionIndex;
    private String[] possibleAnswers;
    private TextView questionTextView, passedQuestionTextView;
    private RadioGroup oneChoiceAnswerRadioGroup;
    private LinearLayout checkBoxesHolder;
    private EditText textualAnswerInputField;
    private Button answerButton;
    private boolean[] answersState;//For non textual answers

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
        questionTextView = rootView.findViewById(R.id.question_text_view);
        oneChoiceAnswerRadioGroup = rootView.findViewById(R.id.answers_radio_group);
        checkBoxesHolder = rootView.findViewById(R.id.checkBoxes_holder);
        textualAnswerInputField = rootView.findViewById(R.id.textual_answer_text_input_field);
        answerButton = rootView.findViewById(R.id.answers_btn);
        passedQuestionTextView = rootView.findViewById(R.id.passed_question_text_view);

        if(question.passed()){
            questionPassed();
        }

        questionTextView.setText(question.getQuestion());

        possibleAnswers = question.getPossibleAnswers();

        //Start the answer fragment layout construction.
        startAnswerPanelConstruction();

        //Add an on click listener to the button so the user can test the answer.
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(question.getType() == Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, textualAnswerInputField.getText().toString())) {
                    questionPassed();
                    changeQuestion();
                }else if(question.getType() != Question.Type.TEXTUAL && question.checkAnswer(getActivity().getApplicationContext(), level, questionIndex, answersState)){
                    questionPassed();
                    changeQuestion();
                }else{
                    //Wrong answer, show a tip to help the user.
                    showTip();
                }
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

    private void constructMultiChoiceAnswerPanelLayout(){
        final int numPossibleAnswers = question.getPossibleAnswersLength();
        answersState = new boolean[numPossibleAnswers];

        checkBoxesHolder.setVisibility(View.VISIBLE);

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

    private void constructTextualAnswerPanelLayout(){
        textualAnswerInputField.setVisibility(View.VISIBLE);
    }

    //
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

    //Prepare the layout when the question is passed.
    private void questionPassed(){
        answerButton.setVisibility(View.GONE);
        passedQuestionTextView.setVisibility(View.VISIBLE);
    }

    //Change question page when passed.
    private void changeQuestion(){
        QuestionsActivity.QUESTION_VIEW_PAGER.setCurrentItem(QuestionsActivity.QUESTION_VIEW_PAGER.getCurrentItem() + 1, true);
    }

    //Show a tip to help answering the question.
    private void showTip(){
        //TODO: Make a toast with a tip to help the user answering the question...
        Context context = getActivity().getApplicationContext();
        Toast.makeText(context, String.format(context.getResources().getString(R.string.failed_question_tip), question.getQuestionTip()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(Level.LEVEL_KEY, level);
        outState.putInt(QUESTION_INDEX_KEY, questionIndex);
        outState.putParcelable(Question.QUESTION_KEY, question);

        super.onSaveInstanceState(outState);
    }
}
