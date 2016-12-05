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

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.activity.CategoriasActivity;
import br.com.alimentar.alergia.model.Tabelas;

public class CategoriaFragment extends Fragment {
    private String[] mCategorias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCategorias = new String[]{getString(R.string.categoria_bebidas), getString(R.string.categoria_biscoitos),
                getString(R.string.categoria_congelados), getString(R.string.categoria_doces_sobremesas),
                getString(R.string.categoria_enlatados), getString(R.string.categoria_graos_cereais), getString(R.string.categoria_leite),
                getString(R.string.categoria_iorgute), getString(R.string.categoria_massa), getString(R.string.categoria_paes),
                getString(R.string.categoria_bolos), getString(R.string.categoria_queijo_frios)};

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

}