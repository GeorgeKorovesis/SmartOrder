package com.example.george.smartorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends Activity  {

    /*Initializations*/
    /***************************************************************************************************/
    /*used in startActivityForResult(nOrder, CREATE_ORDER)*/
    private static final int CREATE_ORDER = 1;
    /*used in startActivityForResult(Orders, PENDING_ORDERS)*/
    private static final int PENDING_ORDERS = 2;

    private static final int MAX_NUM_TABLES = 100;

    /*used in Shared Preferences*/
    public static final String PREFS_NAME_VERSION = "ProductsFileVersion";
    public static final String PREFS_NAME_PRODUCTS = "Products";
    public static final String PREFS_SUMMARY = "Summary";
    public static final String PREFS_INITIAL_DATA_WRITTEN = "InitialData";
    /*Button nOrderButton*/
    private static Button nOrderButton;

    /*Button setTokenButton*/
    private static Button setTokenButton;
      /*Button settingsButton*/
    private static Button settingsButton;

    boolean initialDataExist;

    SharedPreferences initialDataWritten ;
    SharedPreferences.Editor editor1;
    SharedPreferences sharedPreferences;
    /*Arraylisg containing objects of class ProdItem */
    ArrayList<ProdItem> productList ;

    SQLiteDatabase SmartOrderDB;
    String[] refreshments = {"Coca Cola", "Tea"};
    String[] coffees = {"Espresso", "Capuccino", "Freddo Espresso", "Freddo Capuccino", "Frappe", "Nescafe"};
    String[] food = {"Club Sandwich", "Tost"};
    /***************************************************************************************************/

    /*onCreate Method*/
    /***************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*set nOrderButton*/
        nOrderButton = (Button) findViewById(R.id.NewOrderBtn);

        /*set setTokenButton*/
        setTokenButton = (Button) findViewById(R.id.setTokenID);

          /*set settings Button*/
        settingsButton = (Button) findViewById(R.id.Settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().putBoolean(MyGcmListenerService.Action_UpdateDB, false).apply();

        Thread thread = new Thread() {
            @Override
            public void run() {
                initialDataWritten = getSharedPreferences(PREFS_INITIAL_DATA_WRITTEN, 0);
                editor1 = initialDataWritten.edit();

                initialDataExist = initialDataWritten.getBoolean("initialData", false);
                    /*Initial Data will be written only once when application runs for first time*/
                if (!initialDataExist) {
                    Log.i("@@Check1", "@@Check1");
                    if (loadInitialData())
                        editor1.putBoolean("initialData", true);
                    editor1.apply();
                }
                //printInitialData();

        /*Drop table Orders if exists....Inconsistent data, since app may have been closed with data inside*/
                SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
                SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
                SmartOrderDB.close();
                Log.i("@Check1@","@Check1@");
                productList=createProductList();

        /*Use SharedPreferences -> summary ... Clear data at the beginning*/
        //        SharedPreferences summary = getSharedPreferences(PREFS_SUMMARY, 0);
        //        SharedPreferences.Editor editor2 = summary.edit();
        //        editor2.clear();
        //        editor2.apply();
            }
        };
        thread.start();



        /*Use SharedPreferences settings*/
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME_VERSION, 0);
        //float prod_file_v = settings.getFloat("prod_file_version", 0);

        /* if internet is not activated, inform user */
        if (!isOnline()) {
            /*make toast informing that Internet Connection is lost...*/
            Toast toast = Toast.makeText(getApplicationContext(), "Internet Connection is lost...", Toast.LENGTH_LONG);
            toast.show();
        }



/*This button is used to create a new order. New activity is created, with code "CREATE_ORDER"*/
        nOrderButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (productList != null)
                {
                    /*Ask with a dialog the number of table*/
                    tableDialogPicker();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "No products found. Restart application and turn on WiFi or Data to get updates", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        );

        /*This button is used to see pending orders. New activity is created, with code "PENDING_ORDERS"*/
        setTokenButton.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                Intent setToken = new Intent(getApplicationContext(), SetToken.class);
                                                startActivity(setToken);
                                            }
                                        }
        );

          /*This button is used to see pending orders. New activity is created, with code "PENDING_ORDERS"*/
        settingsButton.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                Intent settings = new Intent(getApplicationContext(), Settings.class);
                                                startActivity(settings);
                                            }
                                        }
        );

    }
    /*************************************************************************************************/

