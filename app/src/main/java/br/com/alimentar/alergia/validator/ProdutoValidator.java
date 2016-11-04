package br.com.alimentar.alergia.validator;

import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by gilmar on 20/10/16.
 */

public class ProdutoValidator extends Validator{

    public static boolean setCategoriaError(Spinner seletor_categoria, String errorMessage, View viewError, int position) {

        View view = seletor_categoria.getSelectedView();
        TextView tvListItem = (TextView) view;
        TextView tvInvisibleError = (TextView) viewError; //findViewById(R.id.tvInvisibleError);

        if (errorMessage != null && position == 0) {
            tvListItem.setError(errorMessage);
            tvListItem.requestFocus();

            tvInvisibleError.requestFocus();
            tvInvisibleError.setError(errorMessage);
            return false;
        } else {
            tvListItem.setError(null);
            tvInvisibleError.setError(null);
            return true;
        }
    }
}
