package com.strangersteam.strangers;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import android.widget.TimePicker;

import java.util.Calendar;




public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static interface OnCompleteListener{
        void onComplete(int hour, int minute);
    }

    private OnCompleteListener mOnCompleteListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            this.mOnCompleteListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnCompleteListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        this.mOnCompleteListener.onComplete(i, i1);
    }
}
