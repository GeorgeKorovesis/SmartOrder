package com.example.george.smartorder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by George on 5/8/2016.
 */
public class OrdersList extends Activity implements taskCompletionResult{


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    //private ProgressBar mRegistrationProgressBar;
    private TextView ordersListTextView, statusTextView;
   // private boolean isReceiverRegistered;
    private MyDBReceiver myDBReceiver;
    private MyStatusReceiver myStatusReceiver;
    private Boolean Status = false;
    SharedPreferences updateDB;
    Boolean updOrNotDB;
    SQLiteDatabase SmartOrderDB;
    ArrayList<String> ordersList;
    ordersListAdapter adapter;
    RecyclerView ordListRecView;
    TextView tokenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_list);
        Log.i("Arxi","arxi");
        System.out.println("OnCreate is called");
/*Probably not needed*/
//        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
//                }


//            }
//        };
        /*not needed*/
        //ordersListTextView = (TextView) findViewById(R.id.ordersList);
        statusTextView = (TextView) findViewById(R.id.status);
        statusTextView.setText("Offline");
        statusTextView.setTextColor(Color.RED);
        tokenTextView = (TextView) findViewById(R.id.token);

        // Setup RecyclerView
        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        SmartOrderDB.execSQL("CREATE TABLE IF NOT EXISTS OrderList(tablenum VARCHAR, orders VARCHAR);");

        if (!isEmptyDB())
        {
            createOrdersList();
        }
        else
        {
            ordersList = new ArrayList<>();
            ordersList.add(0,"Empty List");
            //SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
            //SmartOrderDB.execSQL("DROP TABLE IF EXISTS OrdersList");
            //SmartOrderDB.close();
            System.out.println("Empty DB meta to oncreate....");
        }
        ordListRecView = (RecyclerView) findViewById(R.id.ordListRecView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ordListRecView.setLayoutManager(linearLayoutManager);

        // Setup Adapter
        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        adapter = new ordersListAdapter(getBaseContext(), R.layout.summary_list_item1, ordersList,SmartOrderDB );

        ordListRecView.setAdapter(adapter);

        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new myItemTouchHelper1(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(ordListRecView);


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myDBReceiver = new MyDBReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyGcmListenerService.Action_UpdateDB);
        registerReceiver(myDBReceiver, intentFilter);

        myStatusReceiver = new MyStatusReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(RegistrationIntentService.Action_UpdateStatus);
        registerReceiver(myStatusReceiver, intentFilter);

        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!Status && checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyGcmListenerService.Action_UpdateDB);
        registerReceiver(myDBReceiver, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(RegistrationIntentService.Action_UpdateStatus);
        registerReceiver(myStatusReceiver, intentFilter);

        updateDB = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        updOrNotDB = updateDB.getBoolean(MyGcmListenerService.Action_UpdateDB, false);

        if(updOrNotDB)
            {
                updateOrdersList();
                updateDB.edit().putBoolean(MyGcmListenerService.Action_UpdateDB, false).apply();
            }

        }

    @Override
    protected void onStop() {
        unregisterReceiver(myDBReceiver);
        unregisterReceiver(myStatusReceiver);
        super.onStop();
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private class MyDBReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            int datapassed = arg1.getIntExtra("DATAPASSED", 0);

            Log.i("Datapassed","Datapassed");
            Toast.makeText(OrdersList.this,
                    "Triggered by Service!\n"
                            + "Data passed: " + String.valueOf(datapassed),
                    Toast.LENGTH_LONG).show();

            updateOrdersList();

        }

    }

    private class MyStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
//@@//
            Log.i("I got it","Got it!!!");
            Status = arg1.getBooleanExtra("status",false);
            String key = arg1.getStringExtra("key");

            if(Status) {
                Log.i("mpika gia up-sta","mpika");
                statusTextView.setText("Online");
                statusTextView.setTextColor(Color.GREEN);
                //int start = iid.indexOf("@");
                //String key = iid.substring(start+1,start+9);
                tokenTextView.setText("Authorization key:  " + key);
                tokenTextView.setHint("Each Client shall use this key to be able to send orders.");
            }
        }

    }

    private void createOrdersList()
    {
        //TODO: read and store products in db
        Cursor resultSet;
        String table, order;

        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        resultSet = SmartOrderDB.rawQuery("SELECT * FROM OrderList", null);
        ordersList = new ArrayList<>();
        if (resultSet!=null) {
            while (resultSet.moveToNext()) {
                table = resultSet.getString(resultSet.getColumnIndex("tablenum"));
                order = resultSet.getString(resultSet.getColumnIndex("orders"));
                //id = resultSet.getInt(resultSet.getColumnIndex("id"));
                ordersList.add(createOrderString(table, order));
                System.out.println("added in orderlist->"+table+":"+order);
            }
        }
    }



    private void updateOrdersList()
    {
        //TODO: read and store products in db
        Cursor resultSet;
        String table, order;
        //Integer id;

        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        resultSet = SmartOrderDB.rawQuery("SELECT * FROM OrderList", null);
        ordersList = new ArrayList<>();
        if (resultSet!=null) {
            while (resultSet.moveToNext()) {
                table = resultSet.getString(resultSet.getColumnIndex("tablenum"));
                order = resultSet.getString(resultSet.getColumnIndex("orders"));
                //id = resultSet.getInt(resultSet.getColumnIndex("id"));

                ordersList.add(createOrderString(table, order));
                System.out.println("added in orderlist->"+table+":"+order);
            }
            updateAdapter();
        }
    }

    private String createOrderString(String table, String order)
    {

        System.out.println("added #Table "+table+" -> "+order);
        System.out.println("Wrong???"+new String("#Table "+table+" -> "+order));

        return "#Table "+table+" -> "+order;
    }

    public void updateProductsDB(String itemToBeRemoved)
    {
        Cursor resultSet;
        String table, order;
        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        resultSet = SmartOrderDB.rawQuery("SELECT * FROM OrderList", null);
        ordersList = new ArrayList<>();
        if (resultSet.moveToNext()) {
            while (resultSet.moveToNext()) {
                table = resultSet.getString(resultSet.getColumnIndex("tablenum"));
                order = resultSet.getString(resultSet.getColumnIndex("orders"));
                System.out.println("Added->"+table+":"+order);
                ordersList.add(createOrderString(table, order));
            }
            updateAdapter();
        }
        resultSet.close();
    }

    public void updateAdapter()
    {
        // Setup Adapter
        System.out.println("Updating Adapter with"+ordersList.size()+" items");
        adapter.update(ordersList);
        //ordListRecView.setAdapter(adapter);

        // Setup ItemTouchHelper
        //ItemTouchHelper.Callback callback = new myItemTouchHelper1(adapter);
        //ItemTouchHelper helper = new ItemTouchHelper(callback);
        //helper.attachToRecyclerView(ordListRecView);
    }

    public boolean isEmptyDB()
    {
        Cursor resultSet;
        //SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        resultSet = SmartOrderDB.rawQuery("SELECT * FROM OrderList", null);
        if(resultSet==null)
            return true;
        else
            return false;
    }

}
