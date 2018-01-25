package com.example.android.androidquizapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidquizapp.R;

/**
 * Created by Android on 06-01-2018.
 */

public class DialogUtils extends DialogFragment {

    public static final String LEVEL_PROGRESS_TITLE_KEY = "level_title_key";
    public static final String LEVEL_PROGRESS_SCORE_KEY = "level_score_key";
    public static final String LEVEL_PROGRESS_QUESTIONS_PASSED_KEY = "questions_passed_key";
    public static final String LEVEL_PROGRESS_WRONG_ANSWER_KEY = "level_attempts_key";
    public static final String CONFIRM_DIALOG_MESSAGE_KEY = "msg_key";

    private Type type;
    private OnPositiveButtonClickedListener listener;

    /**
     * The setType method set up the dialog type.
     * This method accepts two types of dialog, a Dialog.Type.LEVEL_PROGRESS_DIALOG and a Dialog.Type.CONFIRM_DIALOG.
     *
     * @param type - The type of this dialog.
     */
    public void setType(DialogUtils.Type type){
        this.type = type;
    }

    public void setOnPositiveButtonClickedListener(OnPositiveButtonClickedListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return createDialog();
    }

    private Dialog createDialog(){

        AlertDialog.Builder builder = null;

        try{
            builder = buildDialog();
        }catch(DialogException e){e.printStackTrace();}

        return builder.create();
    }

    private AlertDialog.Builder buildDialog() throws DialogException{

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Verify if the type is set, if not throw an exception.
        if(type != null){
            //Verify the type TODO: Test if Dialog exceptions work...
            switch (type){
                case CONFIRM_DIALOG: return createConfirmationDialog(builder);
                case LEVEL_PROGRESS_DIALOG: return createStatisticDialog(builder);
                default: throw new DialogException("Incompatible type!");
            }
        }else{ throw new DialogException("Type not found!"); }
    }

    /**
     * The createConfirmationDialog method creates a AlertDialog.Builder configured
     * to work with one positive button and one cancel button as a normal confirmation dialog do.
     *
     * @param builder - An AlertDialog.Builder.
     * @return AlertDialog.Builder - An AlertDialog.Builder configured to work has a confirmation dialog.
     */
    private AlertDialog.Builder createConfirmationDialog(AlertDialog.Builder builder){
        builder.setMessage(getArguments().getString(CONFIRM_DIALOG_MESSAGE_KEY))
                .setPositiveButton(R.string.clean_progress, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositiveButtonClicked();
                    }
                })
                .setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogUtils.this.getDialog().cancel();
                    }
                });

        return builder;
    }

    private AlertDialog.Builder createStatisticDialog(AlertDialog.Builder builder){

       //Bundle arguments.
        int progress = getArguments().getInt(LEVEL_PROGRESS_SCORE_KEY);
        int questionsPassed = getArguments().getInt(LEVEL_PROGRESS_QUESTIONS_PASSED_KEY);
        int wrongAnswers = getArguments().getInt(LEVEL_PROGRESS_WRONG_ANSWER_KEY);

        //Convert to string
        String progressString = String.format(getResources().getString(R.string.your_progress), progress);
        String questionsPassedString = String.format(getResources().getString(R.string.questions_passed), questionsPassed);
        String wrongAnswersString = String.format(getResources().getString(R.string.wrong_answers), wrongAnswers);

        //Inflate the dialog.
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.level_progress_dialog, null);
        ((ProgressBar) dialogLayout.findViewById(R.id.progress_dialog_progressBar)).setProgress(progress);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_progress_text_view)).setText(progressString);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_passed_questions_text_view)).setText(questionsPassedString);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_wrong_answers_text_view)).setText(wrongAnswersString);

        builder.setTitle(String.format(getResources().getString(R.string.level_progress_dialog_title), getArguments().getString(LEVEL_PROGRESS_TITLE_KEY)))
                .setView(dialogLayout)
                .setPositiveButton(R.string.show_statistics, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositiveButtonClicked();
                    }
                }).setNegativeButton(R.string.cancel_dialog, null);

        return builder;
    }

    public enum Type {
        LEVEL_PROGRESS_DIALOG,
        CONFIRM_DIALOG
    }

    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked();
    }

    //Inner class to deal with dialog exceptions.
    public class DialogException extends Exception {
        DialogException(String e) {
            super(e);
        }
    }
}
