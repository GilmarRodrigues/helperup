package br.com.alimentar.alergia.custom;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import br.com.alimentar.alergia.R;

/**
 * Created by gilmar on 10/10/16.
 */

public class CustomEditText extends EditText {
    private Context context;
    private AttributeSet attrs;
    private OnClickKeyPreItem onClickKeyPreItem;


    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            //InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            if (onClickKeyPreItem != null) onClickKeyPreItem.onEscondeTeclado();
        }
        return false;
    }

    public void setOnClickKeyPreItem(OnClickKeyPreItem onClickKeyPreItem) {
        this.onClickKeyPreItem = onClickKeyPreItem;
    }

    public interface OnClickKeyPreItem {
        void onEscondeTeclado();
    }
}