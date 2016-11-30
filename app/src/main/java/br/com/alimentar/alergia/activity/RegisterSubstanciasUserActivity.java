package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.SubstanciasAdapter;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;
import br.com.alimentar.alergia.model.User;

public class RegisterSubstanciasUserActivity extends BaseActivity {
    private DatabaseReference mDatabaseUser;
    private FirebaseAuth mAuth;
    private ListView mListView;
    private List<Substancia> mSubstanciasSwitch;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_substancias_user);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child(Tabelas.USUARIO);
        mDatabaseUser.keepSynced(true);

        mSubstanciasSwitch = Tabelas.addSubstancias(this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new SubstanciasAdapter(this, onClickSwitch(), mSubstanciasSwitch));

        findViewById(R.id.salvar_btn).setOnClickListener(onClickSalvar());
        findViewById(R.id.pular_btn).setOnClickListener(onClickPular());

        mUser = getIntent().getParcelableExtra(User.KEY);
    }

    private View.OnClickListener onClickPular() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUser.imagem.equals(Tabelas.DEFAULT)) {
                    final String key = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = mDatabaseUser.child(key);
                    User user = new User(mUser.nome, mUser.email, mUser.imagem);
                    current_user_db.setValue(user);
                }

                Intent mainIntent = new Intent(RegisterSubstanciasUserActivity.this, MainActivity.class);
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
            showProgressDialog(R.string.realizando_cadastro);

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = null;
                    if (dataSnapshot.hasChild(key)) {
                        if (mUser.imagem.equals(Tabelas.DEFAULT)) {
                            user = new User(mUser.nome, mUser.email, Tabelas.DEFAULT, mSubstanciasSwitch);
                            Map<String, Object> postValue = user.toMap();
                            Map<String, Object> childUpdates = new HashMap<String, Object>();
                            //Map<String, Object> hashtaghMap = new ObjectMapper().convertValue(childUpdates, Map.class);
                            childUpdates.put(key, postValue);
                            mDatabaseUser.updateChildren(childUpdates);
                        }
                    } else {
                        if (!mUser.imagem.equals(Tabelas.DEFAULT)) {
                            DatabaseReference current_user_db = mDatabaseUser.child(key);
                            user = new User(mUser.nome, mUser.email, mUser.imagem, mSubstanciasSwitch);
                            current_user_db.setValue(user);
                        }
                    }

                    Intent mainIntent = new Intent(RegisterSubstanciasUserActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            hideProgressDialog();
        }
    }


    private SubstanciasAdapter.OnClickSwitch onClickSwitch() {
        return new SubstanciasAdapter.OnClickSwitch() {
            @Override
            public void onClick(View view, int position) {
                if (mSubstanciasSwitch.get(position).status.equals(getString(R.string.const_nao_contem))) {
                    mSubstanciasSwitch.get(position).status = getString(R.string.const_contem);
                    Log.i("Script", "contem");
                } else {
                    mSubstanciasSwitch.get(position).status = getString(R.string.const_nao_contem);
                    Log.i("Script", "nao contem");
                }
            }
        };
    }

}
