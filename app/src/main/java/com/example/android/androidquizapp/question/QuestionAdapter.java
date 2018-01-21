package com.example.android.androidquizapp.question;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;

/**
 * Created by Android on 20-12-2017.
 */

public class QuestionAdapter extends FragmentPagerAdapter {

    private Context context;
    private Level level;

    public QuestionAdapter(Context context, FragmentManager fragmentManager, Level level){
        super(fragmentManager);
        this.context = context;
        this.level = level;
    }

    //Return how many questions exists in the array
    @Override
    public int getCount() {
        return level.getQuestions().size();
    }


    //Return a fragment for this particular item position
    @Override
    public Fragment getItem(int position) {

        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.prepareFragmentState(level, position);

        return questionFragment;
    }

    //Return the tab element title
    @Override
    public CharSequence getPageTitle(int position) {

        return context.getResources().getString(R.string.question_label) + " " + String.valueOf(position + 1);
    }
}
