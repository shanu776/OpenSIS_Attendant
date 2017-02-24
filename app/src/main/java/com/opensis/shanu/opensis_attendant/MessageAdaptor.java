package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by Shanu on 2/22/2017.
 */

public class MessageAdaptor extends ArrayAdapter<String> {
    String[] name;
    String[] message;
    String[] date;
    int[] image;
    Context c;
    LayoutInflater inflater;
    public MessageAdaptor(Context context, String[] date, String[] name, String[] message, int[] image) {
        super(context, R.layout.message_model,name);
        this.name=name;
        this.message=message;
        this.date=date;
        this.image=image;
        this.c=context;
    }

    public class MessageViewholder{
        TextView name;
        TextView message;
        TextView date;
        ImageView image;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.message_model,null);
        }
        MessageViewholder msgViewhlder=new MessageViewholder();


        return super.getView(position, convertView, parent);
    }
}
