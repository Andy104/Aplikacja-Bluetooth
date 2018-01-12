package com.example.andy.robotbt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andy on 2018-01-11.
 */

public class CustomAdapter extends ArrayAdapter<DeviceName> implements View.OnClickListener{

    private static final String TAG = "CustomAdapter";

    private ArrayList<DeviceName> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
    }

    public CustomAdapter(ArrayList<DeviceName> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() no action");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceName dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.device_address);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAddress.setText(dataModel.getAddress());

        return result;
    }
}
