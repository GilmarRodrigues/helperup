package br.com.alimentar.alergia.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.SubstanciasAdapter;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;

import static android.os.Build.VERSION_CODES.M;
import static com.google.android.gms.analytics.internal.zzy.B;
import static com.google.android.gms.analytics.internal.zzy.i;
import static com.google.android.gms.analytics.internal.zzy.s;
import static com.google.android.gms.internal.zzng.fa;

public class SubstanciasActivity extends BaseActivity {
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private ListView mListView;
    private List<Substancia> mSubstanciasSwitch = Tabelas.SUBSTANCIAS;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substancias);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUser.keepSynced(true);

        mProgress = new ProgressDialog(this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new SubstanciasAdapter(this, onClickSwitch()));

        findViewById(R.id.salvar_btn).setOnClickListener(onClickSalvar());
        findViewById(R.id.pular_btn).setOnClickListener(onClickPular());

        mUser = getIntent().getParcelableExtra(User.KEY);

    }

    private View.OnClickListener onClickPular() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUser.imagem.equals("default")) {
                    final String key = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = mDatabaseUser.child(key);
                    User user = new User(mUser.nome, mUser.email, mUser.imagem);
                    current_user_db.setValue(user);
                }

                Intent mainIntent = new Intent(SubstanciasActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        };
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSubstancia();
            }
        };
    }

    private void startSubstancia() {
        final String key = mAuth.getCurrentUser().getUid();

        if (mSubstanciasSwitch.size() > 0) {
            mProgress.setTitle(getString(R.string.aguarde));
            mProgress.setMessage(getString(R.string.realizando_cadastro));
            mProgress.show();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = null;
                    if (dataSnapshot.hasChild(key)) {
                        if (mUser.imagem.equals("default")) {
                            user = new User(mUser.nome, mUser.email, "default", mSubstanciasSwitch);
                            Map<String, Object> postValue = user.toMap();
                            Map<String, Object> childUpdates = new HashMap<String, Object>();
                            //Map<String, Object> hashtaghMap = new ObjectMapper().convertValue(childUpdates, Map.class);
                            childUpdates.put(key, postValue);
                            mDatabaseUser.updateChildren(childUpdates);
                        }
                    } else {
                        if (!mUser.imagem.equals("default")) {
                            DatabaseReference current_user_db = mDatabaseUser.child(key);
                            user = new User(mUser.nome, mUser.email, mUser.imagem, mSubstanciasSwitch);
                            current_user_db.setValue(user);
                        }
                    }

                    Intent mainIntent = new Intent(SubstanciasActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            mProgress.dismiss();
        }
    }


    private SubstanciasAdapter.OnClickSwitch onClickSwitch() {
        return new SubstanciasAdapter.OnClickSwitch() {
            @Override
            public void onClick(View view, int position) {
                if (mSubstanciasSwitch.get(position).status.equals(Tabelas.NAOCONTEM)) {
                    mSubstanciasSwitch.get(position).status = Tabelas.CONTEM;
                } else {
                    mSubstanciasSwitch.get(position).status = Tabelas.NAOCONTEM;
                }
            }
        };
    }

}
