package com.example.android.androidquizapp.level;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidquizapp.R;
import com.example.android.androidquizapp.question.QuestionsActivity;

import java.util.ArrayList;

/**
 *The {@link LevelSelectionCardAdapter} extends RecyclerView.Adapter and is used
 * to help us holding an array of CardViews, the {@link RecyclerView} is similar to {@link android.widget.ListView}
 * The difference between them is that the RecyclerView can support a large amount of data and it can recycle views, which give
 * as more performance in case of a huge amount of data.
 *
 * @see Level
 *
 * @author Fábio Gouveia
 * @version 1.0
 *
 */

class LevelSelectionCardAdapter extends RecyclerView.Adapter<LevelSelectionCardAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Level> levels;

    //Constructor
    LevelSelectionCardAdapter(Context context, ArrayList<Level> levels){
        this.context = context;
        this.levels = levels;
    }

    @Override
    public LevelSelectionCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Inflate a new View representing the CardView
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_selection_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //Get the Level from levels ArrayList
        final Level level = levels.get(position);

        //Get application resources.
        final Resources resources = context.getResources();

        //State of orientation
        boolean orientationLandscape = false;

        //Check device orientation to know how to display the cards
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(340, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.rootView.setLayoutParams(layoutParams);

            //set orientation landscape equal true
            orientationLandscape = true;
        }

        //Basically in this step we copy the values we want to display from the level into the views on the holder.
        holder.level.setText(level.getName());
        holder.levelIcon.setImageResource(getIconResourceId(level, position));

        //Verify if the card is enabled/disabled or if the position corresponding to the first level.
        if (level.isUnlocked(levels, position)){
            holder.levelEarnings.setText(level.getEarnings());
            if (orientationLandscape) {
                //Remove the header right corner questions label and show the footer one
                holder.numberOfQuestionsTop.setVisibility(View.GONE);
                holder.numberOfQuestions.setVisibility(View.VISIBLE);

                //Set up the questions on the card
                setupNumberOfQuestions(holder.numberOfQuestions, level, resources);
            } else {
                //Remove the header right corner questions label
                holder.numberOfQuestionsTop.setVisibility(View.VISIBLE);

                //Set up the questions on the card
                setupNumberOfQuestions(holder.numberOfQuestionsTop, level, resources);
            }

            //Set up the score on the card
            String score = String.format(resources.getString(R.string.score_placeholder), level.getScore());
            holder.cardFooterRequiredTextView.setText(score);
        }else{

            //Card is locked, disable the card
            holder.rootView.setCardBackgroundColor(resources.getColor(R.color.colorSecondaryDark));
            holder.cardHeader.setBackground(resources.getDrawable(R.drawable.level_card_header_bgd_disabled));
            holder.levelEarnings.setVisibility(View.GONE);
            holder.numberOfQuestionsTop.setText("");
            holder.cardFooterRequiredTextView.setText(level.getRequiredToUnlock());
            holder.cardFooter.setBackground(resources.getDrawable(R.drawable.level_card_footer_bgd_disabled));
        }

        //Listen the clicks on the card
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If level enabled allow the user to navigate into questions activity.
                if(level.isUnlocked(levels, holder.getAdapterPosition())){

                    //Show some interaction to the user
                    holder.cardHeader.setBackground(resources.getDrawable(R.drawable.level_card_header_bgd_clicked));
                    holder.cardFooter.setBackground(resources.getDrawable(R.drawable.level_card_footer_bgd_clicked));


                    //Intent to start a question activity with an extra level on it.
                    final Intent intent = new Intent(context, QuestionsActivity.class);
                    intent.putExtra(Level.LEVEL_KEY, level);
                    //Start the activity with an intent.
                    context.startActivity(intent);

                    //Cast context to Activity so we can override activity pending transition.
                    ((Activity) context).overridePendingTransition(R.anim.enter_activity_animation, R.anim.exit_activity_animation);

                }else{
                    //User have no access to the level
                    Level levelToUnlock = levels.get(holder.getAdapterPosition() - 1);
                    Toast.makeText(context,"You need to score " + String.valueOf((int) levelToUnlock.getPercentageRequiredToPass()) + "% to reach " + level.getRequiredToUnlock() + " and unlock this level!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * This method setups the number of questions on cards
     **/
    private void setupNumberOfQuestions(TextView numQuestionsTextView, Level level, Resources resources) {
        //Set up the number of questions
        numQuestionsTextView.setCompoundDrawables(null, null, null, null);
        int numQuestions = level.getNumberOfQuestions();
        String numQuestionsLabel = String.format(resources.getString(R.string.number_of_questions_placeholder), numQuestions);
        numQuestionsTextView.setText(numQuestionsLabel);
    }

    /**
     * When this method is called returns the number of levels stored in levels array.
     *
     * @return Integer - The number of levels.
     */
    @Override
    public int getItemCount() {
        return levels.size();
    }

    //Private method to get the icon on level selection card view
    private int getIconResourceId(Level level, int position){

        //Create typed arrays to obtain the drawable arrays.
        TypedArray icons = context.getResources().obtainTypedArray(R.array.level_card_icon);
        TypedArray iconsDisabled = context.getResources().obtainTypedArray(R.array.level_card_icon_disabled);

        //Extract the resource id from array at this specific position.
        int iconResId = icons.getResourceId(position, android.R.drawable.ic_menu_delete);
        int iconDisabledResId = iconsDisabled.getResourceId(position, android.R.drawable.ic_menu_delete);

        //Always recycle typed arrays.
        icons.recycle();
        iconsDisabled.recycle();

        //Choose what icon to return
        return (level.isUnlocked(levels, position)) ? iconResId : iconDisabledResId;
    }

    //Static inner class to hold de CardViews
    //@see RecyclerView.ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView rootView;
        private RelativeLayout cardHeader;
        private LinearLayout cardFooter;
        private TextView level, levelEarnings, numberOfQuestionsTop, numberOfQuestions, cardFooterRequiredTextView;
        private ImageView levelIcon;

        private ViewHolder(View itemView) {
            super(itemView);

            rootView = (CardView) itemView.getRootView();
            cardHeader = itemView.findViewById(R.id.card_header);
            level = itemView.findViewById(R.id.level_text_view);
            levelEarnings = itemView.findViewById(R.id.level_earnings_text_view);
            numberOfQuestionsTop = itemView.findViewById(R.id.number_of_questions_text_view_top);
            numberOfQuestions = itemView.findViewById(R.id.number_of_questions_text_view);
            levelIcon = itemView.findViewById(R.id.level_icon);
            cardFooter = itemView.findViewById(R.id.card_footer);
            cardFooterRequiredTextView = itemView.findViewById(R.id.footer_text_view);
        }
    }
}
