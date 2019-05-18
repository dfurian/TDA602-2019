package lbs.lab.maclocation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

// ***************************************************
// *** Read this file, but do not edit its contents **
// ***************************************************

/**
 * This defines a dialog where the user is presented with a textbox to enter a URL, and options to cancel or submit.
 * The callbacks in the YesNoListener interface allow the user to define what actions should be done after this dialog is interacted with.
 */
public class ExfiltrateFragment extends DialogFragment {

    public interface YesNoListener {
        void onYes(String url);

        void onNo();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.url_input, null);
        final EditText input = view.findViewById(R.id.input_url);
        input.setSelection(input.getText().length());

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.url_title)
                .setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = input.getText().toString();
                        ((YesNoListener) getActivity()).onYes(url);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((YesNoListener) getActivity()).onNo();
                    }
                })
                .create();
    }
}
