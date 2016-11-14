package br.com.alimentar.alergia.activity;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.view.RoundedImageView;

import static android.R.attr.data;
import static br.com.alimentar.alergia.R.id.fab;
import static br.com.alimentar.alergia.R.id.iv_produto;

public class ViewProdutoActivity extends BaseActivity {
    private DatabaseReference mDatabaseProduto;
    private DatabaseReference mDatabaseUsuario;
    private TextView campo_fabricante;
    private TextView campo_nome_user;
    private RoundedImageView imagem_perfil;
    private ImageView iv_produto;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_produto);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDatabaseProduto = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabaseUsuario = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseProduto.keepSynced(true);
        mDatabaseUsuario.keepSynced(true);

        findViewById(R.id.fab).setOnClickListener(onClickRemove());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String key = getIntent().getExtras().getString("key");

        campo_fabricante = (TextView) findViewById(R.id.tv_fabricante);
        campo_nome_user = (TextView) findViewById(R.id.tv_nome_user);
        imagem_perfil = (RoundedImageView) findViewById(R.id.iv_perfil);
        iv_produto = (ImageView) findViewById(R.id.iv_produto);

        mDatabaseProduto.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid_user = (String) dataSnapshot.child("uid_user").getValue();

                campo_fabricante.setText((String) dataSnapshot.child("fabricatente").getValue());

                carregaImagem(iv_produto, (String) dataSnapshot.child("imagem").getValue());

                toolbar.setTitle((String) dataSnapshot.child("nome").getValue());

                CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                collapsing.setTitle((String) dataSnapshot.child("nome").getValue());

                setUser(uid_user);
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };
    }

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
}
