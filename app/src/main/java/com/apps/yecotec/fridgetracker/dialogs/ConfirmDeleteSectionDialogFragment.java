package com.apps.yecotec.fridgetracker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.utils.DBUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.apps.yecotec.fridgetracker.SectionAdapter.SECTION_ID;

/**
 * Created by kenruizinoue on 10/4/17.
 */

public class ConfirmDeleteSectionDialogFragment extends DialogFragment {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();
    MainActivityNotifyInterface mainActivityNotifyInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle sectionIdBundle = getArguments();
        final int sectionId = sectionIdBundle.getInt(SECTION_ID);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_confirm_delete_section, null))
                .setPositiveButton(getResources().getString(R.string.delete_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivityNotifyInterface.onConfirmedDeletion(sectionId);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmDeleteSectionDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.confirm_title));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            mainActivityNotifyInterface = (ConfirmDeleteSectionDialogFragment.MainActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement MainActivityNotifyInterface");
        }
    }

    //helper interface to pass event back to host activity
    public interface MainActivityNotifyInterface {

        void onConfirmedDeletion(int sectionId);

    }
}