package br.com.alimentar.alergia.validator;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import br.com.alimentar.alergia.view.CustomEditText;

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

    public static void openTeclado(CustomEditText editText, Context context) {
        editText.clearFocus();
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

}
