package com.jonli.fundkeeper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Li on 2016/12/5.
 **/

public class Favorite extends Fragment{
    private View v;
    private LinearLayout mHead;
    private ListView favorite_list;
    private myAdapter myadapter;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private int list_count;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.favorite,container,false);
        initView();
        setHasOptionsMenu(true);
        return v;
    }

    private void initView(){
        mHead = (LinearLayout) v.findViewById(R.id.favorite_head);
        favorite_list = (ListView) v.findViewById(R.id.favorite_list);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_add).setVisible(false);
        menu.findItem(R.id.action_update).setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((HomeMainActivity)getActivity()).setHomeBack();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("觀察清單");
        mDBHelper = new DBHelper(getActivity());
        setList();

    }

    private void setList(){
        mHead.setFocusable(true);
        mHead.setClickable(true);
        mHead.setBackgroundColor(Color.parseColor("#b2d235"));
        mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

        favorite_list.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

        myadapter = new myAdapter(getActivity(), R.layout.hvl_item);
        favorite_list.setAdapter(myadapter);
        favorite_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                db = mDBHelper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT * FROM List WHERE id = ?",new String[]{i+1+""});
                c.moveToFirst();
                ((HomeMainActivity)getActivity()).addCount();
                Bundle bundle = new Bundle();
                bundle.putString("url",c.getString(3));
                bundle.putInt("company",c.getInt(11));
                bundle.putString("fundname",c.getString(1));
                FundInformation f = new FundInformation();
                f.setArguments(bundle);
                FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = mfragmentmanager.beginTransaction();
                ft.replace(R.id.fl_content,f).addToBackStack(null).commit();
                c.close();
            }
        });
        favorite_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupWindow(view,i);
                return true;
            }
        });
    }

    private void showPopupWindow(View view,int pos){
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window, null);
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        Button pop_btn1 = (Button) contentView.findViewById(R.id.pop_btn1);
        Button pop_btn2 = (Button) contentView.findViewById(R.id.pop_btn2);
        Button pop_btn3 = (Button) contentView.findViewById(R.id.pop_btn3);
        pop_btn1.setOnClickListener(new ButtonClick(pos,popupWindow));
        pop_btn1.setText("取消關注");
        pop_btn2.setVisibility(View.GONE);
        pop_btn3.setVisibility(View.GONE);
        popupWindow.showAsDropDown(view, 550, 0);
    }

    private class ButtonClick implements View.OnClickListener{
        int pos;
        PopupWindow pw;
        ButtonClick(int pos,PopupWindow pw){
            this.pos = pos; this.pw = pw;
        }
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.pop_btn1){
                Toast.makeText(getActivity(),"已取消清單",Toast.LENGTH_SHORT).show();

                db = mDBHelper.getWritableDatabase();
                db.delete("List","id = "+(pos+1),null);
                Cursor c = db.rawQuery("SELECT * FROM List;",null);

                c.moveToFirst();
                if (c.getCount() != 0){
                    do{
                        Log.e("TEST",c.getInt(0)+"/"+(pos+1));
                        if (c.getInt(0) > pos+1){
                            int id = c.getInt(0);
                            db.execSQL("UPDATE List SET id = ? WHERE id = ?",new Object[]{id-1,id});
                            Log.e("deleteAccount",c.getString(0)+"/"+c.getString(1));
                        }
                    }while (c.moveToNext());
                }
                myadapter.notifyDataSetChanged();
                c.close();
                pw.dismiss();
            }
        }
    }

    private class myAdapter extends BaseAdapter{
        private int id_row_layout;
        private LayoutInflater mInflater;

        public myAdapter(Context context, int id_row_layout) {
            super();
            this.id_row_layout = id_row_layout;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            db = mDBHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM List",null);
            list_count = c.getCount();
            c.close();
            return list_count;
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null){
                view = mInflater.inflate(id_row_layout, null);
                holder = new ViewHolder();
                MyHScrollView scrollView1 = (MyHScrollView) view.findViewById(R.id.horizontalScrollView1);
                holder.scrollView = scrollView1;
                holder.txt1 = (TextView) view.findViewById(R.id.textView1);
                holder.txt2 = (TextView) view.findViewById(R.id.textView2);
                holder.txt3 = (TextView) view.findViewById(R.id.textView3);
                holder.txt4 = (TextView) view.findViewById(R.id.textView4);
                holder.txt5 = (TextView) view.findViewById(R.id.textView5);
                holder.txt6 = (TextView) view.findViewById(R.id.textView6);
                holder.txt8 = (TextView) view.findViewById(R.id.textView8);
                holder.txt9 = (TextView) view.findViewById(R.id.textView9);
                holder.t1 = (TextView) view.findViewById(R.id.tx1);
                holder.t2 = (TextView) view.findViewById(R.id.tx2);
                MyHScrollView headSrcrollView = (MyHScrollView) mHead.findViewById(R.id.horizontalScrollView1);
                headSrcrollView.AddOnScrollChangedListener(new OnScrollChangedListenerImp(scrollView1));
                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }
            db = mDBHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM List WHERE id = ?",new String[]{i+1+""});
            c.moveToFirst();
            holder.txt1.setVisibility(View.GONE);
            holder.t1.setVisibility(View.VISIBLE); holder.t2.setVisibility(View.VISIBLE);
            holder.txt2.setText(c.getString(4));
            holder.txt3.setText(c.getString(5));
            holder.txt4.setText(c.getString(6));
            holder.txt5.setText(c.getString(7));
            holder.txt6.setText(c.getString(8));
            holder.txt8.setText(c.getString(9));
            holder.txt9.setText(c.getString(10));
            holder.t1.setText(c.getString(1));
            holder.t2.setText(c.getString(2));
            c.close();
            return view;
        }
    }

    private class ViewHolder {
        TextView txt1,txt2,txt3,txt4,txt5,txt6,txt8,txt9,t1,t2;
        HorizontalScrollView scrollView;
    }

    private class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent arg1) {
            HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
                    .findViewById(R.id.horizontalScrollView1);
            headSrcrollView.onTouchEvent(arg1);
            return false;
        }
    }

    private class OnScrollChangedListenerImp implements MyHScrollView.OnScrollChangedListener {
        MyHScrollView mScrollViewArg;

        public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
            mScrollViewArg = scrollViewar;
        }
        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            mScrollViewArg.smoothScrollTo(l, t);
        }
    }
}
