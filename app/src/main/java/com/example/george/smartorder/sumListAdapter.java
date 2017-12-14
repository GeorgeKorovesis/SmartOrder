package com.example.george.smartorder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by George on 12/20/2015.
 */


public class sumListAdapter extends RecyclerView.Adapter<sumListAdapter.ViewHolder> {

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

    public sumListAdapter(Context context, int resource, ArrayList<String> items,SQLiteDatabase smartOrder)
    {
        //super(context, resource, items);
        myItems = items;
        myContext = context;
        myDatabase = smartOrder;
    }


/*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) myContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.i("Position=" + position, "Position");
            Log.i("SlideItem" + slideEffectItem, "SlideItem");

            convertView = infalInflater.inflate(R.layout.summary_list_item1, null);

            TextView tt1 = (TextView) convertView.findViewById(R.id.listText);
            //LinearLayout.MarginLayoutParams margins = (LinearLayout.MarginLayoutParams) tt1.getLayoutParams();
            //margins.leftMargin = oldlM;
            //tt1.setLayoutParams(margins);
            //ViewGroup.MarginLayoutParams margins=(ViewGroup.MarginLayoutParams)tt1.getLayoutParams();
            //margins.leftMargin=oldlM;
            //tt1.setLayoutParams(margins);                //lp.setMarginStart(oldlM);
            //convertView.setLayoutParams(lp);

            Log.i("prin to setTag", "Prin to setTag");
            Log.i("settag-position", "" + position);
            tt1.setTag(position);
            //Log.i("Position in tag is", "" + convertView.getTag());
            Log.i("prin to setTextView", "Prin to setTextview");

            //TextView tt1 = (TextView) convertView.findViewById(R.id.listText);
            Log.i("prin to setText", "Prin to setText");
            tt1.setText(myItems.get(position));
        }
            //else
            //{
            //LayoutInflater infalInflater = (LayoutInflater) myContext
            //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //TextView tt1 = (TextView) convertView.findViewById(R.id.listText);
            //ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) tt1.getLayoutParams();

//            if (position != slideEffectItem) {
//                Log.i("item1", "item1");
            //convertView = infalInflater.inflate(R.layout.summary_list_item1, null);


//                margins.leftMargin = oldlM;
//                tt1.setLayoutParams(margins);
//            } else {
//                Log.i("slideeffectitem=", "" + slideEffectItem);
//                Log.i("position", "" + position);
//                Log.i("item2", "item2");
            //convertView = infalInflater.inflate(R.layout.summary_list_item1, null);
            //ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) convertView.getLayoutParams();
            //lp.setMarginStart(lM);
            //convertView.setLayoutParams(lp);

//                margins.leftMargin = lM;
//                tt1.setLayoutParams(margins);


            //        Log.i("prin to setTag", "Prin to setTag");
            //          convertView.setTag(position);
            //Log.i("Position in tag is", "" + convertView.getTag());
//            Log.i("prin to setTextView", "Prin to setTextview");

            //TextView tt1 = (TextView) convertView.findViewById(R.id.listText);
            // Log.i("prin to setText", "Prin to setText");
            //  tt1.setText(myItems.get(position));
            //}

            return convertView;

        }

*/

    public void update(ArrayList<String> updatedProducts)
    {
        myItems = updatedProducts;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        Log.i("Adapter-Remove","");

        String itemToBeRmvd = myItems.get(position);
        myItems.remove(position);
        notifyItemRemoved(position);
        removeFromDB(itemToBeRmvd);
        if(myContext instanceof Settings) {
            Log.i("Adapter","tsekarw context");
            ((Settings) myContext).updateProductsDB(itemToBeRmvd);
            Log.i("Adapter","tsekarw context2");

        }
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(myItems, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    private void removeFromDB(String itemToBeRemoved)
    {
        myDatabase.execSQL("DELETE FROM Products WHERE pname ='"+itemToBeRemoved+"';");
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

}