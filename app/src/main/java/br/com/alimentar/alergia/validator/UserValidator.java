package br.com.alimentar.alergia.validator;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import br.com.alimentar.alergia.view.CustomEditText;

/**
 * Created by gilmar on 25/09/16.
 */

public class UserValidator extends Validator {
    public static boolean valitadeEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean validatePassword(String password) {
        return password.length() > 5;
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
