package br.com.alimentar.alergia.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.utils.AndroidUtils;
import br.com.alimentar.alergia.view.RoundedImageView;

import static android.R.attr.data;
import static android.R.attr.key;
import static br.com.alimentar.alergia.R.id.fab;
import static br.com.alimentar.alergia.R.id.iv_produto;
import static br.com.alimentar.alergia.R.id.llt_alergenicos;

public class ViewProdutoActivity extends BaseActivity {
    private DatabaseReference mDatabaseProduto;
    private DatabaseReference mDatabaseUsuario;
    private FirebaseAuth mAuth;
    private TextView campo_fabricante;
    private TextView campo_nome_user;
    private RoundedImageView imagem_perfil;
    private ImageView iv_produto;
    private Toolbar toolbar;
    private LinearLayout mLayoutLayout;
    private List<String> mAlergenicos;
    private String key = null;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_produto);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseProduto = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabaseUsuario = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseProduto.keepSynced(true);
        mDatabaseUsuario.keepSynced(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(onClickRemove());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = getIntent().getExtras().getString("key");

        campo_fabricante = (TextView) findViewById(R.id.tv_fabricante);
        campo_nome_user = (TextView) findViewById(R.id.tv_nome_user);
        imagem_perfil = (RoundedImageView) findViewById(R.id.iv_perfil);
        iv_produto = (ImageView) findViewById(R.id.iv_produto);
        mLayoutLayout = (LinearLayout) findViewById(R.id.llt_alergenicos);

        final String key_user_uid = mAuth.getCurrentUser().getUid();

        mDatabaseProduto.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(key != null) {
                    String uid_user = (String) dataSnapshot.child("uid_user").getValue();

                    campo_fabricante.setText((String) dataSnapshot.child("fabricatente").getValue());

                    carregaImagem(iv_produto, (String) dataSnapshot.child("imagem").getValue());

                    toolbar.setTitle((String) dataSnapshot.child("nome").getValue());

                    CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsing.setTitle((String) dataSnapshot.child("nome").getValue());

                    setUser(uid_user);

                    setAlergenicosUser(key_user_uid);

                    mAlergenicos = new ArrayList<String>();
                    for (int i = 0; i < Tabelas.addSubstancias(getBaseContext()).size(); i++) {
                        String alergenico = dataSnapshot.child("substancias").child(String.valueOf(i)).child("status").getValue().toString();
                        if (alergenico != null || alergenico.equals(null)) {
                            if (getString(R.string.const_contem).equals(alergenico)) {
                                mAlergenicos.add(dataSnapshot.child("substancias").child(String.valueOf(i)).child("nome").getValue().toString());
                            }
                        }
                    }

                    if (key_user_uid.equals(uid_user)) {
                        fab.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private View.OnClickListener onClickRemove() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUtils.alertDialog(ViewProdutoActivity.this, R.string.msg_atencao, R.string.msg_remove, onClickRemoveProduto());
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
    }

    private AndroidUtils.onClickPositivo onClickRemoveProduto() {
        return new AndroidUtils.onClickPositivo() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseProduto.child(key).removeValue();
                Toast.makeText(ViewProdutoActivity.this, getString(R.string.msg_remove_sucesso), Toast.LENGTH_SHORT).show();
                key = null;
                finish();
            }
        };
    }

    //set usuario que adicionou o produto
    public void setUser(String uid_user) {

            mDatabaseUsuario.child(uid_user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String imagem = (String) dataSnapshot.child("imagem").getValue();
                    campo_nome_user.setText((String) dataSnapshot.child("nome").getValue());
                    if (!imagem.equals(Tabelas.DEFAULT)) {
                        carregaImagem(imagem_perfil, imagem);
                    } else {
                        imagem_perfil.setImageDrawable(getResources().getDrawable(R.drawable.ic_img_perfil));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    // set alergia na cor vermelhado
    public void setAlergenicosUser(String key_user_uid) {
            mDatabaseUsuario.child(key_user_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    List<String> alergenicosUser = new ArrayList<>();
                    for (int i = 0; i < Tabelas.addSubstancias(getBaseContext()).size(); i++) {
                        if (getString(R.string.const_contem).equals(dataSnapshot.child("substancias").child(String.valueOf(i)).child("status").getValue().toString())) {
                            alergenicosUser.add(dataSnapshot.child("substancias").child(String.valueOf(i)).child("nome").getValue().toString());
                        }
                    }


                    // Adiciona alegenicos na tela e se o usuario tiver alergia coloca o alergenico na cor vermelhor
                    if (mAlergenicos.size() > 0) {
                        for (String alergenico : mAlergenicos) {
                            TextView tv_alergenico = new TextView(ViewProdutoActivity.this);

                            if (alergenicosUser.contains(alergenico)) {
                                tv_alergenico.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                            }

                            tv_alergenico.setPadding(0, 0, 0, 15);
                            tv_alergenico.setText(alergenico);
                            mLayoutLayout.addView(tv_alergenico);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


    }
}
