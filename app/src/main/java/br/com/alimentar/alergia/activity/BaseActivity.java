package br.com.alimentar.alergia.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.com.alimentar.alergia.R;

/**
 * Created by gilmar on 27/09/16.
 */

public class BaseActivity extends AppCompatActivity {

    protected Toolbar setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

}
