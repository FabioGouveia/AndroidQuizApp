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
 * TODO: Complete this header
 * Created by Android on 19-12-2017.
 */

public class QuestionsActivity extends AppCompatActivity {

    public static int NUMBER_OF_WRONG_ANSWERS;

    private Level level;
    private ViewPager questionsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //Get bundle from Intent call.
        Bundle bundle = getIntent().getExtras();

        //Get the supported action bar.
        ActionBar actionBar = getSupportActionBar();


        if(bundle != null && actionBar != null){

            //Set arrow home button enabled.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            //Set the class loader to load a Parcelable object.
            bundle.setClassLoader(Level.class.getClassLoader());
            //Get Level instance through a Parcelable.
            level =  bundle.getParcelable(Level.LEVEL_KEY);

            //Set action bar with level name. TODO: Treat null pointer exception
            actionBar.setTitle(level.getName());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    //TODO: comment the method in a proper way...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(this, LevelSelectionActivity.class));
                animateBackActivityTransition();//Animate activities transition
                break;
            case R.id.show_level_statistics:

                Bundle statisticsBundle = new Bundle();
                statisticsBundle.putString(DialogUtils.LEVEL_PROGRESS_TITLE_KEY, level.getName());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_SCORE_KEY, level.getScore());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_QUESTIONS_PASSED_KEY, level.getNumberOfPassedQuestions());
                statisticsBundle.putInt(DialogUtils.LEVEL_PROGRESS_WRONG_ANSWER_KEY, NUMBER_OF_WRONG_ANSWERS);

                DialogUtils levelProgressDialog = new DialogUtils();
                levelProgressDialog.setType(DialogUtils.Type.LEVEL_PROGRESS_DIALOG);
                levelProgressDialog.setArguments(statisticsBundle);

                //Set a listener to listen positive clicks in the dialog.
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

                levelProgressDialog.show(getSupportFragmentManager(), null);

                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    //This method is called when the user press the back button.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Animate the activity transition
        animateBackActivityTransition();
    }

    /**
     * This method changes the question to the next question if exists.
     */
    public void viewPagerNextQuestion(){
        int currentQuestion = questionsViewPager.getCurrentItem();

        if(currentQuestion < level.getNumberOfQuestions())
            questionsViewPager.setCurrentItem(currentQuestion + 1,true);
    }

    //This method help us overriding android pending transitions between activities when leave this activity to the level selection activity.
    private void animateBackActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_questions_animation, R.anim.exit_activity_questions_animation);
    }

    //This method help us overriding android pending transitions between activities when changing to statistics activity.
    private void animateGoToStatisticsActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_animation, R.anim.exit_activity_animation);
    }
}
