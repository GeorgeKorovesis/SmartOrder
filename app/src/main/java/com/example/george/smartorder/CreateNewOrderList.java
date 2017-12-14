package com.example.george.smartorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CreateNewOrderList extends Activity {

    private Context context = null;
    private ArrayList<ProdItem> productList;
    private HashMap<String, List<String>> listDataChild;
    private List<String> listDataHeader;
    private Integer tableNumber;
    private ArrayList<OrderItem> orderList;
    private ArrayList<String> summaryList;
    private ListView lv;

    private static final int SUBMIT_ORDER = 2;
    private static final int def_table = 1;


    private CheckBox[] drinkSugarQuantity;
    private CheckBox[] drinkSugarType;
    private CheckBox[] drinkSize;
    private CheckBox[] drinkMisc;
    private EditText commentsText;

    static Integer sugarQId[] = {R.id.Black, R.id.Medium, R.id.Sweet};
    static Integer sugarTId[] = {R.id.White_Sugar, R.id.Brown_Sugar, R.id.Other_Sugar_Type};
    static Integer sizeId[] = {R.id.Small_Size, R.id.Big_Size};
    static Integer miscId[] = {R.id.Honey, R.id.Milk, R.id.Chocolate, R.id.Cinnamon};

    static Integer sugarQString[] = {R.string.Black, R.string.Medium, R.string.Sweet};
    static Integer sugarTString[] = {R.string.White_Sugar, R.string.Brown_Sugar, R.string.Other_Sugar_Type};
    static Integer sizeString[] = {R.string.Small_size, R.string.Big_size};
    static Integer miscString[] = {R.string.Honey, R.string.Milk, R.string.Chocolate, R.string.Cinnamon};

    //private EditText tableView;
    private TextView tableView;
    //private Button btnSubmitOrder;
    private Button btnSummary;
    //ArrayAdapter summaryListAdapter;
    private EditText commentsView;

    SQLiteDatabase SmartOrderDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_order_list);

       // tableView = (EditText) findViewById(R.id.tableNumber);
        tableView = (TextView) findViewById(R.id.tableNumber);

        context = getApplicationContext();
        productList = new ArrayList<>();
        String temp_category;
        listDataHeader = new ArrayList<>();
        ExpandableListView expListView;

        orderList = new ArrayList<>();

       /*TODO2 : Get products and table from database SmartOrder DONE...*/
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        /*This is the correct code*/
        productList = readProductsDB();
        /**************************/
        /*Remove following line*/
        //productList = (ArrayList<ProdItem>) bundle.getSerializable("ProductList");
        /***********************/
        tableNumber = bundle.getInt("TableNumber");
        //tableNumber = def_table;
        tableView.setText("Τραπέζι " + tableNumber);

        /*create list with product categories*/

        for (int i = 0; i < productList.size(); i++) {
            temp_category = new String(productList.get(i).getProductCategory());

            if (!listDataHeader.contains(temp_category)) {
                listDataHeader.add(temp_category);
            }
        }

        listDataChild = CreateAdapterData(productList, listDataHeader);

        //btnSubmitOrder = (Button) findViewById(R.id.submitOrder);
        btnSummary = (Button) findViewById(R.id.orderSummary);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        expListView.setAdapter(new ExpandableListAdapter(context, listDataHeader, listDataChild));

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Group Clicked " + listDataHeader.get(groupPosition),
                //        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(),
                //        listDataHeader.get(groupPosition) + " Expanded",
                //        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(),
                //        listDataHeader.get(groupPosition) + " Collapsed",
                //        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                //Toast.makeText(
                //        getApplicationContext(),
                //        listDataHeader.get(groupPosition)
                //                + " : "
                //                + listDataChild.get(
                //                listDataHeader.get(groupPosition)).get(
                //                childPosition), Toast.LENGTH_SHORT)
                //        .show();

                alertFormProductDetails(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));

                return false;
            }
        });
