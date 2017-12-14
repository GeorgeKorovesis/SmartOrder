package com.example.george.smartorder;

/**
 * Created by George on 11/27/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SummaryListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public SummaryListAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.lblListItem);
        textView.setText(values.get(position).toString());
        //FrameLayout textView = (FrameLayout) rowView.findViewById(R.id.lblListItem);
        //textView. setText(values.get(position).toString());

        // Change icon based on name

        return rowView;
    }
}