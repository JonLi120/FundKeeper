package com.jonli.fundkeeper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Li on 2016/11/19.
 **/

public class Account extends Fragment{
    private View v;
    private ListView account_list;
    private AccountAdapter adapter;
    private DBHelper myDBHelper;
    private SQLiteDatabase db;

    private TableLayout ad_plus1,ad_plus2;
    private LinearLayout ad_plus3;
    private EditText ad_edit_date,ad_edit_name,ad_edit_remarks,ad_edit_cash,ad_edit_whomoney,ad_edit_fundname,ad_edit_unit,ad_edit_price,ad_edit_free,ad_edit_date2,ad_edit_transfer;
    private Spinner ad_spinner_class,ad_spinner_account,ad_spinner_fundname,ad_spinner_account2,ad_spinner_account3;
    private TextView ad_title,ad_txt_cash,ad_text_whomoney,ad_text_fundname,ad_text_unit,ad_text_price,ad_text_free;
    private Button btn_income,btn_pay,btn_transfer,ad_btn_cancel,ad_btn_add;
    private AutoCompleteTextView ad_auto_fundname;

    private int Account_number,Account_position;
    private String[] sp_class_income = new String[]{"現金收入","基金利息收入","賣出基金收入"};
    private String[] sp_class_cost = new String[]{"現金支出","購買基金"};
    private ArrayList arr_list_name, arr_list_cash, arr_fund_name ;

