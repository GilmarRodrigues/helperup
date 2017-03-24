package br.com.alimentar.alergia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

/**
 * Created by gilmar on 20/10/16.
 */

public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    private Context context;
    private AttributeSet attrs;
    private CustomEditText.OnClickKeyPreItem onClickKeyPreItem;


    public CustomAutoCompleteTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onClickKeyPreItem != null) onClickKeyPreItem.onEscondeTeclado();
        }
        return false;
    }

    public void setOnClickKeyPreItem(CustomEditText.OnClickKeyPreItem onClickKeyPreItem) {
        this.onClickKeyPreItem = onClickKeyPreItem;
    }
}
