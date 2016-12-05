package br.com.alimentar.alergia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.alimentar.alergia.R;
import br.com.alimentar.alergia.model.Item;

/**
 * Created by gilmar on 24/10/16.
 */

public class ItemAdapter extends BaseAdapter{
    private  static ArrayList<Item> items;
    private LayoutInflater inflater;

    public ItemAdapter(Context context) {
        getActions(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getIconId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Item item = items.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            convertView.setTag(holder);

            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.label = (TextView) convertView.findViewById(R.id.tv_label);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageResource(item.getIconId());
        holder.label.setText(item.getLabel());

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView label;
    }

    static public ArrayList<Item> getActions(Context context){
        if(items == null){
            items = new ArrayList<>();
            items.add( new Item(R.drawable.ic_add_a_photo, context.getString(R.string.menu_boottom_sheet_camera)));
            items.add( new Item(R.drawable.ic_image, context.getString(R.string.menu_boottom_sheet_galeria)) );
        }
        return(items);
    }
}
