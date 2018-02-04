package com.example.android.androidquizapp.level;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences preferences;
    private boolean progressErased;
    private DialogUtils confirmActionDialog;
    private LevelStatisticsAdapter statisticsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_statistics);

        //Get application shared preferences.
        preferences = getSharedPreferences(QueryUtils.PREFERENCES_FILE, MODE_PRIVATE);

        if (savedInstanceState != null) {
            //Set the class loader to load a Parcelable object.
            savedInstanceState.setClassLoader(Level.class.getClassLoader());
            //Set the level to the last saved state
            level = savedInstanceState.getParcelable(Level.LEVEL_KEY);
            //Set the progress erased state to the last saved state
            progressErased = savedInstanceState.getBoolean(Level.PROGRESS_ERASED_KEY);
        } else {

            //Get progress was erased status for the first time only
            progressErased = preferences.getBoolean(QueryUtils.PROGRESS_ERASED_KEY, false);

            //Used for calls coming from questions activity
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                //Set the class loader to load a Parcelable object.
                bundle.setClassLoader(Level.class.getClassLoader());
                //Get Level instance so we can go back to the same activity questions where we were.
                level = bundle.getParcelable(Level.LEVEL_KEY);
            }
        }

        //Get the supported action bar
        ActionBar actionBar = getSupportActionBar();

        //If the action bar is not null we set the home button on our action bar.
        if(actionBar != null ){
            //Set arrow home button enabled.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        //Instantiate a custom adapter to work with the statistics ListView
        statisticsAdapter = new LevelStatisticsAdapter(this, QueryUtils.createLevels(getApplicationContext()));

        //Create a ListView to display the statistics
        ListView statisticsList  = findViewById(R.id.level_statistics_list);
        statisticsList.setAdapter(statisticsAdapter);

        //Create the confirmation dialog

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
                //Home button pressed
                leaveThisActivityProcess();
                break;
            case R.id.clean_level_progress:

                //Create a bundle for the confirmation dialog
                Bundle confirmBundle = new Bundle();
                confirmBundle.putString(DialogUtils.CONFIRM_DIALOG_MESSAGE_KEY, "Clean all progress?");

                //Check if the progress was erased
                if (!progressErased) {
                    //Create a confirmation dialog
                    confirmActionDialog = new DialogUtils();
                    confirmActionDialog.setType(DialogUtils.Type.CONFIRM_DIALOG);
                    confirmActionDialog.setArguments(confirmBundle);

                    //Listen the clicks on the dialog positive button
                    confirmActionDialog.setOnPositiveButtonClickedListener(new DialogUtils.OnPositiveButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            //Erase the level progress after a successful user confirmation.
                            cleanProgress();
                        }
                    });
                    //Show the confirmation dialog.
                    confirmActionDialog.show(getSupportFragmentManager(), null);
                } else {
                    //Progress already cleaned
                    Toast.makeText(getApplicationContext(), "You have no progress yet!", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Store the level on rotation
        outState.putParcelable(Level.LEVEL_KEY, level);

        //Store the progress erased instance state for later use
        outState.putBoolean(Level.PROGRESS_ERASED_KEY, progressErased);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Remove the dialog if exists
        if (confirmActionDialog != null) {
            confirmActionDialog.dismiss();
        }
    }

    /**
     * This method cleans all progress
     **/
    private void cleanProgress() {
        if (level != null) {
            for (Question question : level.getQuestions()) {
                question.resetProgress();
            }

            //Level state erased
            level = null;
        }

        //Erase persistent level progress.
        QueryUtils.cleanProgress(getApplicationContext(), QueryUtils.createLevels(getApplicationContext()));

        //Update statistics list.
        statisticsAdapter.clear();
        statisticsAdapter.addAll(QueryUtils.createLevels(getApplicationContext()));
        statisticsAdapter.notifyDataSetChanged();


        //Progress cleaned
        Toast.makeText(getApplicationContext(), "Your progress was successfully cleaned!", Toast.LENGTH_SHORT).show();

        //Set persistent progress erased state to true
        preferences.edit().putBoolean(QueryUtils.PROGRESS_ERASED_KEY, true).apply();

        //Set progress erased to true
        progressErased = true;
    }

    /**
     * This method exits this activity
     **/
    private void leaveThisActivityProcess(){
        //Progress was erased, can't go back to questions, some levels are locked now
        if(level != null){

            //Prepare the intent for go back to questions
            Intent intent = new Intent(this, QuestionsActivity.class);
            intent.putExtra(Level.LEVEL_KEY, level);

            //Go back to questions
            this.startActivity(intent);

        }else{
            //The level is null, we go back to the level selection activity
            this.startActivity(new Intent(this, LevelSelectionActivity.class));
        }

        //Animate the activity transition
        animateBackActivityTransition();
    }

    //This method help us overriding android pending transitions between activities when leave this activity to the level selection activity.
    private void animateBackActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_questions_animation, R.anim.exit_activity_questions_animation);
    }

    @Override
    protected void onDestroy() {
        //Clean the variables for memory reuse
        preferences = null;
        confirmActionDialog = null;
        statisticsAdapter = null;

        //Inform garbage collector that can collect old variables
        System.gc();

        super.onDestroy();
    }
}
