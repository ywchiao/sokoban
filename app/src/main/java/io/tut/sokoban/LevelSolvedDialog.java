package io.tut.sokoban;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class LevelSolvedDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = getString(R.string.str_level_finished, getArguments().getInt("steps"));

        builder.setMessage(message)
            .setPositiveButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
