package com.apps.yecotec.fridgetracker.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.SectionContract;
import com.apps.yecotec.fridgetracker.data.SectionsDBOpenHelper;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.NotifyInterfaceUtils;

/**
 * Created by kenruizinoue on 9/9/17.
 */

public class AddSectionDialogFragment extends DialogFragment implements View.OnClickListener {

    private AlertDialog dialog;

    //for logging purpose
    String TAG = this.getClass().getSimpleName();

    private EditText nameEditText;
    private ImageView colorView1, colorView2, colorView3, colorView4, colorView5;
    private int selectedColor;

    private SQLiteDatabase mDb;
    private SectionsDBOpenHelper dbOpenHelper;

    NotifyInterfaceUtils notifyInterfaceUtils;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //initialize db related variables
        dbOpenHelper = new SectionsDBOpenHelper(getActivity());
        mDb = dbOpenHelper.getWritableDatabase();

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.add_section_dialog, null);

        //default color selection
        selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption1);

        //find views from resources
        nameEditText = (EditText) dialogLayout.findViewById(R.id.edit_text_add_section_dialog);
        colorView1 = (ImageView) dialogLayout.findViewById(R.id.color_view_1);
        colorView2 = (ImageView) dialogLayout.findViewById(R.id.color_view_2);
        colorView3 = (ImageView) dialogLayout.findViewById(R.id.color_view_3);
        colorView4 = (ImageView) dialogLayout.findViewById(R.id.color_view_4);
        colorView5 = (ImageView) dialogLayout.findViewById(R.id.color_view_5);

        //set up listeners to ImageViews
        colorView1.setOnClickListener(this);
        colorView2.setOnClickListener(this);
        colorView3.setOnClickListener(this);
        colorView4.setOnClickListener(this);
        colorView5.setOnClickListener(this);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                handleText();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.done_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new DBUtils(getActivity()).insertSectionIntoDB(nameEditText.getText().toString(), selectedColor);

                        //notify through interface to host activity for updating the ui
                        notifyInterfaceUtils.onAddSection();
                        //Log.d(TAG, "Inserted: " + nameEditText.getText().toString() + "and" + selectedColor);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddSectionDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.add_section_dialog_title));

        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.color_view_1:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption1);
                colorView1.setImageResource(R.drawable.selected_circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_2:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption2);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.selected_circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_3:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption3);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.selected_circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_4:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption4);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.selected_circle_color_4);
                colorView5.setImageResource(R.drawable.circle_color_5);
                break;

            case R.id.color_view_5:
                selectedColor = ContextCompat.getColor(getActivity(), R.color.colorOption5);
                colorView1.setImageResource(R.drawable.circle_color_1);
                colorView2.setImageResource(R.drawable.circle_color_2);
                colorView3.setImageResource(R.drawable.circle_color_3);
                colorView4.setImageResource(R.drawable.circle_color_4);
                colorView5.setImageResource(R.drawable.selected_circle_color_5);
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            notifyInterfaceUtils = (NotifyInterfaceUtils) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement NoticeDialogListener");
        }
    }

    private void handleText() {
        if(nameEditText.getText().toString() != null && !nameEditText.getText().toString().isEmpty()) {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
