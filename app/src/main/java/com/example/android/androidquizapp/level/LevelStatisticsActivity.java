package com.example.android.androidquizapp.level;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.question.Question;
import com.example.android.androidquizapp.question.QuestionsActivity;
import com.example.android.androidquizapp.utils.DialogUtils;
import com.example.android.androidquizapp.utils.QueryUtils;

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

        //Instantiate a custom adapter to work with the statistics ListView
        statisticsAdapter = new LevelStatisticsAdapter(this, QueryUtils.createLevels(getApplicationContext()));

        //Create a ListView to display the statistics
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
                        if(level != null) {
                            for (Question question : level.getQuestions()) {
                                question.resetProgress();
                            }
                        }

                        //Erase persistent level progress.
                        QueryUtils.cleanProgress(getApplicationContext(), QueryUtils.createLevels(getApplicationContext()));

                        //No level data so we go back to level selection activity
                        level = null;

                        //Update statistics list.
                        statisticsAdapter.clear();
                        statisticsAdapter.addAll(QueryUtils.createLevels(getApplicationContext()));
                        statisticsAdapter.notifyDataSetChanged();

                        //Progress cleaned
                        Toast.makeText(getApplicationContext(), "Your progress was successfully cleaned!", Toast.LENGTH_SHORT).show();
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

    //User wants to leave
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
}
