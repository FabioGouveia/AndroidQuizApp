package com.example.android.androidquizapp.level;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.androidquizapp.R;

import java.util.ArrayList;

/**
 * The {@link LevelStatisticsAdapter} works as an adapter between an array of levels and a list view, this adapter
 * helps the list view to show their views.
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class LevelStatisticsAdapter extends ArrayAdapter<Level> {

    //An array with levels
    private ArrayList<Level> levels;

    /**
     * {@link LevelStatisticsAdapter} constructor method
     *
     * @param context Application context.
     * @param levels  an <tt>ArrayList</tt> of levels.
     */
    LevelStatisticsAdapter(Context context, ArrayList<Level> levels) {
        super(context, 0, levels);
        this.levels = levels;
    }

    /**
     * This method returns a view from the adapter, it's basically used by list view.
     * {@link android.widget.ListView} calls this method when it wants a view from the adapter.
     *
     * @param position    The position of this view in the array.
     * @param convertView A view stored on the array.
     * @param parent      The root view, parent of all the views inside the array.
     * @return a <tt>view</tt> from the adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.statistic_item, parent,  false);
        }

        //Get the level at this specific position
        Level level = getItem(position);

        //If level exists
        if(level != null) {

            //get level score
            int levelScore = level.getScore();

            //Get resources
            Resources resources = getContext().getResources();

            //Prepare the statistic variables
            String progress = resources.getString(R.string.your_progress, levelScore);
            String numQuestions = resources.getString(R.string.questions_passed, level.getNumberOfPassedQuestions());

            //Show the variables
            ((TextView) convertView.findViewById(R.id.progressbarLabel)).setText(level.getName());
            ((ProgressBar) convertView.findViewById(R.id.progressbar)).setProgress(levelScore);
            RelativeLayout lockedInfoPanel = convertView.findViewById(R.id.progress_info_panel_locked);
            LinearLayout infoPanel = convertView.findViewById(R.id.progress_info_panel);

            //Adapt the layout
            infoPanel.setVisibility(View.GONE);
            lockedInfoPanel.setVisibility(View.VISIBLE);

            //if the level is unlocked
            if (level.isUnlocked(levels, position)) {
                lockedInfoPanel.setVisibility(View.GONE);//Remove locked panel
                infoPanel.setVisibility(View.VISIBLE);//Show progress info panel

                //Show information
                ((TextView) convertView.findViewById(R.id.progress_text_view)).setText(progress);
                ((TextView) convertView.findViewById(R.id.num_questions_passed_text_view)).setText(numQuestions);
            }
        }

        return convertView;
    }
}
