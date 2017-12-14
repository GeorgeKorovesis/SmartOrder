package com.example.george.smartorder;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by George on 11/24/2015.
 */
public class SubmitOrder extends Activity {

    Context context;
    ArrayList<OrderItem> orderItemList;
    ArrayList<String> OrderList;
    TextView table;
    Integer table_no;
    Button subOrdBtn;
    SQLiteDatabase SmartOrder;


    public static final String PREFS_SUMMARY = "Summary";

    sumListAdapter adapter;
    ListView lv;

    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    View removeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.submit_order);

        table = (TextView) findViewById(R.id.table);

        context = getApplicationContext();

        Intent intent = this.getIntent();

        Bundle bundle = intent.getExtras();

        removeView = null;

        //orderItemList = new ArrayList<>();
        //orderItemList = (ArrayList<OrderItem>) bundle.getSerializable("Summary");


        /*TODO5: read Products from Database*/
        OrderList=readProductsFromDB();
        /************************************/
        //readProductsInShPref();

        //table_no = bundle.getInt("Table");

        //OrderList = new ArrayList<>();
        //OrderList = CreateOrderList();

        table_no = bundle.getInt("Table");
        table.setText(getText(R.string.table) + " " + table_no + "\n");

        /******/
        // Setup RecyclerView
        RecyclerView suborderRecyclerView = (RecyclerView) findViewById(R.id.suborder_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        suborderRecyclerView.setLayoutManager(linearLayoutManager);

        // Setup Adapter
        adapter = new sumListAdapter(getBaseContext(), R.layout.summary_list_item1, OrderList,SmartOrder );
        suborderRecyclerView.setAdapter(adapter);

        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new MyItemTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(suborderRecyclerView);


        subOrdBtn = (Button) findViewById(R.id.submit_order);

        subOrdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //for (int j = 0; j < orderItemList.size(); j++) {
                    //    orderItemList.get(j).setTable(table_no);
                    //}
                    // CALL GetText method to make post method call
                    sendOrder();
                } catch (Exception ex) {
                    Log.i(" url exeption! ", "" + ex);
                }

                //SQLiteDatabase SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
                //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");

            }
        });

    }

    /*public void performSlideViewEffect(View v,float historicX,float historicY,float x,float y) {

        TextView tt1 = (TextView) v.findViewById(R.id.listText);

        LinearLayout.MarginLayoutParams lp = (LinearLayout.MarginLayoutParams) v.getLayoutParams();

        lp.setMarginStart(lp.leftMargin - Math.round(historicX - x));

        v.setLayoutParams(lp);

        v.invalidate();


    }*/





    public  void  sendOrder()  throws UnsupportedEncodingException {
        String order = new String("Test:Table1,Freddo espresso");
        //Toast.makeText(context, order, Toast.LENGTH_LONG).show();
        ArrayList<String> ordList = new ArrayList<>();
        ordList=readProductsFromDB();
        ordList.add(0,""+table_no);
        //OrderList=readProductsFromDB();
        //OrderList.add(0,""+table_no);
        new NetworkAsyncTask().execute(ordList);
        clearOrders();
        emptyOrdersDB();

    }


/*
    ArrayList<String> CreateOrderList() {
        ArrayList<String> summaryList = new ArrayList<>();
        String tempText = "";
        for (int i = 0; i < orderItemList.size(); i++) {
            tempText = (i + 1) + "." + orderItemList.get(i).getProductName();

            for (int j=0;j<orderItemList.get(i).getSize().size();j++) {
                tempText += "\n\tΜέγεθος: " + orderItemList.get(i).getSize().get(j);
                Log.i(orderItemList.get(i).getSize().get(j),"");
            }
            for (int j=0;j<orderItemList.get(i).getSugarQuantity().size();j++) {
                tempText += "\n\tΠοσότητα Ζάχαρης:" + orderItemList.get(i).getSugarQuantity().get(j);
                Log.i(orderItemList.get(i).getSugarQuantity().get(j),"");
            }
            for (int j=0;j<orderItemList.get(i).getSugarType().size();j++) {
                tempText += "\n\tΕίδος Ζάχαρης:" + orderItemList.get(i).getSugarType().get(j);
                Log.i(orderItemList.get(i).getSugarType().get(j),"");
            }
            for (int j=0;j<orderItemList.get(i).getMisc().size();j++) {
                tempText += "\n\tΕπιπρόσθετα:" + orderItemList.get(i).getMisc().get(j);
                Log.i(orderItemList.get(i).getMisc().get(j),"");
            }
            summaryList.add(tempText);
        }
        return summaryList;
    }
*/



    private void storeProductsInDB()
    {
        SQLiteDatabase SmartOrderDB;

            SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
            SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
            SmartOrderDB.execSQL("CREATE TABLE Orders(tablenum VARCHAR, orders VARCHAR);");
            for (String temp : OrderList) {
            SmartOrderDB.execSQL("INSERT INTO Orders VALUES('" + table_no + "','" + temp + "');");
        }
        SmartOrderDB.close();
    }

    private ArrayList<String> readProductsFromDB ()
    {
        ArrayList<String> orderList = new ArrayList<>();
        String table,order;

        Cursor resultSet;

        SmartOrder = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //Log.i("@@@prin diavasw Orders","@@@@");
        resultSet = SmartOrder.rawQuery("SELECT * FROM Orders", null);

        while (resultSet.moveToNext()) {
            //table = resultSet.getString(resultSet.getColumnIndex("tablenum"));
            order = resultSet.getString(resultSet.getColumnIndex("orders"));

            //order = "Τραπέζι " + table + ":" + order;
            orderList.add(order);
        }
        //SmartOrder.close();
        return orderList;
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
    //storeProductsInShPref();
        storeProductsInDB();
    }


    private void clearOrders()
    {
        OrderList.clear();
        adapter.update(OrderList);
    }
    private void emptyOrdersDB()
    {
        SQLiteDatabase SmartOrderDB;

        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        SmartOrderDB.execSQL("CREATE TABLE Orders(tablenum VARCHAR, orders VARCHAR);");
        SmartOrderDB.close();
    }

}