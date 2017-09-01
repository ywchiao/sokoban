package io.tut.sokoban;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class LevelClosingDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getArguments().getBoolean("solved")) {
            message = getString(
                R.string.str_level_solved,
                getArguments().getInt("steps"),
                getArguments().getString("elapsed")
            );
        }
        else {
            message = getString(
                R.string.str_level_stuck,
                getArguments().getInt("steps"),
                getArguments().getString("elapsed")
            );
        }

        builder.setMessage(message)
            .setPositiveButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
