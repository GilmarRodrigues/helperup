package br.com.alimentar.alergia.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.ItemAdapter;
import br.com.alimentar.alergia.fragment.CustomBottomSheetDialogFragment;
import br.com.alimentar.alergia.model.Item;
import br.com.alimentar.alergia.view.CustomAutoCompleteTextView;
import br.com.alimentar.alergia.view.CustomEditText;

import static br.com.alimentar.alergia.R.id.fab;


public class RegisterProdutoActivity extends AppCompatActivity {
    private CustomEditText campo_nome;
    private CustomAutoCompleteTextView campo_fabricante;
    private Spinner seletor_categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_produto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campo_nome = (CustomEditText) findViewById(R.id.edit_nome);
        campo_fabricante = (CustomAutoCompleteTextView) findViewById(R.id.edit_fabricante);
        seletor_categoria = (Spinner) findViewById(R.id.spinner_categoria);
        setCategorias();
//        ((TextView)seletor_categoria.getChildAt(0)).setError("Menssagem");

        findViewById(fab).setOnClickListener(onClickFab());
        findViewById(R.id.proximo_btn).setOnClickListener(onClickProximo());

    }

    private View.OnClickListener onClickProximo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setError("Error");
                //ckeckProduto();
                //((TextView)seletor_categoria.getChildAt(0)).setError("Menssagem");
            }
        };
    }

    private View.OnClickListener onClickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                CustomBottomSheetDialogFragment fragment = new CustomBottomSheetDialogFragment();
                fragment.show( getSupportFragmentManager(), CustomBottomSheetDialogFragment.FRAGMENT_KEY );
            }
        };
    }


    private void ckeckProduto() {
        String nome = campo_nome.getText().toString().trim();
        String fabricante = campo_fabricante.getText().toString().trim();

        if (!validator()) {

        }
    }

    private boolean validator() {
        campo_nome.setError(null);
        campo_fabricante.setError(null);
        return false;
    }

    private void setCategorias() {
        String categorias[] = new String[]{"Categoria A", "Categoria B"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seletor_categoria.setAdapter(adapter);
    }

    public void setError(String errorMessage) {
        View view = seletor_categoria.getSelectedView();

        // Set TextView in Secondary Unit spinner to be in error so that red (!) icon
        // appears, and then shake control if in error
        TextView tvListItem = (TextView) view;

        // Set fake TextView to be in error so that the error message appears
        TextView tvInvisibleError = (TextView) findViewById(R.id.tvInvisibleError);

        // Shake and set error if in error state, otherwise clear error
        if (errorMessage != null) {
            tvListItem.setError(errorMessage);
            tvListItem.requestFocus();

            // Shake the spinner to highlight that current selection
            // is invalid -- SEE COMMENT BELOW
            //Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            //seletor_categoria.startAnimation(shake);

            tvInvisibleError.requestFocus();
            tvInvisibleError.setError(errorMessage);
        } else {
            tvListItem.setError(null);
            tvInvisibleError.setError(null);
        }
    }
}
