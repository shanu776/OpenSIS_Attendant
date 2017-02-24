package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shanu on 2/23/2017.
 */

public class StudentAdaptor extends ArrayAdapter{

    String[] name;
    String[] beaconID;
    int[] image;
    LayoutInflater inflater;
    Context c;
    public StudentAdaptor(Context context, String[] name,String[] beaconID) {
        super(context, R.layout.dashboard_model,name);
        this.name=name;
        this.beaconID=beaconID;
      /*  this.image=image;*/
        this.c=context;
    }

    class DashboardViewHolder{
        ImageView image;
        TextView name;
        TextView beaconId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.dashboard_model,null);
        }
        DashboardViewHolder view=new DashboardViewHolder();
        view.name= (TextView) convertView.findViewById(R.id.childname);
        view.beaconId= (TextView) convertView.findViewById(R.id.beaconId);
        view.image= (ImageView) convertView.findViewById(R.id.studentImage);

        view.name.setText(name[position]);
        view.beaconId.setText(beaconID[position]);

        return convertView;
    }
}
