package com.example.george.smartorder;

/**
 * Created by George on 8/6/2016.
 */
 import android.database.Cursor;
 import android.database.sqlite.SQLiteDatabase;
 import android.support.v7.widget.RecyclerView;
 import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by adammcneilly on 9/8/15.
 */
public class MyItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private sumListAdapter myAdapter;

    public MyItemTouchHelper(sumListAdapter mAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.myAdapter = mAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        myAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        myAdapter.remove(viewHolder.getAdapterPosition());
    }


}