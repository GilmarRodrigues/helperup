package br.com.alimentar.alergia.fragment.dialog;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Window;

import br.com.alimentar.alergia.R;

/**
 * Created by gilmar on 06/12/16.
 */

public class BaseDialog extends DialogFragment {
    protected ProgressDialog mProgressDialog;

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }
        // Atualiza o tamanho do dialog
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_heigth);
        // getDialog().getWindow().setLayout(width, height);
        //getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.white);
    }

    public void showProgressDialog(int mensagem) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
            mProgressDialog.setTitle(getString(R.string.aguarde));
            mProgressDialog.setMessage(getString(mensagem));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
