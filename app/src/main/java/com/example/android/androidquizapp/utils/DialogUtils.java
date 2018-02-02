package com.example.android.androidquizapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.androidquizapp.R;

/**
 * {@link DialogUtils} class works as a helper to dialog jobs in the app.
 *
 * @author Fábio Gouveia
 * @version 1.0
 */

public class DialogUtils extends DialogFragment {

    //Class members
    public static final String LEVEL_TITLE_KEY = "level_title_key";
    public static final String LEVEL_EARNINGS_KEY = "level_earnings_key";
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

    /**
     * This method add a click listener to this dialog
     **/
    public void setOnPositiveButtonClickedListener(OnPositiveButtonClickedListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Create the dialog
        return createDialog();
    }

    /**
     * This method creates a new dialog
     *
     * @return a <tt>Dialog</tt> for the purpose
     */
    @NonNull
    private Dialog createDialog(){

        AlertDialog.Builder builder = null;

        try{
            builder = buildDialog();
        }catch(DialogException e){e.printStackTrace();
        }

        //If builder not equal to null, create the builder and return it
        return builder != null ? builder.create() : createDialog();
    }

    /** This method builds different types of dialogs
     *
     * @return a <tt>AlertDialog.Builder</tt> build for the purpose
     *
     * @throws DialogException
     *              If dialog type is incompatible or not found
     */
    private AlertDialog.Builder buildDialog() throws DialogException{

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Verify if the type is set, if not throw an exception.
        if(type != null){
            //Verify the type TODO: Test if Dialog exceptions work...
            switch (type){
                case CONFIRM_DIALOG: return createConfirmationDialog(builder);
                case LEVEL_PROGRESS_DIALOG:
                    return buildStatisticDialog(builder);
                case LEVEL_PASSED_ALERT_DIALOG:
                    return createLevelPassedAlertDialog(builder);
                default: throw new DialogException("Incompatible type!");
            }
        }else{ throw new DialogException("Type not found!");
        }
    }

    /**
     * This methods builds a {@link android.app.AlertDialog.Builder} with confirmation dialog
     * behavior and returns it.
     *
     * @param builder
     *          A {@link android.app.AlertDialog.Builder} to build a level confirmation dialog
     *
     * @return a <tt>AlertDialog.Builder</tt> build it like a level confirmation dialog
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

    /**
     * This methods builds a {@link android.app.AlertDialog.Builder} with level passed dialog
     * behavior and returns it.
     *
     * @param builder A {@link android.app.AlertDialog.Builder} to build a level passed dialog
     * @return a <tt>AlertDialog.Builder</tt> build it like a level passed dialog
     */
    private AlertDialog.Builder createLevelPassedAlertDialog(AlertDialog.Builder builder) {

        String levelPassedMessage = String.format(getResources().getString(R.string.level_passed_dialog_msg), getArguments().getString(LEVEL_EARNINGS_KEY));

        builder.setTitle(getResources().getString(R.string.level_passed_dialog_title))
                .setMessage(levelPassedMessage)
                .setPositiveButton(R.string.level_passed_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogUtils.this.getDialog().cancel();
                    }
                });
        return builder;
    }

    /**
     * This methods builds a {@link android.app.AlertDialog.Builder} with statistic dialog
     * behavior and returns it.
     *
     * @param builder A {@link android.app.AlertDialog.Builder} to build a level passed dialog
     * @return a <tt>AlertDialog.Builder</tt> build it like a statistic dialog
     */
    private AlertDialog.Builder buildStatisticDialog(AlertDialog.Builder builder){

       //Bundle arguments.
        int progress = getArguments().getInt(LEVEL_PROGRESS_SCORE_KEY);
        int questionsPassed = getArguments().getInt(LEVEL_PROGRESS_QUESTIONS_PASSED_KEY);
        int wrongAnswers = getArguments().getInt(LEVEL_PROGRESS_WRONG_ANSWER_KEY);

        //Convert to string
        String progressString = getResources().getString(R.string.your_progress, progress);
        String questionsPassedString = getResources().getString(R.string.questions_passed, questionsPassed);
        String wrongAnswersString = getResources().getString(R.string.wrong_answers, wrongAnswers);

        //Get dialog root view to use in the inflater
        ViewGroup dialogRootView = getActivity().findViewById(R.id.progress_dialog_root_view);

        //Inflate the dialog.
        View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.level_progress_dialog, dialogRootView, false);
        ((ProgressBar) dialogLayout.findViewById(R.id.progress_dialog_progressBar)).setProgress(progress);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_progress_text_view)).setText(progressString);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_passed_questions_text_view)).setText(questionsPassedString);
        ((TextView) dialogLayout.findViewById(R.id.progress_dialog_wrong_answers_text_view)).setText(wrongAnswersString);

        builder.setTitle(String.format(getResources().getString(R.string.level_progress_dialog_title), getArguments().getString(LEVEL_TITLE_KEY)))
                .setView(dialogLayout)
                .setPositiveButton(R.string.show_statistics, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPositiveButtonClicked();
                    }
                }).setNegativeButton(R.string.cancel_dialog, null);

        return builder;
    }

    /** Enumeration to enumerate dialog types **/
    public enum Type {
        LEVEL_PROGRESS_DIALOG,
        LEVEL_PASSED_ALERT_DIALOG,
        CONFIRM_DIALOG
    }

    /** Inner interface to deal with dialog positive button clicks **/
    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked();
    }

    /** Inner class to deal with dialog exceptions **/
    public class DialogException extends Exception {
        DialogException(String e) {
            super(e);
        }
    }
}
