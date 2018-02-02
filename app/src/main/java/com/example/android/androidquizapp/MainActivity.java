package com.example.android.androidquizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.androidquizapp.level.LevelSelectionActivity;
import com.example.android.androidquizapp.utils.QueryUtils;

/**
 * The {@link MainActivity} class extends {@link AppCompatActivity} and represents
 * the the app starting point activity.
 *
 * TODO: Complete this header
 * TODO: Review the animation time
 *
 * @author Fábio Miguel dos Santos Gouveia
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity {


    private final static String ANIMATION_CURRENT_POSITION_KEY = "animation_current_repetition_saved_state";

    private TextView introText;
    private String[] introTextArray;
    private Button skipButton;
    private Animation introAnimation;
    private int animationCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get application shared preferences.
        final SharedPreferences prefs = getSharedPreferences(QueryUtils.PREFERENCES_FILE, MODE_PRIVATE);

        //User want's to see the animation?
        boolean showAnimation = prefs.getBoolean(QueryUtils.ANIMATION_ON_START_KEY, true);

        //If user preferences contains animation on start boolean variable and showAnimation is equal false...
        if (!showAnimation) {
            //Then we start level selection activity.
            startDifficultyLevelActivity();
        }

        //Initializing our views
        introText = findViewById(R.id.intro_text);
        skipButton = findViewById(R.id.skip_button);

        //Initializing our intro text array
        introTextArray = getResources().getStringArray(R.array.intro_text);

        //Instantiate our initial intro text animation
        introAnimation = AnimationUtils.loadAnimation(this, R.anim.intro_text_increase_animation);

        //If instance state Bundle not equal to null.
        if(savedInstanceState != null){
            //Retrieve current animation position from saved instance state.
            animationCurrentPosition = savedInstanceState.getInt(ANIMATION_CURRENT_POSITION_KEY);

            //Android starts counting animations after the first frame, so if the animation current repetition number is odd
            //we need to call the decrease animation.
            if(animationCurrentPosition % 2 != 0){
                introAnimation = AnimationUtils.loadAnimation(this, R.anim.intro_text_decrease_animation);
            }

            //Set how many time the animation need to repeat until reach the end from this position.
            introAnimation.setRepeatCount(introAnimation.getRepeatCount() - animationCurrentPosition);
        }

        //Create new thread to deal with persistent data creation.
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!prefs.contains(QueryUtils.FIRST_TIME_USAGE_KEY)) {
                    try{
                        QueryUtils.createPersistentData(getApplicationContext(), R.array.level_name, prefs);
                    }catch(Exception e){
                        e.printStackTrace();
                        //Problems creating persistent data
                    }
                }
            }
        }).start();//Start the thread...


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Add a listener to help us deal with animation stages.
        introAnimation.setAnimationListener(new CostumeAnimationListener());

        //Start the animation.
        introText.startAnimation(introAnimation);

        //Add a click listener to the intro skip button;
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //User wants to skip the animation;
                startDifficultyLevelActivity();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Save current animation position.
        outState.putInt(ANIMATION_CURRENT_POSITION_KEY, animationCurrentPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //We don´t need this objects anymore.
        introText = null;
        skipButton = null;
        introTextArray = null;
        introAnimation = null;

        /*
         * we call the garbage collector " gc() System method " to alert system that here are variables "References to memory" that we no longer use
         * and the space they occupy in RAM memory can be collected.
        */
        System.gc();
    }

    //Change the animation text value
    private void changeAnimationText(){
        switch(animationCurrentPosition){
            case 2:
            case 3:
                introText.setText(introTextArray[1]);
                break;
            case 4:
            case 5:
                introText.setText(introTextArray[2]);
                break;
            default:
                introText.setText(introTextArray[0]);//Set the intro text view with the initial text;
        }
    }

    //Start select difficulty level activity
    private void startDifficultyLevelActivity() {
        startActivity(new Intent(this, LevelSelectionActivity.class));
        overridePendingTransition(R.anim.enter_activity_animation, R.anim.exit_activity_animation);
    }

    /*
    * Private inner class implementing AnimationListener;
    * Costume listener to listen the animation changes;
    */
    private class CostumeAnimationListener implements  Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            changeAnimationText();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            introText.setVisibility(View.GONE);
            startDifficultyLevelActivity();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            /*
            * Increments the current position by one each time the animation repeats.
            * The animationCurrentPosition variable help us when we need to know the current animation position.
            */
            animationCurrentPosition++;
            changeAnimationText();
        }
    }
}
