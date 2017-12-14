package com.example.george.smartorder;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class Settings extends Activity implements taskCompletionResult {

    ArrayList<String> productList, categoryList;
    SQLiteDatabase smartOrder;
    EditText nProduct, nCategory;
    ArrayAdapter<String> adp;
    sumListAdapter adapter;
    RecyclerView productListRecView;
    Spinner spinnerProdCateg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button addBtn;

        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        addBtn = (Button)findViewById(R.id.add_product);
        nProduct = (EditText)findViewById(R.id.new_product);
        nCategory = (EditText)findViewById(R.id.new_category);
        readProductsFromDB();


        productListRecView = (RecyclerView)findViewById(R.id.products_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productListRecView.setLayoutManager(linearLayoutManager);

        adapter = new sumListAdapter(this, R.layout.summary_list_item1, productList, smartOrder);
        productListRecView.setAdapter(adapter);

        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new MyItemTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(productListRecView);


        categoryList.add(0,getResources().getString(R.string.category_selection));
        spinnerProdCateg = (Spinner)findViewById(R.id.products_spinner);
        adp= new ArrayAdapter<String>(this,
                R.layout.spinner_item,categoryList);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProdCateg.setAdapter(adp);
        spinnerProdCateg.setSelection(0);
        spinnerProdCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (position!=0) {
                    String imc_met = spinnerProdCateg.getSelectedItem().toString();
                    nCategory.setText(imc_met);
                    //categoryList.remove(0);
                    spinnerProdCateg.setSelection(0);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToDB();

                categoryList.add(0,getResources().getString(R.string.category_selection));

                adp= new ArrayAdapter<String>(Settings.this,R.layout.spinner_item,categoryList);

                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProdCateg.setAdapter(adp);
                spinnerProdCateg.setSelection(0);
                spinnerProdCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        if (position!=0) {
                            String imc_met = spinnerProdCateg.getSelectedItem().toString();
                            nCategory.setText(imc_met);
                            //spinnerProdCateg.setSelection(position);
                            //categoryList.remove(0);
                            spinnerProdCateg.setSelection(0);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
                adapter.update(productList);
            }
        });
    }

    private void readProductsFromDB ()
    {

        Cursor resultSet;
        /*Create strings like Coca Cola | Refreshments*/
        //ArrayList<String> products = new ArrayList<>();
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();


        smartOrder = openOrCreateDatabase("SmartOrder",MODE_PRIVATE,null);
        resultSet = smartOrder.rawQuery("SELECT * FROM Products",null);

        while(resultSet.moveToNext()) {
            productList.add(productList.size(),resultSet.getString(resultSet.getColumnIndex("pname")));
            categoryList.add(categoryList.size(),resultSet.getString(resultSet.getColumnIndex("pcategory")));
        }

        /*remove duplicates from category list*/
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(categoryList);
        categoryList.clear();
        categoryList.addAll(hashSet);
    }

    private void addProductToDB()
    {
        String product, category;
        category = nCategory.getText().toString();
        product = nProduct.getText().toString();
        smartOrder.execSQL("CREATE TABLE IF NOT EXISTS Products(pname VARCHAR, pcategory VARCHAR);");
        smartOrder.execSQL("INSERT INTO Products VALUES ('"+product+"','"+category+"');");
        readProductsFromDB();
        //adp.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UpdateDB();
    }

    //@Override
    //public void onBackPressed()
    //{
    //    UpdateDB();
    //    super.onBackPressed();
    //}

    private void UpdateDB()
    {
        Cursor resultSet = smartOrder.rawQuery("SELECT * FROM Products",null);

        String temp;
        while(resultSet.moveToNext()) {
            temp=resultSet.getString(resultSet.getColumnIndex("pname"));
            if(!productList.contains(temp))
                smartOrder.rawQuery("DELETE FROM Products WHERE pname='"+temp+"'",null);
        }
    }

    public void updateProductsDB(String itemToBeRemoved) {

        Log.i("@@@@#@@@","@@@@#@@@@");
        readProductsFromDB();
        for(int i=0;i<categoryList.size();i++)
            Log.i(i+".->"+categoryList.get(i),"dsdsds");
        categoryList.add(0,getResources().getString(R.string.category_selection));

        //adp.notifyDataSetChanged();


        adp= new ArrayAdapter<String>(Settings.this,
                R.layout.spinner_item,categoryList);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProdCateg.setAdapter(adp);
        spinnerProdCateg.setSelection(0);
        spinnerProdCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (position!=0) {
                    String imc_met = spinnerProdCateg.getSelectedItem().toString();
                    nCategory.setText(imc_met);
                    spinnerProdCateg.setSelection(position);
                    categoryList.remove(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


    }
}