    private int mYear,mMonth,mDay,autoindex;
    private float f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.account, container, false);
        init();
        setHasOptionsMenu(true);
        return v;

    }

    private void init(){
        account_list = (ListView) v.findViewById(R.id.acc_list);
        btn_income = (Button) v.findViewById(R.id.btn_income);
        btn_pay = (Button) v.findViewById(R.id.btn_pay);
        btn_transfer = (Button) v.findViewById(R.id.btn_transfer);

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                showDialog(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        myDBHelper = new DBHelper(getActivity());
        db = myDBHelper.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM Account;",null);
        if (cursor.getCount() == 0){
            db = myDBHelper.getWritableDatabase();
            db.execSQL("INSERT INTO Account VALUES (?, ?, ?, ?);",new Object[]{0,"基金帳本","預設",0});
        }
        cursor.close();

        adapter = new AccountAdapter(getActivity());
        account_list.setAdapter(adapter);
        account_list.setOnItemLongClickListener(new LongClick());
        account_list.setOnItemClickListener(new ItemClick());

        btn_income.setOnClickListener(new ButtonClick());
        btn_pay.setOnClickListener(new ButtonClick());
        btn_transfer.setOnClickListener(new ButtonClick());
    }
    public void showDatePickerDialog() {
        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                        // 完成選擇，顯示日期
                        ad_edit_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        ad_edit_date2.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }
    private void showDialog(final int s){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.account_dialog, null);

        ad_plus1 = (TableLayout) view.findViewById(R.id.ad_plus_layout1);
        ad_plus2 = (TableLayout) view.findViewById(R.id.ad_plus_layout2);
        ad_plus3 = (LinearLayout) view.findViewById(R.id.ad_plus_layout3);

        ad_edit_name = (EditText) view.findViewById(R.id.ad_edit_name);
        ad_edit_remarks = (EditText) view.findViewById(R.id.ad_edit_remarks);
        ad_edit_cash = (EditText) view.findViewById(R.id.ad_edit_cash);
        ad_edit_date = (EditText) view.findViewById(R.id.ad_edit_date);
        ad_edit_whomoney = (EditText) view.findViewById(R.id.ad_edit_whomoney);
        ad_edit_fundname = (EditText) view.findViewById(R.id.ad_edit_fundname);
        ad_edit_unit = (EditText) view.findViewById(R.id.ad_edit_unit);
        ad_edit_price = (EditText) view.findViewById(R.id.ad_edit_price);
        ad_edit_free = (EditText) view.findViewById(R.id.ad_edit_free);
        ad_edit_date2 = (EditText) view.findViewById(R.id.ad_edit_date2);
        ad_edit_transfer = (EditText) view.findViewById(R.id.ad_edit_transfer);

        ad_spinner_class = (Spinner) view.findViewById(R.id.ad_spinner_class);
        ad_spinner_account = (Spinner) view.findViewById(R.id.ad_spinner_account);
        ad_spinner_fundname = (Spinner) view.findViewById(R.id.ad_spinner_fundname);
        ad_spinner_account2 = (Spinner) view.findViewById(R.id.ad_spinner_account2);
        ad_spinner_account3 = (Spinner) view.findViewById(R.id.ad_spinner_account3);

        ad_btn_cancel = (Button) view.findViewById(R.id.ad_btn_cancel);
        ad_btn_add = (Button) view.findViewById(R.id.ad_btn_add);

        ad_title = (TextView) view.findViewById(R.id.ad_title);
        ad_txt_cash = (TextView) view.findViewById(R.id.ad_text_cash);
        ad_text_whomoney = (TextView) view.findViewById(R.id.ad_text_whomoney);
        ad_text_fundname = (TextView) view.findViewById(R.id.ad_text_fundname);
        ad_text_unit = (TextView) view.findViewById(R.id.ad_text_unit);
        ad_text_price = (TextView) view.findViewById(R.id.ad_text_price);
        ad_text_free = (TextView) view.findViewById(R.id.ad_text_free);

        ad_auto_fundname = (AutoCompleteTextView) view.findViewById(R.id.ad_auto_fundname);

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(view).setCancelable(false);
        final AlertDialog dialog = ad.create();
        dialog.show();

        switch (s){
            case 0:
                setDialogLayout(0);
                break;
            case 1:
                setDialogLayout(1);
                db = myDBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE id = ?;",new String[]{Account_position+""});
                cursor.moveToFirst();
                ad_edit_name.setText(cursor.getString(1));
                ad_edit_remarks.setText(cursor.getString(2));
                cursor.close();
                break;
            case 2:
                setDialogLayout(2);
                setDialogView(0);

                ArrayAdapter<String> arr_adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp_class_income);
                arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_class.setAdapter(arr_adapter);

                ArrayAdapter<String> arr_adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_list_name);
                arr_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_account.setAdapter(arr_adapter2);

                ad_spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0){
                            setDialogView(0);
                        }else if (i == 1 || i == 2){
                            setDialogView(1);
                            if (i == 1){
                                ad_text_price.setText("單位利息:");
                            }else if (i == 2){
                                ad_text_price.setText("賣出價格:"); ad_text_unit.setText("賣出量:");
                            }

                            db = myDBHelper.getReadableDatabase();
                            Cursor c = db.rawQuery("SELECT * FROM Fund WHERE A_id = ?",new String[]{ad_spinner_account.getSelectedItemPosition()+""});
                            if (c.getCount() == 0){
                                ad_edit_unit.setEnabled(false); ad_edit_price.setEnabled(false); ad_edit_free.setEnabled(false);
                                Toast.makeText(getActivity(), "此帳戶無基金", Toast.LENGTH_SHORT).show();
                                arr_fund_name = new ArrayList<>();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_fund_name);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                ad_spinner_fundname.setAdapter(adapter);
                                ad_btn_add.setClickable(false);
                            }else{
                                ad_edit_unit.setEnabled(true); ad_edit_price.setEnabled(true); ad_edit_free.setEnabled(true);
                                arr_fund_name = new ArrayList<>();
                                Cursor cursor1 = db.rawQuery("SELECT name FROM Fund WHERE A_id = ? GROUP BY name",new String[]{ad_spinner_account.getSelectedItemPosition()+""});
                                cursor1.moveToFirst();
                                do {
                                    arr_fund_name.add(cursor1.getString(0));
                                }while (cursor1.moveToNext());
                                cursor1.close();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_fund_name);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                ad_spinner_fundname.setAdapter(adapter);
                                ad_btn_add.setClickable(true);
                            }
                            c.close();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
                ad_spinner_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        db = myDBHelper.getReadableDatabase();
                        Cursor c = db.rawQuery("SELECT * FROM Fund WHERE A_id = ?",new String[]{i+""});
                        if (c.getCount() == 0 && (ad_spinner_class.getSelectedItemPosition() == 1 || ad_spinner_class.getSelectedItemPosition() == 2)){
                            ad_edit_unit.setEnabled(false); ad_edit_price.setEnabled(false); ad_edit_free.setEnabled(false);
                            Toast.makeText(getActivity(), "此帳戶無基金", Toast.LENGTH_SHORT).show();
                            arr_fund_name = new ArrayList<>();
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_fund_name);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            ad_spinner_fundname.setAdapter(adapter);
                            ad_btn_add.setClickable(false);
                        }else if(c.getCount() != 0 && (ad_spinner_class.getSelectedItemPosition() == 1|| ad_spinner_class.getSelectedItemPosition() == 2)){
                            ad_edit_unit.setEnabled(true); ad_edit_price.setEnabled(true); ad_edit_free.setEnabled(true);
                            arr_fund_name = new ArrayList<>();
                            Cursor cursor1 = db.rawQuery("SELECT name FROM Fund WHERE A_id = ? GROUP BY name",new String[]{i+""});
                            cursor1.moveToFirst();
                            do {
                                arr_fund_name.add(cursor1.getString(0));
                            }while (cursor1.moveToNext());
                            cursor1.close();
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_fund_name);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            ad_spinner_fundname.setAdapter(adapter);
                            ad_btn_add.setClickable(true);
                        }
                        c.close();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
                break;
            case 3:
                setDialogLayout(3);
                setDialogView(2);
                ArrayAdapter<String> arr_adapter3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,sp_class_cost);
                arr_adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_class.setAdapter(arr_adapter3);

                ArrayAdapter<String> arr_adapter4 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_list_name);
                arr_adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_account.setAdapter(arr_adapter4);

                ad_spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0){
                            setDialogView(2);
                        }else{
                            setDialogView(3);
                            ad_spinner_fundname.setVisibility(View.GONE);ad_edit_fundname.setVisibility(View.GONE);
                            ad_auto_fundname.setVisibility(View.VISIBLE); ad_auto_fundname.setThreshold(1);

                            ad_auto_fundname.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    autoindex = -1;
                                    String str = ad_auto_fundname.getText().toString();

                                    db = myDBHelper.getReadableDatabase();
                                    Cursor c = db.rawQuery("SELECT * FROM FundData WHERE name LIKE ?",new String[]{"%"+str+"%"});

                                    CourseNameAdapter cAdapter = new CourseNameAdapter(getActivity() , c);
                                    ad_auto_fundname.setAdapter(cAdapter);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                }
                            });
                            ad_auto_fundname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    autoindex = i;
                                }
                            });

                            }
                        }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
                break;
            case 4:
                setDialogLayout(4);
                ArrayAdapter<String> arr_adapter5 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_list_name);
                arr_adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_account2.setAdapter(arr_adapter5);

                ArrayAdapter<String> arr_adapter6 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arr_list_name);
                arr_adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ad_spinner_account3.setAdapter(arr_adapter6);
                break;
        }

        ad_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (s){
                    case 0:
                        if (ad_edit_name.getText().toString().trim().equals("")){
                            Toast.makeText(getActivity(), "帳戶名稱不為空", Toast.LENGTH_SHORT).show();
                            ad_edit_name.setSelectAllOnFocus(true);
                        }else if (ad_edit_cash.getText().toString().trim().equals("")){
                            Toast.makeText(getActivity(), "初始金額不為空", Toast.LENGTH_SHORT).show();
                            ad_edit_cash.setSelectAllOnFocus(true);
                        }else{
                            String name = ad_edit_name.getText().toString();
                            String remarks = ad_edit_remarks.getText().toString();
                            String cash = ad_edit_cash.getText().toString();
                            addAccount(Account_number,name,remarks,cash);
                            dialog.cancel();
                        }
                        break;
                    case 1:
                        if (ad_edit_name.getText().toString().trim().equals("")){
                            Toast.makeText(getActivity(), "帳戶名稱不為空", Toast.LENGTH_SHORT).show();
                            ad_edit_name.setSelectAllOnFocus(true);
                        }else{
                            String name = ad_edit_name.getText().toString();
                            String remarks = ad_edit_remarks.getText().toString();
                            updataAccount(name,remarks);
                            dialog.cancel();
                        }
                        break;
                    case 2:
                        if(ad_spinner_class.getSelectedItemPosition() == 0){
                            if(ad_edit_whomoney.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(), "請輸入金額", Toast.LENGTH_SHORT).show();
                                ad_edit_whomoney.setSelectAllOnFocus(true);
                            }else{
                                int income = 1;
                                int account = ad_spinner_account.getSelectedItemPosition();
                                String date = ad_edit_date.getText().toString().trim();
                                int money = Integer.parseInt(ad_edit_whomoney.getText().toString().trim());
                                String remark = ad_edit_fundname.getText().toString().trim();
                                addIncomeData(date,money,income,remark,1,account,"null");
                                dialog.cancel();
                            }
                        }else{
                            if (ad_edit_unit.getText().toString().trim().equals("")){
                                if (ad_spinner_class.getSelectedItemPosition() == 1)
                                    Toast.makeText(getActivity(),"請輸入購買單位數",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(),"請輸入賣出單位數",Toast.LENGTH_SHORT).show();
                                ad_edit_unit.setSelectAllOnFocus(true);
                            }else if (ad_edit_price.getText().toString().trim().equals("")){
                                if (ad_spinner_class.getSelectedItemPosition() == 1)
                                    Toast.makeText(getActivity(),"請輸入購買單位利息",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(),"請輸入賣出單位價格",Toast.LENGTH_SHORT).show();
                                ad_edit_price.setSelectAllOnFocus(true);
                            }else if (ad_edit_free.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(),"請輸入手續費",Toast.LENGTH_SHORT).show();
                                ad_edit_free.setSelectAllOnFocus(true);
                            }else{
                                int interest;
                                int account = ad_spinner_account.getSelectedItemPosition();
                                String date = ad_edit_date.getText().toString().trim();
                                String fundname = ad_spinner_fundname.getSelectedItem().toString();
                                float unit = Float.parseFloat(ad_edit_unit.getText().toString());
                                float price = Float.parseFloat(ad_edit_price.getText().toString());
                                int free = Integer.parseInt(ad_edit_free.getText().toString());
                                db = myDBHelper.getReadableDatabase();
                                Cursor c = db.rawQuery("SELECT * FROM FundData WHERE name = ?",new String[]{fundname});
                                c.moveToFirst();
                                String url = c.getString(2);
                                c.close();
                                if (ad_spinner_class.getSelectedItemPosition() == 1) {
                                    interest = 1;
                                    addFundData(account,date,fundname,unit,price,free,url,interest);
                                } else {
                                    interest = -1;
                                    Cursor c1 = db.rawQuery("SELECT SUN(unit) s_unit FROM Fund WHERE interest = 0 AND name = ? GROUP BY name",new String[]{fundname});
                                    Cursor c2 = db.rawQuery("SELECT SUN(unit) s_unit FROM Fund WHERE interest = -1 AND name = ? GROUP BY name",new String[]{fundname});
                                    c1.moveToFirst(); c2.moveToFirst();
                                    if (unit < (c1.getInt(0)-c2.getInt(0))){
                                        ad_edit_unit.setSelectAllOnFocus(true);
                                        Toast.makeText(getActivity(),"賣出單位數大於購買數",Toast.LENGTH_SHORT).show();
                                    }else{
                                        addFundData(account,date,fundname,unit,price,free,url,interest);
                                        dialog.cancel();
                                    }
                                    c1.close(); c2.close();
                                }
                            }
                        }
                        break;
                    case 3:
                        if (ad_spinner_class.getSelectedItemPosition() == 0){
                            if (ad_edit_whomoney.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(), "請輸入金額", Toast.LENGTH_SHORT).show();
                                ad_edit_whomoney.setSelectAllOnFocus(true);
                            }else{
                                int income = 3;
                                int account = ad_spinner_account.getSelectedItemPosition();
                                String date = ad_edit_date.getText().toString().trim();
                                int money = Integer.parseInt(ad_edit_whomoney.getText().toString().trim());
                                String remark = ad_edit_fundname.getText().toString().trim();
                                addIncomeData(date,money,income,remark,0,account,"null");
                                dialog.cancel();
                            }
                        }else{
                            if (ad_auto_fundname.getText().toString().equals("")){
                                Toast.makeText(getActivity(), "請輸入基金名稱", Toast.LENGTH_SHORT).show();
                                ad_auto_fundname.setSelectAllOnFocus(true);
                            }else if (autoindex == -1){
                                Toast.makeText(getActivity(), "請輸入選擇一檔基金", Toast.LENGTH_SHORT).show();
                                ad_auto_fundname.setSelectAllOnFocus(true);
                            }else if (ad_edit_unit.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(),"請輸入購買單位數",Toast.LENGTH_SHORT).show();
                                ad_edit_unit.setSelectAllOnFocus(true);
                            }else if (ad_edit_price.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(),"請輸入購買單位價格",Toast.LENGTH_SHORT).show();
                                ad_edit_price.setSelectAllOnFocus(true);
                            }else if (ad_edit_free.getText().toString().trim().equals("")){
                                Toast.makeText(getActivity(),"請輸入手續費",Toast.LENGTH_SHORT).show();
                                ad_edit_free.setSelectAllOnFocus(true);
                            }else{
                                int income = 4;
                                int interest = 0;
                                int account = ad_spinner_account.getSelectedItemPosition();
                                String date = ad_edit_date.getText().toString().trim();
                                String fundname = ad_auto_fundname.getText().toString();
                                float unit = Float.parseFloat(ad_edit_unit.getText().toString());
                                float price = Float.parseFloat(ad_edit_price.getText().toString());
                                int free = Integer.parseInt(ad_edit_free.getText().toString());
                                db = myDBHelper.getReadableDatabase();
                                Cursor c = db.rawQuery("SELECT * FROM FundData WHERE name = ?",new String[]{fundname});
                                c.moveToFirst();
                                String url = c.getString(2);
                                c = db.rawQuery("SELECT cash FROM Account WHERE id = ?",new String[]{account+""});
                                c.moveToFirst();
                                if ((unit*price)>c.getFloat(0)){
                                    ad_edit_unit.setSelectAllOnFocus(true);
                                    Toast.makeText(getActivity(),"購買大於現金餘額",Toast.LENGTH_SHORT).show();
                                }else{
                                    addFundData(account,date,fundname,unit,price,free,url,interest);
                                    dialog.cancel();
                                }
                                c.close();
                            }
                        }
                        break;
                    case 4:
                        int account1 = ad_spinner_account2.getSelectedItemPosition();
                        if (ad_edit_transfer.getText().toString().trim().equals("")){
                            Toast.makeText(getActivity(), "請輸入金額", Toast.LENGTH_SHORT).show();
                            ad_edit_transfer.setSelectAllOnFocus(true);
                        }else if(Integer.parseInt(ad_edit_transfer.getText().toString().trim())>(int) arr_list_cash.get(account1)){
                            Toast.makeText(getActivity(), "帳戶沒有那麼多錢", Toast.LENGTH_SHORT).show();
                            ad_edit_transfer.setSelectAllOnFocus(true);
                        }else if(Integer.parseInt(ad_edit_transfer.getText().toString().trim()) == 0){
                            Toast.makeText(getActivity(), "金額不要輸入0", Toast.LENGTH_SHORT).show();
                            ad_edit_transfer.setSelectAllOnFocus(true);
                        }else if (ad_spinner_account2.getSelectedItemPosition() == ad_spinner_account3.getSelectedItemPosition()){
                            Toast.makeText(getActivity(), "轉帳帳戶不可以一樣", Toast.LENGTH_SHORT).show();
                        }else{
                            String date = ad_edit_date2.getText().toString().trim();
                            int money = Integer.parseInt(ad_edit_transfer.getText().toString().trim());
                            int account2 = ad_spinner_account3.getSelectedItemPosition();
                            addTransfer(date,money,account1,account2);
                            dialog.cancel();
                        }
                        break;
                }
            }
        });
        ad_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private class CourseNameAdapter extends CursorAdapter{
        private LayoutInflater layoutInflater;

        public CourseNameAdapter(Context context, Cursor c) {
            super(context, c);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.autotext_item, null);
            setView(view, cursor);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            setView(view, cursor);
        }

        private void setView(View view, Cursor cursor)
        {
            TextView tv = (TextView) view.findViewById(R.id.auto_text);
            tv.setText(cursor.getString(cursor.getColumnIndex("name")));
        }

        @Override
        public CharSequence convertToString(Cursor cursor)
        {
            return cursor == null ? "" : cursor.getString(cursor.getColumnIndex("name"));
        }
    }

    private void setDialogLayout(int s){
        String str[] = new String[]{"新增帳本","修改帳本","新增收入","新增支出","轉帳"};
        String str2[] = new String[]{"新增","修改","新增","新增","確定"};
        int str3[] = new int[]{View.VISIBLE,View.GONE};
        ad_title.setText(str[s]);
        ad_btn_add.setText(str2[s]);
        if (s == 0 || s == 1){
            ad_plus1.setVisibility(View.VISIBLE); ad_plus2.setVisibility(View.GONE); ad_plus3.setVisibility(View.GONE);
            ad_txt_cash.setVisibility(str3[s]);ad_edit_cash.setVisibility(str3[s]); ad_auto_fundname.setVisibility(View.GONE);
        }else if (s == 2 || s == 3){
            ad_plus1.setVisibility(View.GONE); ad_plus2.setVisibility(View.VISIBLE); ad_plus3.setVisibility(View.GONE);
            ad_auto_fundname.setVisibility(View.GONE);
        }else if (s == 4){
            ad_plus1.setVisibility(View.GONE); ad_plus2.setVisibility(View.GONE); ad_plus3.setVisibility(View.VISIBLE);
            ad_auto_fundname.setVisibility(View.GONE);
        }
        if (s == 2 || s == 3){
            //設置時間
            ad_edit_date.setInputType(InputType.TYPE_NULL);
            ad_edit_date.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
            ad_edit_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog();
                }
            });
            ad_edit_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b == true) showDatePickerDialog();
                }
            });
        }else if (s == 4){
            ad_edit_date2.setInputType(InputType.TYPE_NULL);
            ad_edit_date2.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
            ad_edit_date2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog();
                }
            });
            ad_edit_date2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b == true) showDatePickerDialog();
                }
            });
        }

    }
    private void setDialogView(int s){
        int i[] = new int[]{View.GONE,View.VISIBLE};
        String str[] = new String[]{"收入備註：","基金名稱：","費用備註：","基金名稱："};

        ad_edit_unit.setVisibility(i[s%2]); ad_edit_price.setVisibility(i[s%2]); ad_edit_free.setVisibility(i[s%2]);
        ad_text_unit.setVisibility(i[s%2]); ad_text_price.setVisibility(i[s%2]); ad_text_free.setVisibility(i[s%2]);
        ad_spinner_fundname.setVisibility(i[s%2]);ad_edit_fundname.setVisibility(i[(s+1)%2]);
        ad_text_whomoney.setVisibility(i[(s+1)%2]); ad_edit_whomoney.setVisibility(i[(s+1)%2]);

        ad_text_fundname.setText(str[s]);
    }

    private class ItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((HomeMainActivity)getActivity()).addCount();

            Bundle bundle = new Bundle();
            bundle.putInt("acc pos", i);
            AccountContent f = new AccountContent();
            f.setArguments(bundle);
            FragmentManager mfragmentmanager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = mfragmentmanager.beginTransaction();
            ft.replace(R.id.fl_content, f).addToBackStack(null).commit();
        }
    }

    private class LongClick implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            showPopupWindow(view,i);
            Account_position = i;
            return false;
        }
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
        pop_btn2.setOnClickListener(new ButtonClick(pos,popupWindow));
        pop_btn3.setOnClickListener(new ButtonClick(pos,popupWindow));
        popupWindow.showAsDropDown(view, 50, 0);
    }
    private class ButtonClick implements View.OnClickListener{
        int pos;
        PopupWindow pw;
        ButtonClick(int pos,PopupWindow pw){
            this.pos = pos;
            this.pw = pw;
        }
        ButtonClick(){}
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_income:
                    showDialog(2);
                    break;
                case R.id.btn_pay:
                    showDialog(3);
                    break;
                case R.id.btn_transfer:
                    showDialog(4);
                    break;
                case R.id.pop_btn1: //編輯
                    showDialog(1);
                    pw.dismiss();
                    break;
                case R.id.pop_btn2: //刪除
                    deleteAccountData(pos);
                    pw.dismiss();
                    break;
                case R.id.pop_btn3: //取消
                    pw.dismiss();
                    break;
            }
        }
    }

    private class AccountAdapter extends BaseAdapter{

        private LayoutInflater myInflater;
        private SQLiteDatabase db;

        public AccountAdapter(Context context) {
            myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            db = myDBHelper.getReadableDatabase();
            Cursor cursor =  db.rawQuery("SELECT * FROM Account;",null);
            Account_number = cursor.getCount();
            arr_list_name = new ArrayList();
            arr_list_cash = new ArrayList();
            cursor.moveToFirst();
            do{
                arr_list_name.add(cursor.getString(1));
                arr_list_cash.add(cursor.getFloat(3));
            }while (cursor.moveToNext());
            cursor.close();
            return Account_number;
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
        public View getView(int i, View v, ViewGroup viewGroup) {
            ViewHolder holder;

            if( v == null){
                v = myInflater.inflate(R.layout.account_item, null);
                holder = new ViewHolder();
                holder.item_name = (TextView) v.findViewById(R.id.acc_item_title);
                holder.item_fund_total = (TextView) v.findViewById(R.id.acc_item_txt1);
                holder.item_loss_total = (TextView) v.findViewById(R.id.acc_item_txt2);
                holder.item_cash_total = (TextView) v.findViewById(R.id.acc_item_txt3);
                holder.item_acc_total = (TextView) v.findViewById(R.id.acc_item_txt4);
                v.setTag(holder);
            }else {
                holder = (ViewHolder) v.getTag();
            }
            DecimalFormat df = new DecimalFormat("$#,###,##0.##");

            String accountname = getAccountName(i);
            Float cashtotal = (Float) arr_list_cash.get(i);
            float fundtotal[] = getFundTotal(i);

            holder.item_name.setText(accountname);
            holder.item_fund_total.setText(df.format(fundtotal[0]));
            float loss =fundtotal[1];
            holder.item_loss_total.setText(df.format(loss));
            if (loss > 0){
                holder.item_loss_total.setTextColor(0xFF22E400);
            }else if (loss == 0){
                holder.item_loss_total.setTextColor(0xFF000000);
            }else if(loss < 0){
                holder.item_loss_total.setTextColor(0xFFFF1F1F);
            }
            Log.e("GetView","i="+i+" cashtotal="+cashtotal);
            Log.e("+","------------------");
            holder.item_cash_total.setText(df.format(cashtotal));
            Object total = fundtotal[0]+fundtotal[1]+cashtotal;
            if ((Float)total >= 0){
                holder.item_acc_total.setTextColor(0xFF22E400);
            }else{
                holder.item_acc_total.setTextColor(0xFFFF1F1F);
            }
            holder.item_acc_total.setText(df.format(total));
            return v;
        }

    }
    private class ViewHolder{
       TextView item_name,item_fund_total,item_loss_total,item_cash_total,item_acc_total;
    }
    private void addAccount(int num,String name,String remark,String cash){
        db = myDBHelper.getWritableDatabase();
        Account_number = num+1;
        db.execSQL("INSERT INTO Account VALUES (?, ?, ?, ?);",new Object[]{num,name,remark,Integer.parseInt(cash)});
        adapter.notifyDataSetChanged();
    }
    private void addIncomeData(String date, Object money, int c, String remark, int positive, int aid, Object fid){
        db = myDBHelper.getWritableDatabase();
        db.execSQL("INSERT INTO Record(date,money,class,remarks,positive,A_id,F_id) VALUES (?,?,?,?,?,?,?);",new Object[]{date,money,c,remark,positive,aid,fid});
        if (positive == 0){
            db.execSQL("UPDATE Account SET cash = cash-? WHERE id = ?",new Object[]{money,aid});
        }else{
            db.execSQL("UPDATE Account SET cash = cash+? WHERE id = ?",new Object[]{money,aid});
        }
        adapter.notifyDataSetChanged();
    }
    private void addFundData(int account,String date,String fundname,float unit,float price,int free,String url,int interest){
        db = myDBHelper.getWritableDatabase();

        db.execSQL("INSERT INTO Fund(name,date,url,unit,price,free,interest,A_id) VALUES (?,?,?,?,?,?,?,?)",new Object[]{fundname,date,url,unit,price,free,interest,account});
        if (interest == 0){
            Object money = (unit*price)+free;
            db.execSQL("UPDATE Account SET cash = cash-? WHERE id = ?",new Object[]{money,account});
        }else if (interest == 1 || interest == -1){
            Object money = (unit*price)-free;
            db.execSQL("UPDATE Account SET cash = cash+? WHERE id = ?",new Object[]{money,account});
        }
        adapter.notifyDataSetChanged();
    }
    private void updataAccount(String name,String remarks){
        db = myDBHelper.getWritableDatabase();
        db.execSQL("UPDATE Account SET name = ?, remarks = ? WHERE id = ?",new Object[]{name,remarks,Account_position});
        adapter.notifyDataSetChanged();
    }
    private void deleteAccountData(int pos){
        if (Account_number > 1){
            db.delete("Account","id = "+pos,null);
            db.delete("Fund","A_id = "+pos,null);
            db.delete("Record","A_id = "+pos,null);
            Account_number -= 1;

            db = myDBHelper.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM Account;",null);
            c.moveToFirst();
            do{
                if (c.getInt(0) > pos){
                    int id = c.getInt(0);
                    db = myDBHelper.getWritableDatabase();
                    db.execSQL("UPDATE Account SET id = ? WHERE id = ?",new Object[]{id-1,id});
                    db.execSQL("UPDATE Record SET A_id = ? WHERE A_id = ?",new Object[]{id-1,id});
                    Log.e("deleteAccount",c.getString(0)+"/"+c.getString(1)+"/"+c.getString(2)+"/"+c.getString(3));
                }
            }while (c.moveToNext());
            adapter.notifyDataSetChanged();
            c.close();
        }else{
            Toast.makeText(getActivity(), "手下留情，保留一本帳戶", Toast.LENGTH_SHORT).show();
        }
    }
    private String getAccountName(int i){
        String str;
        db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Account WHERE id = ?",new String[]{i+""});
        cursor.moveToFirst();
        if (cursor.getString(2).isEmpty()){
            str =  cursor.getString(1);
        }else{
            str = cursor.getString(1)+"("+cursor.getString(2)+")";
        }
        Log.e("getAccount",cursor.getString(0)+"/"+cursor.getString(1)+"/"+cursor.getString(2)+"/"+cursor.getString(3));
        cursor.close();
        return str;
    }
    private float[] getFundTotal(int i){
        db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Fund WHERE A_id = ?",new String[]{i+""});
        final float [] arr={0,0};
        if (cursor.getCount() == 0){
        }else{
            Cursor cursor1 = db.rawQuery("SELECT name, url, SUM(unit*price) sum, SUM(unit) s_unit FROM Fund WHERE A_id = ? AND interest = 0 GROUP BY name",new String[]{i+""});
            cursor1.moveToFirst();
            do{
                Log.e("Thread",cursor1.getString(0)+"/"+cursor1.getString(2)+"/"+cursor1.getString(3));
                final String url = cursor1.getString(1).replace("wr01","wr02");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Document doc = Jsoup.connect(url).timeout(0).get();
                            Elements tr = doc.select("tr");
                            f = Float.parseFloat(tr.get(9).select("td").get(1).text());
                            Log.e("Thread",f+"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Cursor cursor2 = db.rawQuery("SELECT unit, price, interest FROM Fund WHERE name = ?",new String[]{cursor1.getString(0)});
                cursor2.moveToFirst();
                ArrayList<ArrayList<Object>> arr0 = new ArrayList<>();
                float T_unit = 0;
                do{
                    ArrayList<Object> arr1 = new ArrayList<>();
                    if (cursor2.getInt(2) == 0){
                        arr1.add(cursor2.getFloat(0));
                        arr1.add(cursor2.getFloat(1));
                        T_unit = T_unit + cursor2.getFloat(0);
                        arr0.add(arr1);
                    }else{
                        T_unit = T_unit - cursor2.getFloat(0);
                    }
                }while (cursor2.moveToNext());
                arr[0] = arr[0]+(T_unit * f);

                float C_unit = T_unit; float sum = 0;
                for (int j = 0 ; j < arr0.size() ; j++){
                    if ((float)arr0.get(j).get(0) < C_unit){
                        sum = sum + ((float)arr0.get(j).get(0) * (float)arr0.get(j).get(1));
                        C_unit = C_unit - (float)arr0.get(j).get(0);
                    }else if ((float)arr0.get(j).get(0) == C_unit){
                        sum = sum + ((float)arr0.get(j).get(0) * (float)arr0.get(j).get(1));
                        C_unit = 0; break;
                    }else if ((float)arr0.get(j).get(0) > C_unit){
                        sum = sum + (C_unit * (float)arr0.get(j).get(1));
                        C_unit = 0; break;
                    }
                    Log.e("Test","Sum = "+sum+"/ Cunit ="+C_unit);
                }
                arr[1] = arr[1] + ((T_unit * f) - sum);

            }while (cursor1.moveToNext());
            cursor1.close();
        }
        cursor.close();
        return arr;
    }
    private int getCashTotal(int i){
        db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cash FROM Account WHERE id = ?",new String[]{i+""});
        cursor.moveToFirst();
        int firstcash = cursor.getInt(0);

        cursor =  db.rawQuery("SELECT * FROM Record INNER JOIN Account ON Record.A_id = Account.id AND Account.id = ? AND Record.money IS NOT NULL",new String[]{i+""});
        Log.e("getCashTotal","firstcash="+firstcash);
        if (cursor.getCount() == 0){
            cursor.close();
            return firstcash;
        }else{
            Log.e("GetCashTotal","not null");
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                do {
                    /*if (cursor.getInt(4) == 0){
                        firstcash = firstcash - cursor.getInt(2);
                    }else{
                        firstcash = firstcash + cursor.getInt(2);
                    }*/
                    Log.e("getCashTotal","record"+cursor.getString(0)+"/"+cursor.getString(1)+"/"+cursor.getString(2)+"/"+cursor.getString(3)+"/"+cursor.getString(4)+"/"+cursor.getString(5)+"/"+cursor.getString(6)+"/"+cursor.getString(7));
                }while (cursor.moveToNext());
            }
            cursor.close();
            return firstcash;
        }
    }
    private void addTransfer(String date,int money,int from,int to){
        db = myDBHelper.getWritableDatabase();
        db.execSQL("INSERT INTO Record(date,money,class,remarks,positive,A_id,F_id) VALUES (?,?,?,?,?,?,?);",new Object[]{date,money,6,"轉帳支出",0,from,""});
        db.execSQL("UPDATE Account SET cash = cash-? WHERE id = ?",new Object[]{money,from});
        db.execSQL("INSERT INTO Record(date,money,class,remarks,positive,A_id,F_id) VALUES (?,?,?,?,?,?,?);",new Object[]{date,money,5,"轉帳收入",1,to,""});
        db.execSQL("UPDATE Account SET cash = cash+? WHERE id = ?",new Object[]{money,to});
        adapter.notifyDataSetChanged();
    }
}
