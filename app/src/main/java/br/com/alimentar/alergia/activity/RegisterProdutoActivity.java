package br.com.alimentar.alergia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.fragment.CustomBottomSheetDialogFragment;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.validator.ProdutoValidator;
import br.com.alimentar.alergia.view.CustomAutoCompleteTextView;
import br.com.alimentar.alergia.view.CustomEditText;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static br.com.alimentar.alergia.R.id.centerCrop;
import static br.com.alimentar.alergia.R.id.edit_codigo_barra;
import static br.com.alimentar.alergia.R.id.fab;
import static br.com.alimentar.alergia.utils.AndroidUtils.alertDialog;
import static br.com.alimentar.alergia.validator.ProdutoValidator.verificarCodigoBarraFirebase;


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
    private String mProdutoKey;


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
        findViewById(R.id.btn_scanner).setOnClickListener(onClickScanner());

        String codigo_barra = getIntent().getExtras().getString("codigo_barra");
        if (codigo_barra != "") {
            campo_codigo_barra.setText(codigo_barra);
        }

    }

    private View.OnClickListener onClickScanner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(RegisterProdutoActivity.this).initiateScan();
            }
        };
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
                                //iv_produto.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                                actualImage = Compressor.getDefault(RegisterProdutoActivity.this).compressToFile(actualImage);
                                mImagemUri = Uri.fromFile(actualImage);

                                /*Bitmap bitmap = BitmapFactory.decodeFile(actualImage.getAbsolutePath());
                                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                                iv_produto.setImageBitmap(bitmap);
                                iv_produto.setScaleType(ImageView.ScaleType.FIT_XY);*/
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
            ProdutoValidator.verificarCodigoBarraFirebase(mDatabase, campo_codigo_barra, this, campo_codigo_barra.getText().toString().trim(), TAG, new ProdutoValidator.Callback() {
                @Override
                public void onVerificarCodigoBarraFirebase(boolean flag) {
                    if(flag) {
                        showProgressDialog(R.string.msg_salvando_produto);

                        StorageReference filepath = mStorage.child(Tabelas.IMAGEM_PRODUTO).child(mImagemUri.getLastPathSegment());
                        filepath.putFile(mImagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                Produto produto = new Produto(nome, fabricante, codigo_barra, mCategorias[mPositionSelectorCategoria], downloadUrl.toString(), dataAtual(), "true", mCurrentUser.getUid(), Tabelas.addSubstancias(getBaseContext()));
                                Intent mainIntent = new Intent(RegisterProdutoActivity.this, RegisterSubstanciaProdutoActivity.class);
                                mainIntent.putExtra(Produto.KEY, produto);
                                startActivity(mainIntent);
                                AndroidUtils.closeVirtualKeyboard(RegisterProdutoActivity.this, campo_nome);

                                hideProgressDialog();

                            }
                        });

                    }
                }
            });

            //showProgressDialog(R.string.msg_salvando_produto);

            /*StorageReference filepath = mStorage.child(Tabelas.IMAGEM_PRODUTO).child(mImagemUri.getLastPathSegment());
            filepath.putFile(mImagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newProduto = mDatabase.push();

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        public boolean flag = true;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mProdutoKey = dataSnapshot.getKey();
                            if (flag) {
                                for (DataSnapshot produto : dataSnapshot.getChildren()) {
                                    if (codigo_barra.equals(produto.child("codigo_barra").getValue(String.class))) {
                                        flag = false;
                                        alertDialog(RegisterProdutoActivity.this, R.string.msg_produto_ja_existe, R.string.msg_produto_ja_possui_um_cadastro, onClickVaiParaRegisterProdutoActivity(produto.getKey()));
                                        hideProgressDialog();
                                        break;
                                    }
                                }
                            }

                            if (flag) {
                                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        final Produto produto = new Produto(nome, fabricante, codigo_barra, mCategorias[mPositionSelectorCategoria], downloadUrl.toString(), dataAtual(), "true", mCurrentUser.getUid(), Tabelas.addSubstancias(getBaseContext()));
                                        newProduto.setValue(produto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    hideProgressDialog();

                                                    Intent mainIntent = new Intent(RegisterProdutoActivity.this, RegisterSubstanciaProdutoActivity.class);
                                                    mainIntent.putExtra(Produto.KEY, produto);
                                                    startActivity(mainIntent);
                                                    AndroidUtils.closeVirtualKeyboard(RegisterProdutoActivity.this, campo_nome);
                                                } else {
                                                    hideProgressDialog();
                                                }
                                            }
                                        });
                                        flag = false;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, databaseError.getMessage());
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });


                }
            });*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.i(TAG, "onActivityResult:Cancelled");
            } else {
                verificarCodigoBarraFirebase(mDatabase, campo_codigo_barra, this, result.getContents(), TAG, new ProdutoValidator.Callback() {
                    @Override
                    public void onVerificarCodigoBarraFirebase(boolean flag) {

                    }
                });
                /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot produto : dataSnapshot.getChildren()) {
                            if (result.getContents().equals(produto.child("codigo_barra").getValue(String.class))) {
                                alertDialog(RegisterProdutoActivity.this, R.string.msg_produto_ja_existe, R.string.msg_produto_ja_possui_um_cadastro, onClickVaiParaRegisterProdutoActivity(produto.getKey()));
                                break;
                            }
                        }
                        campo_codigo_barra.setText(result.getContents());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });*/
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.i(TAG, "onActivityResult:Cancelled");
            } else {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean flag = true;
                        for (DataSnapshot produto : dataSnapshot.getChildren()) {
                            if (result.getContents().equals(produto.child("codigo_barra").getValue(String.class))) {
                                flag = false;
                                alertDialog(RegisterProdutoActivity.this, R.string.msg_produto_ja_existe, R.string.msg_produto_ja_possui_um_cadastro, onClickVaiParaRegisterProdutoActivity(produto.getKey()));
                                break;
                            }
                        }
                        if (flag) {
                            campo_codigo_barra.setText(result.getContents());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    private AndroidUtils.onClickPositivo onClickVaiParaRegisterProdutoActivity(final String key) {
        return new AndroidUtils.onClickPositivo() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RegisterProdutoActivity.this, ViewProdutoActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        };
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
        mCategorias = Tabelas.addCategorias(this);

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
        mFabricas = Tabelas.addFabricantes(getBaseContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mFabricas);
        campo_fabricante.setAdapter(adapter);
    }

    private String dataAtual() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String data = fmt.format(date);

        //Locale brasil = new Locale("pt", "BR");
        //DateFormat f2 = DateFormat.getDateInstance(DateFormat.FULL, brasil);
        //String data = f2.format(date);
        return data;
    }

}
