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
 * The {@link LevelStatisticsAdapter}
 * Created by Android on 07-01-2018.
 */

public class LevelStatisticsAdapter extends ArrayAdapter<Level> {

    private ArrayList<Level> levels;

    public LevelStatisticsAdapter(Context context, ArrayList<Level> levels){
        super(context, 0, levels);
        this.levels = levels;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.statistic_item, parent,  false);
        }

        Level level = getItem(position);

        if(level != null) {
            int levelScore = level.getScore();

            Resources resources = getContext().getResources();

            String progress = String.format(resources.getString(R.string.progress), levelScore + "%");
            String numQuestions = String.format(resources.getString(R.string.passed_questions), String.valueOf(level.getNumberOfPassedQuestions()));

            ((TextView) convertView.findViewById(R.id.progressbarLabel)).setText(level.getName());
            ((ProgressBar) convertView.findViewById(R.id.progressbar)).setProgress(levelScore);
            RelativeLayout lockedInfoPanel = convertView.findViewById(R.id.progress_info_panel_locked);
            LinearLayout infoPanel = convertView.findViewById(R.id.progress_info_panel);
            infoPanel.setVisibility(View.GONE);
            lockedInfoPanel.setVisibility(View.VISIBLE);

            if(level.isUnlocked(levels, position)){
                lockedInfoPanel.setVisibility(View.GONE);//Remove locked panel
                infoPanel.setVisibility(View.VISIBLE);//Show progress info panel
                ((TextView) convertView.findViewById(R.id.progress_text_view)).setText(progress);
                ((TextView) convertView.findViewById(R.id.num_questions_passed_text_view)).setText(numQuestions);
            }
        }

        return convertView;
    }
}
