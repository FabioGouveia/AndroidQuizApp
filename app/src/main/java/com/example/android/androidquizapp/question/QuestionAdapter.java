package com.example.android.androidquizapp.question;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.level.Level;

/**
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class QuestionAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Level level;

    QuestionAdapter(Context context, FragmentManager fragmentManager, Level level) {
        super(fragmentManager);
        this.context = context;
        this.level = level;
    }

    /**
     * This method returns a {@code int} representing the number of items in this adapter internal array.
     *
     * @return a <tt>int</tt> representing the number of items in this adapter internal array
     */
    @Override
    public int getCount() {
        return level.getQuestions().size();
    }


    /**
     * Return a {@link QuestionFragment} for this particular item position
     *
     * @param position The fragment position in the array inside this adapter.
     * @return A <tt>QuestionFragment</tt>
     * @see Fragment
     */
    @Override
    public Fragment getItem(int position) {

        //Create a fragment
        QuestionFragment questionFragment = new QuestionFragment();
        //Prepare the fragment state
        questionFragment.prepareFragmentState(level, position);

        //Return the fragment
        return questionFragment;
    }

    @Override
    public int getItemPosition(Object object) {

        if (object instanceof QuestionFragment) {
            ((QuestionFragment) object).questionChanged();
        }

        return super.getItemPosition(object);
    }

    /**
     * This method returns a {@code char} sequence every time it get's called.
     *
     * @param position The fragment position in the array inside this adapter.
     * @return a <tt>char</tt> sequence corresponding to the page title
     */
    @Override
    public CharSequence getPageTitle(int position) {

        return context.getResources().getString(R.string.question_label) + " " + String.valueOf(position + 1);
    }

}
