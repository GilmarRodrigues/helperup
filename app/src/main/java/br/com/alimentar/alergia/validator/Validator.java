package br.com.alimentar.alergia.validator;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by gilmar on 25/09/16.
 */

public abstract class Validator {

    public static boolean validateNotNull(View view, String message) {
        if (view instanceof EditText) {
            EditText edText = (EditText) view;
            Editable text = edText.getText();
            if (text != null) {
                String strText = text.toString().trim();
                if (!TextUtils.isEmpty(strText)) {
                    return true;
                }
            }

            edText.setError(message);

            edText.clearFocus();
            edText.setFocusableInTouchMode(true);
            edText.setFocusable(true);
            edText.requestFocus();
        }
        return false;
    }

}
