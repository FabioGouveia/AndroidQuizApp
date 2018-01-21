package com.example.android.androidquizapp.level;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.question.Question;
import com.example.android.androidquizapp.question.QuestionsActivity;
import com.example.android.androidquizapp.utils.DialogUtils;
import com.example.android.androidquizapp.utils.QueryUtils;

import java.util.ArrayList;

/**
 * The {@link LevelStatisticsActivity} is an activity with the propose to show
 * level statistics to the user.
 * Also allow the user to delete levels progress.
 *
 * Created by Android on 07-01-2018.
 */

public class LevelStatisticsActivity extends AppCompatActivity {


    private Level level;

    private LevelStatisticsAdapter statisticsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_statistics);

        Bundle bundle = getIntent().getExtras();

        ActionBar actionBar = getSupportActionBar();

        //If the action bar is not null we set the home button on our action bar.
        if(actionBar != null ){
            //Set arrow home button enabled.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        //If bundle is not null, then we know the intent calling come from questions activity.
        //If the bundle object is null, then we know the intent calling come from level selection activity.
        if(bundle != null){
            //Set the class loader to load a Parcelable object.
            bundle.setClassLoader(Level.class.getClassLoader());
            //Get Level instance so we can go back to the same activity questions where we were.
            level =  bundle.getParcelable(Level.LEVEL_KEY);
        }

        statisticsAdapter = new LevelStatisticsAdapter(this, createLevels());

        ListView statisticsList  = findViewById(R.id.level_statistics_list);
        statisticsList.setAdapter(statisticsAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                leaveThisActivityProcess();
                break;
            case R.id.clean_level_progress:

                Bundle confirmBundle = new Bundle();
                confirmBundle.putString(DialogUtils.CONFIRM_DIALOG_MESSAGE_KEY, "Clean all progress?");

                DialogUtils confirmActionDialog = new DialogUtils();
                confirmActionDialog.setType(DialogUtils.Type.CONFIRM_DIALOG);
                confirmActionDialog.setArguments(confirmBundle);
                confirmActionDialog.setOnPositiveButtonClickedListener(new DialogUtils.OnPositiveButtonClickedListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        //Erase the level progress after a successful user confirmation.
                        for (Question question: level.getQuestions()) {
                            question.resetProgress();
                        }
                        //Erase persistent level progress.
                        QueryUtils.cleanProgress(getApplicationContext(), createLevels());

                        //Update statistics list.
                        statisticsAdapter.clear();
                        statisticsAdapter.addAll(createLevels());
                        statisticsAdapter.notifyDataSetChanged();
                    }
                });

                //Show the confirmation dialog.
                confirmActionDialog.show(getSupportFragmentManager(), null);

                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        leaveThisActivityProcess();
    }

    private void leaveThisActivityProcess(){
        //If we have a level we go back to the questions activity for this level.
        if(level != null){
            Intent intent = new Intent(this, QuestionsActivity.class);
            intent.putExtra(Level.LEVEL_KEY, level);

            this.startActivity(intent);

        }else{
            //The level is null, we go back to the level selection activity.
            this.startActivity(new Intent(this, LevelSelectionActivity.class));
        }

        //Then we animate activities transition.
        //Animate the activity transition
        animateBackActivityTransition();
    }

    //This method help us overriding android pending transitions between activities when leave this activity to the level selection activity.
    private void animateBackActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_questions_animation, R.anim.exit_activity_questions_animation);
    }

    /**
     * The createLevels method hep us to get a list of levels
     * with all the levels and question obtained trough two distinct resource files one is res/values/arrays.xml is where the level names, minimum scores, etc are stored the other is assets/questions.json and is where all the questions are stored.
     * you can edit this two files to change level, add more level, remove levels etc...
     *
     * @return ArrayList - A list of levels with all the levels and question obtained trough resources files res/values/arrays.xml and assets/questions.json.
     */
    private ArrayList<Level> createLevels(){

        //Create an array list to store the levels
        ArrayList<Level> levelsArrayList = new ArrayList<>();

        //Initialize our level string resources
        String[] levelNameArray             = this.getResources().getStringArray(R.array.level_name);
        String[] levelEarningsArray         = this.getResources().getStringArray(R.array.level_earnings);
        String[] levelRequiredToUnlockArray = this.getResources().getStringArray(R.array.required_to_unlock_level);
        int[]    levelScoreNeededToUnlock   = this.getResources().getIntArray(R.array.score_required_to_unlock);


        //Store the number of levels into numLevels variable
        int numLevels = levelNameArray.length;
        //Create levels
        for (int i = 0; i < numLevels; i++) {
            levelsArrayList.add(new Level(levelNameArray[i], levelEarningsArray[i], levelRequiredToUnlockArray[i], levelScoreNeededToUnlock[i], QueryUtils.extractQuestions(this, levelNameArray[i].toLowerCase())));
        }

        //Return an ArrayList with levels
        return levelsArrayList;
    }
}
