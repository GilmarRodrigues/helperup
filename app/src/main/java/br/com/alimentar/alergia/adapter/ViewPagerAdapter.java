package br.com.alimentar.alergia.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.alimentar.alergia.R;

/**
 * Created by gilmar on 09/10/16.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private int[] mResources;
    private int[] titles;
    private int[] descriptions;

    public ViewPagerAdapter(Context mContext, int[] mResources, int[] titles, int[] descriptions) {
        this.mContext = mContext;
        this.mResources = mResources;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.row_view_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        imageView.setImageResource(mResources[position]);

        TextView title = (TextView) itemView.findViewById(R.id.text_title);
        title.setText(mContext.getString(titles[position]));

        TextView description = (TextView) itemView.findViewById(R.id.text_description);
        description.setText(mContext.getString(descriptions[position]));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
