package br.com.alimentar.alergia.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.activity.ViewProdutoActivity;
import br.com.alimentar.alergia.adapter.ProdutoAdapter;
import br.com.alimentar.alergia.model.Produto;
import br.com.alimentar.alergia.model.Tabelas;

public class HomeFragment extends Fragment {
    private DatabaseReference mDatabase;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Tabelas.PRODUTO);
        mDatabase.keepSynced(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // ordenação decrecente
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);

        Query query = mDatabase;


        mRecyclerView.setAdapter(new ProdutoAdapter(Produto.class, R.layout.row_produto, ProdutoAdapter.ViewHolder.class, query, getContext(), onClick()));

        return view;
    }

    private ProdutoAdapter.onClick onClick() {
        return new ProdutoAdapter.onClick() {
            @Override
            public void onClick(View view, int idx, String key) {
                Intent intent = new Intent(getActivity(), ViewProdutoActivity.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        };
    }

}
