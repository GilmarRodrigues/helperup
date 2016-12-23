package br.com.alimentar.alergia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.activity.CategoriasActivity;
import br.com.alimentar.alergia.model.Tabelas;

import static br.com.alimentar.alergia.R.id.view;

public class CategoriaFragment extends Fragment {
    private String[] mCategorias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCategorias = Tabelas.addCategorias(getActivity());

        mCategorias = carregaLista();

        View view = inflater.inflate(R.layout.fragment_categoria, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mCategorias);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onItemClick());

        return view;
    }

    private AdapterView.OnItemClickListener onItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), CategoriasActivity.class);
                intent.putExtra("categoria", mCategorias[position].toString());
                startActivity(intent);
            }
        };
    }

    private String[] carregaLista() {
        String categorias[] = new String[mCategorias.length-1];
        for (int i = 0; i < mCategorias.length; i++) {
            if (i+1 < mCategorias.length)
            categorias[i] = mCategorias[i + 1];
        }
        return categorias;
    }

}