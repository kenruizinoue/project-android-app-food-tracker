package com.apps.yecotec.fridgetracker.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.yecotec.fridgetracker.R;
import com.apps.yecotec.fridgetracker.data.Food;
import com.apps.yecotec.fridgetracker.data.Section;
import com.apps.yecotec.fridgetracker.data.SectionContract;
import com.apps.yecotec.fridgetracker.utils.DBUtils;
import com.apps.yecotec.fridgetracker.utils.NotifyInterfaceUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.apps.yecotec.fridgetracker.SectionAdapter.FOOD_ID;

/**
 * Created by kenruizinoue on 9/28/17.
 */

public class EditFoodDialogFragment extends DialogFragment implements TextWatcher, View.OnClickListener {

    //for logging purpose
    String TAG = this.getClass().getSimpleName();

    private AlertDialog dialog;

    private EditText quantityEditText;
    private EditText nameEditText;
    private Spinner unitSpinner;
    private Spinner categorySpinner;
    private Spinner sectionSpinner;
    private ImageView calendarImageView;
    private ImageView cameraImageView;
    private ImageView capturedImageView;
    private TextView dateTextView;

    private boolean nameTextValidated;
    private boolean quantityTextValidated;
    private boolean expireDateSettled;

    private Cursor sectionCursor;
    private Context context;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    DetailActivityNotifyInterface detailActivityNotifyInterface;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    DBUtils dbUtils;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dbUtils = new DBUtils(getActivity());

        Bundle foodIdBundle = getArguments();
        final int foodId = foodIdBundle.getInt(FOOD_ID);

        final Food foodToEdit = dbUtils.getFoodById(foodId);

        //initialized flags
        nameTextValidated = false;
        quantityTextValidated = false;
        expireDateSettled = false;

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.add_food_dialog, null);

        //find views from resources
        quantityEditText = (EditText) dialogLayout.findViewById(R.id.edit_text_food_quantity_dialog);
        nameEditText = (EditText) dialogLayout.findViewById(R.id.edit_text_food_name_dialog);
        unitSpinner = (Spinner) dialogLayout.findViewById(R.id.spinner_unit);
        categorySpinner = (Spinner) dialogLayout.findViewById(R.id.spinner_category);
        sectionSpinner = (Spinner) dialogLayout.findViewById(R.id.spinner_section);
        calendarImageView = (ImageView) dialogLayout.findViewById(R.id.calendar_image_view);
        cameraImageView = (ImageView) dialogLayout.findViewById(R.id.camera_image_view);
        capturedImageView = (ImageView) dialogLayout.findViewById(R.id.captured_image);
        dateTextView = (TextView) dialogLayout.findViewById(R.id.date_text_view);

        nameEditText.setText(foodToEdit.foodName);
        quantityEditText.setText(foodToEdit.foodQuantity + "");
        if(foodToEdit.foodUnit == 0) quantityEditText.setText((int)foodToEdit.foodQuantity + "");
        unitSpinner.setSelection(foodToEdit.foodUnit);
        categorySpinner.setSelection(foodToEdit.foodCategory);
        dateTextView.setText(foodToEdit.foodExpireDate);

        //register listeners
        nameEditText.addTextChangedListener(this);
        quantityEditText.addTextChangedListener(this);
        calendarImageView.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };

        //get string array of sections from db and populate the spinner view with the fetched records
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getSectionsArray());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //the drop down view
        sectionSpinner.setAdapter(spinnerArrayAdapter);

        Section[] sections = dbUtils.getAllSectionsArray();

        //get the proper position to set at section spinner
        for (int i = 0; i < sections.length; i++) {
            if(sections[i].sectionId == foodToEdit.sectionId) {
                sectionSpinner.setSelection(i);
                i = sections.length;
            }
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.edit_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String foodName = nameEditText.getText().toString();
                        int foodUnit = unitSpinner.getSelectedItemPosition();
                        double foodQuantity = Double.parseDouble(quantityEditText.getText().toString());
                        int foodCategory = categorySpinner.getSelectedItemPosition();


                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        String foodExpireDate;

                        //checks if the user input a date of expiry
                        if(expireDateSettled) {
                            foodExpireDate = sdf.format(myCalendar.getTime());
                        } else {
                            foodExpireDate = foodToEdit.foodExpireDate;
                        }

                        int sectionId = getSectionId(sectionSpinner.getSelectedItemPosition());


                        Food foodToUpdate = new Food(foodToEdit.foodId, foodName, foodUnit, foodQuantity, foodCategory, foodToEdit.foodRegisteredTimestamp, foodExpireDate, sectionId);

                        dbUtils.updateFood(foodToUpdate);

                        //notify through interface to host activity for updating the ui
                        detailActivityNotifyInterface.onEditFinished(foodId);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditFoodDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getResources().getString(R.string.edit_food_dialog_title));

        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){

            case R.id.calendar_image_view:
                dispatchDataPickerDialog();
                break;
            case R.id.camera_image_view:
                dispatchTakePictureIntent();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        handleText();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            capturedImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
        try {
            // Instantiate the NotifyInterfaceUtils so we can send events to the host
            detailActivityNotifyInterface = (DetailActivityNotifyInterface) getActivity();
        } catch (ClassCastException e) {
            // The activity didn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement DetailActivityNotifyInterface");
        }
    }

    private String[] getSectionsArray() {
        List<String> nameList = new ArrayList<>();
        sectionCursor = dbUtils.getAllSections();
        int nameCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry.COLUMN_SECTION_NAME);

        while (sectionCursor.moveToNext()) {
            nameList.add(sectionCursor.getString(nameCol));
        }

        return nameList.toArray(new String[nameList.size()]);
    }

    private void handleText() {
        if(nameEditText.getText().toString() != null && !nameEditText.getText().toString().isEmpty()) {
            nameTextValidated = true;
        } else {
            nameTextValidated = false;
        }

        if(quantityEditText.getText().toString() != null && !quantityEditText.getText().toString().isEmpty()) {
            //validate that introduced text is a number
            if(quantityEditText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
                quantityTextValidated = true;
            } else {
                quantityTextValidated = false;
            }
        } else {
            quantityTextValidated = false;
        }

        if(nameTextValidated && quantityTextValidated) {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    private void updateDateLabel() {
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        //check that selected date by user is a date after current date
        if(currentTimeStamp.before(myCalendar.getTime())){
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateTextView.setText(sdf.format(myCalendar.getTime()));
            expireDateSettled = true;
        } else {
            Toast.makeText(getActivity(), context.getResources().getText(R.string.warning_invalid_date_input), Toast.LENGTH_SHORT).show();
            dateTextView.setText(context.getResources().getText(R.string.expiration_date));
            expireDateSettled = false;
        }
    }

    private void dispatchDataPickerDialog(){
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private int getSectionId(int position) {
        sectionCursor = dbUtils.getAllSections();

        if(sectionCursor.move(position + 1)){
            //Log.d(TAG, "Moved");
            int idCol = sectionCursor.getColumnIndex(SectionContract.SectionEntry._ID);
            return sectionCursor.getInt(idCol);
        } else {
            //Log.d(TAG, "Did not Move");
        }

        return 0;
    }


    //helper interface to pass event back to host activity
    public interface DetailActivityNotifyInterface {

        void onEditFinished(int foodId);

    }

}
