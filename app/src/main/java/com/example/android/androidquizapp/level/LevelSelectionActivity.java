package com.example.android.androidquizapp.level;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.utils.QueryUtils;

/**
 * The {@link LevelSelectionActivity} class represents a level selection screen.
 * This screen old's a list of {@link android.support.v7.widget.CardView} items representing the levels.
 *
 * @author Fábio Gouveia
 * @version 1.0
 *
 * @see Level
 * @see LevelSelectionCardAdapter
 */



public class LevelSelectionActivity extends AppCompatActivity {

    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        //Get application shared preferences.
        userPreferences = getSharedPreferences(QueryUtils.PREFERENCES_FILE, MODE_PRIVATE);

        //Initialize our costume toolbar.
        Toolbar toolBar = findViewById(R.id.level_selection_toolbar);
        toolBar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

        //Set our costume toolbar to be the default supported toolbar.
        setSupportActionBar(toolBar);

        //Initialize a recycler view to deal with the different level cards.
        RecyclerView difficultyLevelList = findViewById(R.id.difficulty_level_list);

        //Initialize a layout manager required to work with a RecyclerView.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //Check device orientation to know how to display the cards and how the recycler layout manager should act
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //Set recycler view linear layout manager orientation to horizontal
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }

        //Instantiate a difficulty level adapter to help us dealing with cards inside of it
        LevelSelectionCardAdapter levelSelectionCardAdapter = new LevelSelectionCardAdapter(this, QueryUtils.createLevels(this));

        //Setting recycler view layout manager
        difficultyLevelList.setLayoutManager(linearLayoutManager);

        //Setting recycler view adapter
        difficultyLevelList.setAdapter(levelSelectionCardAdapter);
    }

    /*
    *
    * We don´t want the user to return for the initial animation screen, so because is a override void method
    * we leave it blank, so when the method is called nothing happen.
     */
    @Override
    public void onBackPressed() {
        //Leave blank, so the user can´t go back to the slide, if you want the user
        //to go back, uncomment the sentence bellow.

        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate our resource menu layout
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //Initialize menu checkable items
        MenuItem showSlideCheckableItem = menu.findItem(R.id.show_animation_checkbox);
        MenuItem playSoundCheckableItem = menu.findItem(R.id.play_sound_effects_checkbox);

        //Change show animation checkbox to checked state depending on user preferences.
        if (userPreferences.getBoolean(QueryUtils.ANIMATION_ON_START_KEY, true)) {

            //User want's to see the animation on application start
            showSlideCheckableItem.setChecked(true);

        } else {
            showSlideCheckableItem.setChecked(false);
        }

        //Change play sound effects checkbox to checked state depending on user preferences.
        if (userPreferences.getBoolean(QueryUtils.PLAY_SOUND_EFFECT_KEY, true)) {

            //User want's to see the animation on application start
            playSoundCheckableItem.setChecked(true);

        } else {
            playSoundCheckableItem.setChecked(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.main_menu_progress:
                this.startActivity(new Intent(this, LevelStatisticsActivity.class));
                animateOpenStatisticsActivityTransition();
                break;
            case R.id.show_animation_checkbox:

                //When user click's the check box is checked
                if(item.isChecked()){
                    //If the box is checked when the user click's, so the user want's to un-check the box and stop showing the initial slide show
                    userPreferences.edit().putBoolean(QueryUtils.ANIMATION_ON_START_KEY, false).apply();
                    invalidateOptionsMenu();//Refresh options menu
                }else{
                    //If the box is unchecked when the user click's, so the user want's to check the box and start's showing the initial slide show
                    userPreferences.edit().putBoolean(QueryUtils.ANIMATION_ON_START_KEY, true).apply();
                    invalidateOptionsMenu();//Refresh options menu
                }

                break;
            default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * This method help us overriding android pending transitions between activities when leave this activity to the level selection activity
     **/
    private void animateOpenStatisticsActivityTransition(){
        overridePendingTransition(R.anim.enter_activity_animation, R.anim.exit_activity_animation);
    }
}
