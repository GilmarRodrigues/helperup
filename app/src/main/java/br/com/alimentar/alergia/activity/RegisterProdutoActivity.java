package br.com.alimentar.alergia.activity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.fragment.CustomBottomSheetDialogFragment;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.validator.ProdutoValidator;
import br.com.alimentar.alergia.view.CustomAutoCompleteTextView;
import br.com.alimentar.alergia.view.CustomEditText;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static br.com.alimentar.alergia.R.id.edit_codigo_barra;
import static br.com.alimentar.alergia.R.id.fab;


public class RegisterProdutoActivity extends BaseActivity {
    private CustomEditText campo_nome;
    private CustomAutoCompleteTextView campo_fabricante;
    private CustomEditText campo_codigo_barra;
    private ImageView iv_produto;
    private Spinner seletor_categoria;
    private Uri mImagemUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private int mPositionSelectorCategoria = 0;
    private String mCategorias[] = null;
    private String mFabricas[] = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_produto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabase.keepSynced(true);

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO).child(mCurrentUser.getUid());
        mDatabaseUser.keepSynced(true);

        campo_nome = (CustomEditText) findViewById(R.id.edit_nome);
        campo_fabricante = (CustomAutoCompleteTextView) findViewById(R.id.edit_fabricante);
        campo_codigo_barra = (CustomEditText) findViewById(edit_codigo_barra);
        seletor_categoria = (Spinner) findViewById(R.id.spinner_categoria);
        iv_produto = (ImageView) findViewById(R.id.iv_produto);
        setCategorias();
        setAutoCompleteFabrica();

        findViewById(fab).setOnClickListener(onClickFab());
        findViewById(R.id.proximo_btn).setOnClickListener(onClickProximo());

    }

    private View.OnClickListener onClickProximo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ckeckProduto();
            }
        };
    }

    private View.OnClickListener onClickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CustomBottomSheetDialogFragment.show(getSupportFragmentManager(), new CustomBottomSheetDialogFragment.Callback() {
                    @Override
                    public void onBottomSheet(Uri imagem, boolean flag) {

                        if (imagem != null) {
                            carregaImagem(iv_produto, imagem.toString());
                            try {
                                File actualImage = FileUtil.from(RegisterProdutoActivity.this, imagem);
                                iv_produto.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                                actualImage = Compressor.getDefault(RegisterProdutoActivity.this).compressToFile(actualImage);
                                mImagemUri = Uri.fromFile(actualImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Snackbar.make(v, getString(R.string.msg_imagem_null), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                });
            }
        };
    }


    private void ckeckProduto() {
        final String nome = campo_nome.getText().toString().trim();
        final String fabricante = campo_fabricante.getText().toString().trim();
        final String codigo_barra = campo_codigo_barra.getText().toString().trim();

        if (!validator()) {

            showProgressDialog(R.string.msg_salvando_produto);

            StorageReference filepath = mStorage.child(Tabelas.IMAGEM_PRODUTO).child(mImagemUri.getLastPathSegment());
            filepath.putFile(mImagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newProduto = mDatabase.push();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Produto produto = new Produto(nome, fabricante, codigo_barra, mCategorias[mPositionSelectorCategoria], downloadUrl.toString(), String.valueOf(new Date()), "true", mCurrentUser.getUid());
                            newProduto.setValue(produto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterProdutoActivity.this, getString(R.string.msg_produto_salvo), Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();
                                        finish();
                                    } else {
                                        hideProgressDialog();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
                }
            });
        }
    }

    private boolean validator() {
        campo_nome.setError(null);
        campo_fabricante.setError(null);
        campo_codigo_barra.setError(null);

        if (iv_produto.getDrawable() == null) {
            Toast.makeText(this, getString(R.string.error_image_not_null), Toast.LENGTH_SHORT).show();
            return true;
        }

        boolean nome_not_null = ProdutoValidator.validateNotNull(campo_nome, getString(R.string.error_field_required));
        if (!nome_not_null) {
            return true;
        }

        boolean fabricante_not_null = ProdutoValidator.validateNotNull(campo_fabricante, getString(R.string.error_field_required));
        if (!fabricante_not_null) {
            return true;
        }

        boolean codigo_barra_not_null = ProdutoValidator.validateNotNull(campo_codigo_barra, getString(R.string.error_field_required));
        if (!codigo_barra_not_null) {
            return true;
        }

        boolean categoria_not_null = ProdutoValidator.setCategoriaError(seletor_categoria, getString(R.string.error_field_required), findViewById(R.id.tvInvisibleError), mPositionSelectorCategoria);
        if (!categoria_not_null) {
            return true;
        }

        return false;
    }

    private void setCategorias() {
        mCategorias = new String[]{getString(R.string.categoria_selecione_item), getString(R.string.categoria_bebidas),
                getString(R.string.categoria_biscoitos), getString(R.string.categoria_congelados), getString(R.string.categoria_doces_sobremesas),
                getString(R.string.categoria_enlatados), getString(R.string.categoria_graos_cereais), getString(R.string.categoria_leite),
                getString(R.string.categoria_iorgute), getString(R.string.categoria_massa), getString(R.string.categoria_paes),
                getString(R.string.categoria_bolos), getString(R.string.categoria_queijo_frios)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, mCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seletor_categoria.setAdapter(adapter);

        seletor_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPositionSelectorCategoria = i;
                if (i != 0) {
                    TextView textError = (TextView) findViewById(R.id.tvInvisibleError);
                    textError.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setAutoCompleteFabrica() {
        mFabricas = new String[]{getString(R.string.fabrica_kuat), getString(R.string.fabrica_coca_cola), getString(R.string.fabrica_fanta), getString(R.string.fabrica_pepsi),
                getString(R.string.fabrica_guarana_antartica), getString(R.string.fabrica_sukita), getString(R.string.fabrica_sprite)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mFabricas);
        campo_fabricante.setAdapter(adapter);
    }

}
