package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;

public class RegisterSubstanciaProdutoActivity extends BaseActivity {
    private DatabaseReference mDatabaseProduto;
    private ListView mListView;
    private List<Substancia> mSubstanciasSwitch;
    private Produto mProduto;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_substancia_produto);

        mDatabaseProduto = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabaseProduto.keepSynced(true);

        mSubstanciasSwitch = Tabelas.addSubstancias(this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new SubstanciasAdapter(this, onClickSwitch(), mSubstanciasSwitch));

        btnSalvar = (Button) findViewById(R.id.salvar_btn);
        btnSalvar.setOnClickListener(onClickSalvar());

        mProduto = getIntent().getParcelableExtra(Produto.KEY);

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

        if (mSubstanciasSwitch.size() > 0) {
            showProgressDialog(R.string.msg_salvando_alergenicos);

            mDatabaseProduto.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = null;
                    for (DataSnapshot produto: dataSnapshot.getChildren()) {
                        if (mProduto.codigo_barra.equals(produto.child("codigo_barra").getValue(String.class))) {
                            key = produto.getKey();
                        }
                    }
                    if (mProduto != null && key != null) {
                        Produto produto = new Produto(mProduto.nome, mProduto.fabricatente, mProduto.codigo_barra, mProduto.categoria, mProduto.imagem, mProduto.data, mProduto.status, mProduto.uid_user, mSubstanciasSwitch);
                        Map<String, Object> postValue = produto.toMap();
                        Map<String, Object> childUpdates = new HashMap<String, Object>();
                        childUpdates.put(key, postValue);
                        mDatabaseProduto.updateChildren(childUpdates);
                    }

                    Toast.makeText(RegisterSubstanciaProdutoActivity.this, getString(R.string.msg_produto_salvo), Toast.LENGTH_SHORT).show();

                    Intent mainIntent = new Intent(RegisterSubstanciaProdutoActivity.this, MainActivity.class);
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
                } else {
                    mSubstanciasSwitch.get(position).status = getString(R.string.const_nao_contem);
                }
            }
        };
    }
}
