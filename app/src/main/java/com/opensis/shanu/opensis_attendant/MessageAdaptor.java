package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.graphics.BitmapFactory;
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
    String[] image;
    Context c;
    LayoutInflater inflater;
    public MessageAdaptor(Context context,  String[] name, String[] message,String[] image,String[] date) {
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
        msgViewhlder.name= (TextView) convertView.findViewById(R.id.message_name);
        msgViewhlder.message= (TextView) convertView.findViewById(R.id.message);
        msgViewhlder.image= (ImageView) convertView.findViewById(R.id.message_image);
        msgViewhlder.date= (TextView) convertView.findViewById(R.id.date);

        msgViewhlder.name.setText(name[position]);
        msgViewhlder.message.setText(message[position]);
        msgViewhlder.image.setImageBitmap(BitmapFactory.decodeFile(image[position]));
        msgViewhlder.date.setText(date[position]);
        return convertView;
    }
}
