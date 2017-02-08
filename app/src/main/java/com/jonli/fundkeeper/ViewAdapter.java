package com.jonli.fundkeeper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewAdapter extends BaseAdapter {
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private LayoutInflater mInflater;
    private int count;
    private Boolean b;
    private ViewHolder holder;
    private ArrayList<ArrayList<String>> arr;
    public ViewAdapter(Context context,int count,Boolean b) {
        mDBHelper = new DBHelper(context);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.count = count;
        this.b = b;
    }

    public ViewAdapter(Context context,int count,Boolean b,ArrayList<ArrayList<String>> arr) {
        mDBHelper = new DBHelper(context);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.count = count;
        this.b = b;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return count;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.viewflow_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.imgView);
            holder.t = (TextView) convertView.findViewById(R.id.viewflow_text);
            holder.t1 = (TextView) convertView.findViewById(R.id.viewflow_text1);
            holder.t2 = (TextView) convertView.findViewById(R.id.viewflow_text2);
            holder.t3 = (TextView) convertView.findViewById(R.id.viewflow_text3);
            holder.rl = (RelativeLayout) convertView.findViewById(R.id.viewflow_rl);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        db = mDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM List WHERE id = ?",new String[]{(position+1)+""});
        c.moveToFirst();
        if (!b){
            holder.iv.setVisibility(View.VISIBLE);
            holder.rl.setVisibility(View.GONE);
            holder.iv.setImageResource(R.drawable.ic_content_plus);
        }else{
            holder.iv.setVisibility(View.GONE);
            holder.rl.setVisibility(View.VISIBLE);
            holder.t.setText(c.getString(1));
            holder.t1.setText(arr.get(position).get(1));
            holder.t2.setText("("+arr.get(position).get(2)+")");
            holder.t3.setText(arr.get(position).get(0));

            if (arr.get(position).get(2).indexOf("-") != -1){
                holder.t1.setTextColor(Color.parseColor("#ff0000"));
                holder.t2.setTextColor(Color.parseColor("#ff0000"));
            }else{
                holder.t1.setTextColor(Color.parseColor("#27ff00"));
                holder.t2.setTextColor(Color.parseColor("#27ff00"));
            }
        }
        c.close();
        return convertView;
    }

    private class ViewHolder{
        TextView t,t1,t2,t3;
        ImageView iv;
        RelativeLayout rl;
    }
}
