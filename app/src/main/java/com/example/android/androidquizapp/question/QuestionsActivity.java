package com.example.android.androidquizapp.question;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;
import com.example.android.androidquizapp.level.LevelSelectionActivity;
import com.example.android.androidquizapp.level.LevelStatisticsActivity;
import com.example.android.androidquizapp.utils.DialogUtils;

/**
 * The {@link QuestionsActivity}
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class QuestionsActivity extends AppCompatActivity {

    //Help's keeping the statistics dialog up to date
    public static int NUMBER_OF_WRONG_ANSWERS;

    //Instance members
    private Level level;
    private ViewPager questionsViewPager;
    private DialogUtils levelProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        if (savedInstanceState != null) {
            //Set the class loader to load a Level object from a Parcelable object.
            savedInstanceState.setClassLoader(Level.class.getClassLoader());
            //Get Level instance through a Parcelable.
            level = savedInstanceState.getParcelable(Level.LEVEL_KEY);
        } else {
            //Used for calls coming from questions activity
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                //Set the class loader to load a Parcelable object.
                bundle.setClassLoader(Level.class.getClassLoader());
                //Get Level instance so we can go back to the same activity questions where we were.
                level = bundle.getParcelable(Level.LEVEL_KEY);
            }
        }
        //Get the supported action bar.
        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {

            //Set arrow home button enabled.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            //Set action bar with level name.
            actionBar.setTitle(level != null ? level.getName() : "Questions");
        }

        //Initialize static number of wrong questions
        NUMBER_OF_WRONG_ANSWERS = level.getTotalWrongAnswers();

        //Initialize our view pager to hold our questions
        questionsViewPager = findViewById(R.id.questions_view_pager);
        TabLayout questionTab = findViewById(R.id.questions_tab);

        //Instantiate a question adapter to deal with level questions
        QuestionAdapter questionAdapter = new QuestionAdapter(this, getSupportFragmentManager(), level);

        questionsViewPager.setAdapter(questionAdapter);
        questionTab.setupWithViewPager(questionsViewPager);

    }


    /**
     * This method is called when android system is ready to create the options menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    /** This method is called when someone clicks on a menu item **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                //Called back home
                startActivity(new Intent(this, LevelSelectionActivity.class));
                animateBackActivityTransition();//Animate activities transition
                break;
            case R.id.show_level_statistics:

                //Called to show statistics, create a bundle with level statistics
                Bundle statisticsBundle = new Bundle();
                statisticsBundle.putString(DialogUtils.LEVEL_TITLE_KEY, level.getName());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_SCORE_KEY, level.getScore());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_QUESTIONS_PASSED_KEY, level.getNumberOfPassedQuestions());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_WRONG_ANSWER_KEY, NUMBER_OF_WRONG_ANSWERS);

                //Create a dialog utils, set up the type and use the bundle
                levelProgressDialog = new DialogUtils();
                levelProgressDialog.setType(DialogUtils.Type.LEVEL_PROGRESS_DIALOG);
                levelProgressDialog.setArguments(statisticsBundle);

                //Set a listener to listen positive clicks on the dialog.
                levelProgressDialog.setOnPositiveButtonClickedListener(new DialogUtils.OnPositiveButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked() {

                        //Here positive click means the user want to go to the statistics activity.
                        Intent intent = new Intent(getApplicationContext(), LevelStatisticsActivity.class);
                        intent.putExtra(Level.LEVEL_KEY, level);
                        startActivity(intent);
                        animateGoToStatisticsActivityTransition();//Animate activities transition

                    }
                });

                //Show the dialog
                levelProgressDialog.show(getSupportFragmentManager(), null);

                break;
            default:
                //Strange click, just call the super method
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Save the level on screen rotation
        outState.putParcelable(Level.LEVEL_KEY, level);

        super.onSaveInstanceState(outState);
    }

    /** This method is called when someone presses the back button **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Animate the activity transition
        animateBackActivityTransition();
    }

    @Override
    protected void onPause() {

        //If level progress dialog exists
        if (levelProgressDialog != null) {
            levelProgressDialog.dismiss();
        }

        super.onPause();
    }

    /**
     * This method refreshes the view pager adapter
     **/
    public void viewPagerRefreshQuestionLayout() {
        questionsViewPager.getAdapter().notifyDataSetChanged();
    }

    /** This method changes the question to the next question if exists **/
    public void viewPagerNextQuestion() {

        //Get the current focused question on the view pager
        int currentQuestion = questionsViewPager.getCurrentItem();

        //The current question is lower then the total number of questions
        if(currentQuestion < level.getNumberOfQuestions())
            //Set the focus on the next question
            questionsViewPager.setCurrentItem(currentQuestion + 1, true);
    }

    /** This method helps overriding the pending transitions between activities **/
    private void animateBackActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_questions_animation, R.anim.exit_activity_questions_animation);
    }

    /**
     * This method help us overriding android pending transitions between activities when changing to statistics activity
     **/
    private void animateGoToStatisticsActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_animation, R.anim.exit_activity_animation);
    }

}