/*This method is used to extract Objects from Json Array and create array with OrderItem objects*/
    ArrayList<ProdItem> ExtractObjsFromJSONArray(JSONArray jsArray)
    {
        /*variables used in method*/
        JSONObject tempObj;
        ProdItem tempOrdObj;
        String Prod_name;
        String Prod_category;

        ArrayList<ProdItem> orderList = new ArrayList<>();

        for (int i=1;i<jsArray.length();i++)
        try {
            /*get pbjects from Json Array, create new ProdItem, and add it in orderList*/
             tempObj = jsArray.getJSONObject(i);

             Prod_name=tempObj.getString("ProductName");
             Prod_category=tempObj.getString("ProductCategory");

             tempOrdObj = new ProdItem(Prod_name,Prod_category);
             orderList.add(tempOrdObj);

        } catch (JSONException e) {
             e.printStackTrace();
            }

        return orderList;
    }

    /*This method is used to extract Version from JsonArray*/
    double ExtractVersionFromJSONArray(JSONArray jsArray)
    {
        /*Variables used*/
        JSONObject tempObj;
        double version = 0;

            try {
                /*version is always the first object in Json Array*/
                tempObj = jsArray.getJSONObject(0);
                version=tempObj.getDouble("Version");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        return version;
    }

    /*sets button nOrderButton clickable or unclickable, depending on downloading status of data*/
    //public void setClickable(boolean status)
    //{
        /*TODO*/
        /*if status == false -> button not clickable ... set new image*/
    //    if(!status)
    //        nOrderButton.setBackgroundResource(R.drawable.roundedbuttongray);
    //    else
    //        nOrderButton.setBackgroundResource(R.drawable.roundedbutton);
        /*if status == true -> button clickable again ... initial image*/
    //    nOrderButton.setClickable(status);
    //}

    /*This interface method is called from DownloaderTask when onBackground finishes downloading data*/
    //public void setProducts(JSONArray products)
    //{
        /*Variables*/
    //    double storedVersion;
    //    double version;
    //    productList = new ArrayList<>();

    //    SharedPreferences settings = getSharedPreferences(PREFS_NAME_VERSION, 0);
    //    storedVersion = (double)settings.getFloat("prod_file_version", 0);
    //    version = ExtractVersionFromJSONArray(products);

    //    if(version > storedVersion) {
    //        SharedPreferences.Editor editor = settings.edit();
    //        editor.putFloat("prod_file_version", (float) version);
    //        editor.apply();
    //        /*extract from Json Array and store in Shared Preferences*/
    //        productList = ExtractObjsFromJSONArray(products);
    //        storeProductsInShPref(productList);

    //        Toast toast = Toast.makeText(getApplicationContext(), "Updated to version "+ version, Toast.LENGTH_SHORT);
    //        toast.show();
    //    }
    //    else
    //    {
    //        Toast toast = Toast.makeText(getApplicationContext(), "Version "+storedVersion+" to be used", Toast.LENGTH_SHORT);
    //        toast.show();
            /*old version to be used. Read from Shared Preferences*/
    //        productList=readProductsInShPref();
    //    }
    //}


/*This method stores the data in SharedPreferences*/
    private void storeProductsInShPref (ArrayList<ProdItem> products)
    {
        ProdItem tempObj;

        SharedPreferences settings = getSharedPreferences(PREFS_NAME_PRODUCTS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("prod_length", products.size());

        for(int i=0;i<products.size();i++)
        {
            tempObj=products.get(i);
            editor.putString("ProductName" + "_" + i, tempObj.getProductName());
            editor.putString("ProductCategory" + "_" + i, tempObj.getProductCategory());
        }
        editor.apply();

    }
    /*This method gets the data from SharedPreferences*/
    private ArrayList<ProdItem> readProductsInShPref () {
        ProdItem tempObj;

        String Prod_name ;
        String Prod_category ;

        SharedPreferences settings = getSharedPreferences(PREFS_NAME_PRODUCTS, 0);
        int length = settings.getInt("prod_length", 0);
        ArrayList<ProdItem> prod_list = new ArrayList<>();

        /*Create prod_list from Shared Preferences settings*/
        for (int i = 0; i < length; i++) {
            Prod_name = settings.getString("ProductName" + "_" + i, null);
            Prod_category = settings.getString("ProductCategory" + "_" + i, null);
            tempObj = new ProdItem(Prod_name, Prod_category);
            prod_list.add(tempObj);
        }
        return prod_list;
    }

    /*This method checks if device is connected to Internet*/
    public boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /*Customized Dialog picker, to set number of table to be served*/
    public void tableDialogPicker()
    {
        /*here i stopped*/
        final NumberPicker aNumberPicker = new NumberPicker(MainActivity.this);
        aNumberPicker.setMaxValue(MAX_NUM_TABLES);
        aNumberPicker.setMinValue(1);

        /*Create Layout*/
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        RelativeLayout linearLayout = new RelativeLayout(MainActivity.this);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPickerParams);

        /*create new alert dialog for table number picker*/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(R.string.Table_Choice);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                /*Create new activity for the order*/
                                //createNewOrderTable(aNumberPicker.getValue());
                                Intent nOrder = new Intent(getApplicationContext(), CreateNewOrderList.class);
                                /*TODO1: do not write in bundle products and table number...they are written in database*/
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("ProductList", productList);
                                bundle.putInt("TableNumber", aNumberPicker.getValue());
                                bundle.putString("Activity", "MainActivity");
                                nOrder.putExtras(bundle);
                                startActivityForResult(nOrder, CREATE_ORDER);
                            }
                        })
                .setNegativeButton(R.string.Cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    boolean loadInitialData()
    {
        Cursor resultSet;
        SmartOrderDB = openOrCreateDatabase("SmartOrder",MODE_PRIVATE,null);
        SmartOrderDB.execSQL("DROP TABLE IF EXISTS Products");
        SmartOrderDB.execSQL("CREATE TABLE IF NOT EXISTS Products(pname VARCHAR, pcategory VARCHAR);");
        for (String data:refreshments)
            SmartOrderDB.execSQL("INSERT INTO Products VALUES('"+data+"','refreshments');");
        for (String data:coffees)
            SmartOrderDB.execSQL("INSERT INTO Products VALUES('"+data+"','coffees');");
        for (String data:food)
            SmartOrderDB.execSQL("INSERT INTO Products VALUES('"+data+"','food');");

        resultSet = SmartOrderDB.rawQuery("SELECT * FROM Products",null);
        //resultSet.moveToFirst();
        if(resultSet.getCount()==0)
            return false;

        resultSet.moveToFirst();
        String prod_name = resultSet.getString(resultSet.getColumnIndex("pname"));
        String categ = resultSet.getString(resultSet.getColumnIndex("pcategory"));
        System.out.println("Product Name="+prod_name+",Category="+categ);
        Log.i("Prod="+prod_name+",Categ="+categ,"dasd");
        return true;
    }


    boolean printInitialData() {
        Cursor resultSet;
        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);

        resultSet = SmartOrderDB.rawQuery("SELECT * FROM Products", null);
        //resultSet.moveToFirst();
        if (resultSet.getCount() == 0)
            return false;


        while (resultSet.moveToNext()) {
            String prod_name = resultSet.getString(resultSet.getColumnIndex("pname"));
            String categ = resultSet.getString(resultSet.getColumnIndex("pcategory"));
            System.out.println("Product Name=" + prod_name + ",Category=" + categ);
            Log.i("Prod=" + prod_name + ",Categ=" + categ, "dasd");
        }
        return true;
    }

    ArrayList<ProdItem> createProductList()
    {
        ArrayList pList = new ArrayList<ProdItem> ();
        Cursor resultSet;
        ProdItem tempObj;
        String pName,pCategory;
        SQLiteDatabase mProducts;

        mProducts = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        resultSet = mProducts.rawQuery("SELECT * FROM Products", null);
        //resultSet.moveToFirst();
        if (resultSet.getCount() == 0)
            return null;

        Log.i("@Check2@","@Check2@");

        while (resultSet.moveToNext()) {
            Log.i("@Check3@","@Check3@");
            pName = resultSet.getString(resultSet.getColumnIndex("pname"));
            pCategory = resultSet.getString(resultSet.getColumnIndex("pcategory"));
            Log.i("@Check4@","@Check4@");
            //System.out.println("Product Name=" + prod_name + ",Category=" + categ);
            //Log.i("Prod=" + prod_name + ",Categ=" + categ, "dasd");
            tempObj = new ProdItem(pName, pCategory);
            Log.i("@Check5@","@Check5@");
            pList.add(tempObj);
        }
        Log.i("@Check6@","@Check6@");
        return pList;
    }





    /*Needed?????????????????? NOOOOOO*/
    //private void createNewOrderTable(int tableNo)
    //{
    //    Cursor resultSet;
    //    SmartOrderDB = openOrCreateDatabase("SmartOrder",MODE_PRIVATE,null);
    //    SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
    //    SmartOrderDB.execSQL("CREATE TABLE IF NOT EXISTS Orders(table INTEGER, order VARCHAR);");
    //    SmartOrderDB.execSQL("INSERT INTO Products VALUES("+tableNo+",'');");
    //}

}