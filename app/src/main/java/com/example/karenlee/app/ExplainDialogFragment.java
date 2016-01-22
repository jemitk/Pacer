package com.example.karenlee.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExplainDialogFragment.OnDialogFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ExplainDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExplainDialogFragment extends DialogFragment {

    static final String TAG = "EXPLAIN_DIALOG_FRAGMENT";

    private OnDialogFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ExplainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExplainDialogFragment newInstance() {
        ExplainDialogFragment dFragment = new ExplainDialogFragment();
        return dFragment;
    }

    public ExplainDialogFragment() {
        // Required empty public constructor
    }

    public void onBye() {
        Log.i(TAG, "The dialog is exiting");
        if (mListener != null) {
            mListener.onDialogBye();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.explain_permissions_dialogue_title)
                .setMessage(R.string.explain_permissions_dialogue_message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBye();
                    }
                })
                .setPositiveButton("Add Permissions", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBye();
                    }
                })
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "Dialog was dismissed");
        onBye();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "Dialog was canceled");
        onBye();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "Attached to " + activity.toString());
        try {
            mListener = (OnDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * dialog fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDialogFragmentListener {

        void onDialogBye();
    }
}
