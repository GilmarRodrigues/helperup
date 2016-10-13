package br.com.alimentar.alergia.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Substancia;
import br.com.alimentar.alergia.model.Tabelas;

/**
 * Created by gilmar on 28/09/16.
 */

public class SubstanciasAdapter extends BaseAdapter {
    private List<Substancia> substancias = Tabelas.SUBSTANCIAS;
    private Context mContext;
    private LayoutInflater mInflater;
    private onClickCheckBox onClickCheckBox;

    public SubstanciasAdapter(Context context, onClickCheckBox onClickCheckBox) {
        this.mContext = context;
        this.onClickCheckBox = onClickCheckBox;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return substancias != null ? substancias.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return substancias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder = null;

        if (view == null) {
            view = mInflater.inflate(R.layout.row_substancia, parent, false);

            holder = new ViewHolder();
            holder.textSubstancia = (TextView) view.findViewById(R.id.text_substancia);
            holder.checkSubstancia = (CheckBox) view.findViewById(R.id.ckek_substancia);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textSubstancia.setText(substancias.get(position).nome);
        boolean checked = holder.checkSubstancia.isChecked();
        if (substancias.get(position).status.equals(Tabelas.CONTEM)) {
            holder.checkSubstancia.setChecked(true);
        } else {
            holder.checkSubstancia.setChecked(false);
        }

        if(onClickCheckBox != null) {
            holder.checkSubstancia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCheckBox.onClick(view, position);
                }
            });
        }

        return view;
    }

    static class ViewHolder {
        TextView textSubstancia;
        CheckBox checkSubstancia;
    }

    public interface onClickCheckBox {
        public void onClick(View view, int position);
    }
}