/*
        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int duration = Toast.LENGTH_SHORT;
                String text = "";
                OrderItem tempProd;

                for (int i = 0; i < orderList.size(); i++) {
                    tempProd = orderList.get(i);
                    text = text + (i + 1) + "." + tempProd.getProductName() + ":" + tempProd.getSugarQuantity() + "," + tempProd.getSugarType() + "," + tempProd.getSize() + " and " + tempProd.getMisc();
                    text += "\n";
                }

                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        });
*/

        btnSummary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.i("SetOnClickListener1", "LALALLAAL");
                Intent nOrder = new Intent(getApplicationContext(), SubmitOrder.class);
                Log.i("SetOnClickListener2", "LALALLAAL");
                Bundle bundle = new Bundle();
                Log.i("SetOnClickListener3", "LALALLAAL");
                bundle.putSerializable("Summary", orderList);
                Log.i("SetOnClickListener4", "LALALLAAL");


                String temp = tableView.getText().toString();
                temp = temp.substring(8);
                tableNumber = Integer.parseInt(temp);
                Log.i("Table Number = " + tableNumber, "");
                
                 /* TODO3. Add orderlist to table orders */
                addOrderToTable(orderList, tableNumber);
                /****************************************/
                /*START DELETE*/
                bundle.putInt("Table", tableNumber);
                //Log.i("SetOnClickListener5", "LALALLAAL");
                nOrder.putExtras(bundle);
                //Log.i("SetOnClickListener6", "LALALLAAL");
                /*END DELETE*/
                startActivity(nOrder);
            }
        });


    }


    public HashMap<String, List<String>> CreateAdapterData(ArrayList<ProdItem> prodList, List<String> prodCategories) {
        HashMap<String, List<String>> listDataChild = new HashMap<>();
        List<String> tempList;
        String key;

        for (int i = 0; i < prodCategories.size(); i++) {
           /*add category string first, with value = 0*/

            key = new String(prodCategories.get(i));
            tempList = new ArrayList<String>();

            for (int j = 0; j < prodList.size(); j++) {
                if (prodList.get(j).getProductCategory().equals(key))
                    tempList.add(prodList.get(j).getProductName());
            }

            List<String> addlist = new ArrayList<>(tempList);

            listDataChild.put(key, addlist);
        }

        return listDataChild;
    }


    /*
     * Show AlertDialog with some form elements.
     */
    public void alertFormProductDetails(final String prod) {

    /*
     * Inflate the XML view. activity_main is in
     * res/layout/form_elements.xml
     */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.drinks_dialog_list_item,
                null, false);


        drinkSugarQuantity = new CheckBox[sugarQId.length];

        for (int i = 0; i < sugarQId.length; i++) {
            drinkSugarQuantity[i] = (CheckBox) formElementsView
                    .findViewById(sugarQId[i]);
        }

        drinkSugarType = new CheckBox[sugarTId.length];
        for (int i = 0; i < sugarTId.length; i++) {
            drinkSugarType[i] = (CheckBox) formElementsView
                    .findViewById(sugarTId[i]);
        }

        drinkSize = new CheckBox[sizeId.length];
        for (int i = 0; i < sizeId.length; i++) {
            drinkSize[i] = (CheckBox) formElementsView
                    .findViewById(sizeId[i]);
        }

        drinkMisc = new CheckBox[miscId.length];
        for (int i = 0; i < miscId.length; i++) {
            drinkMisc[i] = (CheckBox) formElementsView
                    .findViewById(miscId[i]);
        }

        commentsText = (EditText) formElementsView.findViewById(R.id.Comments);

        // the alert dialog
        new AlertDialog.Builder(CreateNewOrderList.this).setView(formElementsView)
                .setTitle(R.string.Options)
                .setNegativeButton(R.string.Cancel, null)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {

                        String toastString = "";
                        OrderItem nItemOrdered = new OrderItem();

                    /*
                     * Detecting whether the checkbox is checked or not.
                     */
                        nItemOrdered = findCheckedItems(drinkSugarQuantity, drinkSugarType, drinkMisc, drinkSize, sugarQString, sugarTString, sizeString, miscString, commentsText);
                        nItemOrdered.setProductName(prod);
                        String temp = tableView.getText().toString();
                        temp = temp.substring(8);
                        tableNumber = Integer.parseInt(temp);
                        Log.i("Table Number = " + tableNumber, "");
                        nItemOrdered.setTable(tableNumber);
                        int index = orderList.size();
                        orderList.add(index, nItemOrdered);

                        //updateOrderList(nItemOrdered);


                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(context, toastString, duration);
                        //toast.show();


                        dialog.cancel();
                    }

                })
                .show();

    }

    private OrderItem findCheckedItems(CheckBox[] SugarQuantity, CheckBox[] SugarType, CheckBox[] Misc, CheckBox[] Size, Integer[] sugarQString, Integer[] sugarTString, Integer[] sizeString, Integer[] miscString, EditText commentsET) {
        OrderItem itemOrdered = new OrderItem();

        /*Check Comments*/
        String writtenComments = commentsET.getText().toString();
        if (!TextUtils.isEmpty(writtenComments))
            itemOrdered.setComments(writtenComments);



       /*Check SugarQuantity*/

        for (int i = 0; i < SugarQuantity.length; i++) {
            if (SugarQuantity[i].isChecked())
                itemOrdered.setSugarQuantity(getString(sugarQString[i]));
        }


        for (int i = 0; i < Size.length; i++) {
            if (Size[i].isChecked())
                itemOrdered.setSize(getString(sizeString[i]));
        }

        for (int i = 0; i < Misc.length; i++) {
            if (Misc[i].isChecked())
                itemOrdered.setMisc(getString(miscString[i]));
        }


        for (int i = 0; i < SugarType.length; i++) {
            if (SugarType[i].isChecked())
                itemOrdered.setSugarType(getString(sugarTString[i]));

        }

        return itemOrdered;
    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        orderList = new ArrayList<>();
    }


    private ArrayList<ProdItem> readProductsDB() {
        ArrayList pList = new ArrayList<>();
        Cursor resultSet;
        ProdItem tempObj;
        String pName, pCategory;
        SQLiteDatabase mProducts;

        mProducts = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        resultSet = mProducts.rawQuery("SELECT * FROM Products", null);

        while (resultSet.moveToNext()) {
            pName = resultSet.getString(resultSet.getColumnIndex("pname"));
            pCategory = resultSet.getString(resultSet.getColumnIndex("pcategory"));
            tempObj = new ProdItem(pName, pCategory);
            pList.add(tempObj);
        }
        return pList;
    }

    public void addOrderToTable(ArrayList<OrderItem> orders, int tableNum) {
        Cursor resultSet;
        String stringOrder;


        SmartOrderDB = openOrCreateDatabase("SmartOrder", MODE_PRIVATE, null);
        //SmartOrderDB.execSQL("DROP TABLE IF EXISTS Orders");
        SmartOrderDB.execSQL("CREATE TABLE IF NOT EXISTS Orders(tablenum VARCHAR, orders VARCHAR);");
        for (OrderItem tempProd : orders) {
            stringOrder = createStringOrder(tempProd);
            String l = "INSERT INTO Orders VALUES('" + tableNum + "','" + stringOrder + "');";
            //Log.i("STRINGGGGGGGGG",""+l);
            SmartOrderDB.execSQL("INSERT INTO Orders VALUES('" + tableNum + "','" + stringOrder + "');");
        }
    }

    /*TODO4*/
    public String createStringOrder(OrderItem tempProd) {
        int sugarQuantSize = tempProd.getSugarQuantity().size();
        int sugarType = tempProd.getSugarType().size();
        int misc = tempProd.getMisc().size();
        int size = tempProd.getSize().size();


        String order = new String();
        order = "$ " + tempProd.getProductName() +" ";

        for (int j = 0; j < size; j++) {
            if(j==0)
                order = order + " | Μεγεθος: ";
            if (j == size - 1)
                order = order + tempProd.getSize().get(j);
            else
                order = order + tempProd.getSize().get(j) + " -> ";
        }
        for (int j = 0; j < sugarQuantSize; j++) {
            if(j==0)
                order = order + " | Ποσότητα Ζάχαρης: ";
            if (j == sugarQuantSize - 1 )
                order = order + tempProd.getSugarQuantity().get(j);
            else
                order = order + tempProd.getSugarQuantity().get(j) + " -> ";
        }
        for (int j = 0; j < sugarType; j++) {
            if(j==0)
                order = order + " | Τύπος Ζάχαρης: ";
            if (j == sugarType - 1)
                order = order + tempProd.getSugarType().get(j) + " "+getResources().getString(R.string.sugarWord);
            else
                order = order + tempProd.getSugarType().get(j) + " & ";
        }
        for (int j = 0; j < misc; j++) {

            if (j==0)
                order = order + " | Διάφορα: ";
            if (j == misc - 1)
                order = order + tempProd.getMisc().get(j);
            else
                order = order + tempProd.getMisc().get(j) + " & ";
        }


        System.out.println("@@@Comments Exist???: "+ tempProd.areThereAnyComments());

        if(tempProd.areThereAnyComments()) {

            order = order + " | Σχόλιο: ";
            order = order + tempProd.getComments();
            System.out.println("@@@@@Wrote: "+ tempProd.getComments());

        }

        return order;
    }

}