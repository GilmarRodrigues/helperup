package br.com.alimentar.alergia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Substancia;

/**
 * Created by gilmar on 28/09/16.
 */

public class SubstanciasAdapter extends BaseAdapter {
    private List<Substancia> substancias;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnClickSwitch onClickSwitch;

    public SubstanciasAdapter(Context context, OnClickSwitch onClickSwitch, List<Substancia> substancias) {
        this.mContext = context;
        this.onClickSwitch = onClickSwitch;
        this.substancias = substancias;
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
            holder.switchSubstancia = (Switch) view.findViewById(R.id.switch_substancia);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textSubstancia.setText(substancias.get(position).nome);

        //boolean checked = holder.checkSubstancia.isChecked();
        if (substancias.get(position).status.equals(mContext.getString(R.string.const_contem))) {
            holder.switchSubstancia.setChecked(true);
            //Log.i("Script", "contem");
        } else {
            holder.switchSubstancia.setChecked(false);
            //Log.i("Script", "nao contem");
        }

        if(onClickSwitch != null) {
            holder.switchSubstancia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSwitch.onClick(view, position);
                }
            });
        }

        return view;
    }

    static class ViewHolder {
        TextView textSubstancia;
        Switch switchSubstancia;
    }

    public interface OnClickSwitch {
        public void onClick(View view, int position);
    }
}
