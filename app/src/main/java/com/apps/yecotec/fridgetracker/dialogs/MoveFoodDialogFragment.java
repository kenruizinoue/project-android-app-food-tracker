package com.apps.yecotec.fridgetracker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.data.Section;
import com.apps.yecotec.fridgetracker.data.SectionContract;
import com.apps.yecotec.fridgetracker.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

import static com.apps.yecotec.fridgetracker.SectionAdapter.SECTION_ID;

/**
 * Created by kenruizinoue on 10/4/17.
 */

public class MoveFoodDialogFragment extends DialogFragment {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();
    MainActivityNotifyInterface mainActivityNotifyInterface;

    private LinearLayout checkBoxContainer;
    private Spinner sectionSpinner;

    DBUtils dbUtils;

    //holds foods to be moved
    List<Food> foodsToMove = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dbUtils = new DBUtils(getActivity());

        Bundle sectionIdBundle = getArguments();
        final int sectionId = sectionIdBundle.getInt(SECTION_ID);

        Food[] foodsOfSection = dbUtils.getFoodsOfSection(sectionId);


        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.dialog_move_food, null);

        checkBoxContainer = (LinearLayout) dialogLayout.findViewById(R.id.food_checkboxes_container);
        sectionSpinner = (Spinner) dialogLayout.findViewById(R.id.move_food_spinner_section);


        //populate checkboxes
        for (Food food:foodsOfSection) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(food.foodName);
            final Food foodToMove = food;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if(checked) {
                        foodsToMove.add(foodToMove);
                    } else {
                        foodsToMove.remove(foodToMove);
                    }
                }
            });
            checkBoxContainer.addView(checkBox);
        }

        //get string array of sections from db and populate the spinner view with the fetched records
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getSectionsArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //the drop down view
        sectionSpinner.setAdapter(spinnerArrayAdapter);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                .setPositiveButton(getResources().getString(R.string.move_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (Food foodToUpdate: foodsToMove) {
                            foodToUpdate.sectionId = getSectionId(sectionSpinner.getSelectedItemPosition());
                            dbUtils.updateFood(foodToUpdate);
                        }

                        mainActivityNotifyInterface.onMoved();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MoveFoodDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.move_food_title));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            mainActivityNotifyInterface = (MoveFoodDialogFragment.MainActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement MainActivityNotifyInterface");
        }
    }

    //helper interface to pass event back to host activity
    public interface MainActivityNotifyInterface {

        void onMoved();

    }

    private String[] getSectionsArray() {
        List<String> nameList = new ArrayList<>();
        Cursor sectionCursor = dbUtils.getAllSections();
        int nameCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);

        while (sectionCursor.moveToNext()) {
            nameList.add(sectionCursor.getString(nameCol));
        }

        return nameList.toArray(new String[nameList.size()]);
    }

    private int getSectionId(int position) {
        Cursor sectionCursor = new DBUtils(getActivity()).getAllSections();

        if(sectionCursor.move(position + 1)){
            int idCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry._ID);
            return sectionCursor.getInt(idCol);
        } else {
        }

        return 0;
    }
}