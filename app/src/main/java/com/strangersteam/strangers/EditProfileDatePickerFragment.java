package com.strangersteam.strangers;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class EditProfileDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static interface OnCompleteListener{
        void onComplete(int year, int month, int day);
    }

    private OnCompleteListener mOnCompleteListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            this.mOnCompleteListener = (EditProfileDatePickerFragment.OnCompleteListener)context;
        }
        catch (final ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnCompleteListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.mOnCompleteListener.onComplete(year, month, day);
    }
}