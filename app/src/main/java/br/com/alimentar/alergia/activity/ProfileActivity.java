package br.com.alimentar.alergia.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.fragment.dialog.UpdateUserDialog;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;
import br.com.alimentar.alergia.view.RoundedImageView;

import static android.R.attr.key;

public class ProfileActivity extends BaseActivity {
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private User mUser;
    private TextView tv_nome;
    private TextView tv_email;
    private ProgressBar progressBar;
    private ImageView iv_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUser.keepSynced(true);

        findViewById(R.id.iv_editar_perfil).setOnClickListener(onClickUpdateUser());

        setUser();
    }

    private View.OnClickListener onClickUpdateUser() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePerfil();
            }
        };
    }

    private void setUser() {
        String key = mAuth.getCurrentUser().getUid();

        tv_nome = (TextView) findViewById(R.id.tv_username);
        tv_email = (TextView) findViewById(R.id.tv_email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        iv_perfil = (ImageView) findViewById(R.id.iv_perfil);

        mDatabaseUser.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                tv_nome.setText(mUser.nome);
                tv_email.setText(mUser.email);

                if (!mUser.imagem.equals(Tabelas.DEFAULT)) {
                    carregaImagem(iv_perfil, mUser.imagem, progressBar);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "Foto: " + mUser.imagem);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePerfil() {
        UpdateUserDialog.show(getSupportFragmentManager(), mUser, new UpdateUserDialog.Callback() {
            @Override
            public void onUserUpdate(User user) {
                tv_nome.setText(user.nome);
                mUser.nome = user.nome;
            }
        });
    }

}
