package com.jonli.fundkeeper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Li on 2016/11/14.
 **/

public class DialogAdapter extends BaseAdapter {

    private Context c;
    private String[] arr_country;
    private String[] arr_country2;
    private int[] arr_flag;
    private LayoutInflater myinflater;

    public DialogAdapter(Context context, String[] arr_country, String[] arr_country2, int[] images) {
        c = context;
        this.arr_country = arr_country;
        this.arr_country2 = arr_country2;
        arr_flag = images;
        myinflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arr_country.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if( convertView == null){
            LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.rate_dialog_item, null);
            holder = new ViewHolder();
            holder.country1 = (TextView) convertView.findViewById(R.id.dialog_item_txt1);
            holder.country2 = (TextView) convertView.findViewById(R.id.dialog_item_txt2);
            holder.image = (ImageView) convertView.findViewById(R.id.dialog_item_iv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.country1.setText(arr_country2[position]);
        holder.country2.setText(arr_country[position]);
        holder.image.setImageResource(arr_flag[position]);
        return convertView;
    }
    static class ViewHolder {
        TextView country1;
        TextView country2;
        ImageView image;
    }
}
