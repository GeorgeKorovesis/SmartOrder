package com.example.george.smartorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by George on 11/23/2016.
 */


    public class ordersListAdapter extends RecyclerView.Adapter<ordersListAdapter.ViewHolder> {

        ArrayList<String> myItems;
        Context myContext;
        SQLiteDatabase myDatabase;


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.summary_list_item1, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindItem(myItems.get(position));
        }

        @Override
        public int getItemCount() {
            return myItems.size();
        }


        //public sumListAdapter(Context context, int textViewResourceId) {
        //    //super(context, textViewResourceId);
        //    myContext = context;
        //}

        public ordersListAdapter(Context context, int resource, ArrayList<String> items,SQLiteDatabase smartOrder)
        {
            //super(context, resource, items);
            myItems = items;
            myContext = context;
            myDatabase = smartOrder;

            for (String item:items)
                System.out.println("Adapter->"+item);
        }


        public void update(ArrayList<String> updatedProducts)
        {
            System.out.println("Update is called....");
            myItems = updatedProducts;
            notifyDataSetChanged();
        }

        public void remove(int position) {
            Log.i("Adapter-Remove","");
            for (String item:myItems)
                System.out.println("listItems="+item);
            String itemToBeRmvd = myItems.get(position);
            myItems.remove(position);
            notifyItemRemoved(position);
            System.out.println("Itemtoberemoved="+itemToBeRmvd);
            updateDB();
            if(myContext instanceof OrdersList) {
                Log.i("Adapter","tsekarw context");
                //((OrdersList) myContext).updateProductsDB(itemToBeRmvd);
                Log.i("Adapter","tsekarw context2");

            }
        }

        public void swap(int firstPosition, int secondPosition){
            Collections.swap(myItems, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);
        }

/*        private void removeFromDB(String itemToBeRemoved)
        {
            //TODO -> drop table and store products from myItems...
            System.out.println("remove from db->"+itemToBeRemoved);
            String tableNo = extractTableNo(itemToBeRemoved);
            String product = extractProduct(itemToBeRemoved);
            myDatabase.execSQL("DELETE FROM OrdersList WHERE tablenum ='"+tableNo+"' AND orders='"+product+"';");
            notifyDataSetChanged();
        }
*/
        private void updateDB()
        {
            String table;
            String product;
            myDatabase.execSQL("DROP TABLE IF EXISTS OrderList");
            myDatabase.execSQL("CREATE TABLE OrderList(tablenum VARCHAR, orders VARCHAR);");
        //TODO -> drop table and store products from myItems...
            for (String item:myItems) {
                table = extractTableNo(item);
                product = extractProduct(item);
                myDatabase.execSQL("INSERT INTO OrderList VALUES ('"+table+"','"+product+"');");

            }
                notifyDataSetChanged();
        }
        public String itemAt (int position)
        {
            return myItems.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public final TextView myTextView;

            public ViewHolder(View view){
                super(view);
                myTextView = (TextView) view.findViewById(R.id.listText);
            }

            public void bindItem(String item){
                this.myTextView.setText(item);
            }
        }


    private String extractTableNo(String itemToBeRemoved)
    {
        /*#Table 2 -> Freddo espresso, .....*/
        int index = itemToBeRemoved.indexOf("-");
        System.out.println("Item to be removed is"+ itemToBeRemoved);
        return itemToBeRemoved.substring(7,index-1);
    }

    private String extractProduct(String itemToBeRemoved)
    {
        /*#Table 2 -> Freddo espresso, .....*/
        int index = itemToBeRemoved.indexOf("-");
        return itemToBeRemoved.substring(index+3);
    }

    }

