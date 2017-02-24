package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Shanu on 2/21/2017.
 */


public class NotificationAdaptor extends ArrayAdapter<String> {

    String[] date={};
    String[] month={};
    String[] notification={};
    Context c;
    LayoutInflater inflater;
    public NotificationAdaptor(Context context, String[] date,String[] month,String[] notification) {
        super(context, R.layout.notification_model,date);
        this.c=context;
        this.date=date;
        this.notification=notification;
        this.month=month;
    }

    //class which hold view for each view
    public class ViewHolder{
        TextView date;
        TextView month;
        TextView notification;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.notification_model,null);
        }

        //create viewHilder's object

        final ViewHolder viewHolder=new ViewHolder();

        //resolve our views
        viewHolder.date= (TextView) convertView.findViewById(R.id.date);
        viewHolder.month= (TextView) convertView.findViewById(R.id.month);
        viewHolder.notification= (TextView) convertView.findViewById(R.id.notification);

        //assigning data

        viewHolder.date.setText(date[position]);
        viewHolder.month.setText(month[position]);
        viewHolder.notification.setText(notification[position]);

        return convertView;
    }
}
