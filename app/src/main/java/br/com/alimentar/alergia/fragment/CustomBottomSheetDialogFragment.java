package br.com.alimentar.alergia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.activity.RegisterProdutoActivity;
import br.com.alimentar.alergia.adapter.ItemAdapter;
import br.com.alimentar.alergia.model.Item;

/**
 * Created by gilmar on 24/10/16.
 */
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String FRAGMENT_KEY = "CustomBottomSheetDialogFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container);

        ItemAdapter adapter = new ItemAdapter(getActivity());

        ListView lv = (ListView) view.findViewById(R.id.lv_itens);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(onItemClick());

        return(view);
    }

    private AdapterView.OnItemClickListener onItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Pos: " + position, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
