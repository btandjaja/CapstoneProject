package com.buntorotandjaja.www.capstoneproject;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DecimalCurrency implements TextWatcher {

    private final DecimalFormat df;
    private final EditText et;
    private String current = "";

    public DecimalCurrency(EditText editText, String pattern) {
        df = new DecimalFormat(pattern);
        df.setDecimalSeparatorAlwaysShown(true);
        this.et = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!s.toString().equals(current)){
       et.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

            current = formatted;
       et.setText(formatted);
       et.setSelection(formatted.length());

       et.addTextChangedListener(this);
        }
    }
}