package jjv.uem.com.aidu.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;


import java.util.Calendar;

import jjv.uem.com.aidu.R;

/**
 * Created by javi0 on 17/01/2017.
 *
 */

public class DatepickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{


    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Usamos la fecha actual com fecha por defecto para nuestro picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Creamos una nueva instancia y la devolvemos
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        TextView tv1= (TextView) getActivity().findViewById(R.id.tv_date);
        String dia = dayOfMonth>9 ? ""+dayOfMonth:"0"+dayOfMonth;
        month++;
        String mes = month>9 ? ""+month:"0"+month;
        tv1.setText(dia+"/"+mes+"/"+year);
        
    }
}
