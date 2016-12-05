package br.com.alimentar.alergia.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.adapter.ProdutoAdapter;
import br.com.alimentar.alergia.fragment.HomeFragment;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Tabelas;

import static br.com.alimentar.alergia.R.id.view;

public class CategoriasActivity extends BaseActivity {
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        String categoria = getIntent().getStringExtra("categoria");

        setUpToolbar();

        getSupportActionBar().setTitle(categoria);
        //toolbar.setSubtitle(categoria);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabase.keepSynced(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // ordenação decrecente
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        Query query = mDatabase.orderByChild("categoria").equalTo(categoria);


        mRecyclerView.setAdapter(new ProdutoAdapter(Produto.class, R.layout.row_produto, ProdutoAdapter.ViewHolder.class, query, this, onClick()));
    }

    private ProdutoAdapter.onClick onClick() {
        return new ProdutoAdapter.onClick() {
            @Override
            public void onClick(View view, int idx, String key) {
                Intent intent = new Intent(CategoriasActivity.this, ViewProdutoActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
