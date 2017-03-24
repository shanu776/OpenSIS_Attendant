package com.opensis.shanu.opensis_attendant;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanu on 2/20/2017.
 */

public class MessageActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolBar;
    NavigationView navigationView;
    View headerView;
    ImageView menuImage;
    TextView menuTitle;
    String menuImagePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
    String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getString(R.string.url);
        setContentView(R.layout.message_activity);
        DatabaseHandler handler=new DatabaseHandler(this);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.message_activity);
        mToolBar= (Toolbar) findViewById(R.id.actionbar);
        mToolBar.setTitle("Message");
        setSupportActionBar(mToolBar);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView= (NavigationView) findViewById(R.id.navigationbar);
        headerView=navigationView.inflateHeaderView(R.layout.menubar_header);
        menuTitle= (TextView) headerView.findViewById(R.id.menu_header_title);
        menuImage= (ImageView) headerView.findViewById(R.id.menu_header_image);
        List<AttendantBean> attendant=handler.getAttendantData();
        for (AttendantBean a:attendant){
            menuTitle.setText(a.getName());
            menuImage.setImageBitmap(BitmapFactory.decodeFile(menuImagePath+a.getEmail()+".jpg"));
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.d("message","item="+item.getItemId());
                switch (item.getItemId())
                {
                    case R.id.dashboard:
                        Intent main=new Intent(MessageActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
                        break;
                    case R.id.calendar:
                        Intent calender=new Intent(MessageActivity.this,CalendarActivity.class);
                        startActivity(calender);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent notifications=new Intent(MessageActivity.this,NotificationActivity.class);
                        startActivity(notifications);
                        finish();
                        break;
                    case R.id.message:
                        Intent message=new Intent(MessageActivity.this,MessageActivity.class);
                        startActivity(message);
                        finish();
                        /*Log.d("message","activity calling.....");*/
                        break;
                    case R.id.profile:
                        Intent profile=new Intent(MessageActivity.this,ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.signout:

                        break;
                }
                return false;
            }
        });

        MessageReceive msg=new MessageReceive();
        msg.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("message","item="+item.getItemId());
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MessageReceive extends AsyncTask{

        String id;
        String[] title;
        String[] message;
        String[] message_image;
        String[] date;
        MessageAdaptor adaptor;
        ListView newList;
        String imagePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url+"reciveMessageByAttendant.htm");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("attendant_id", "33"));
                InputStream inputStream = null;
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);

                inputStream = response.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String test;
                while ((test=bufferedReader.readLine())!=null)
                {
                    JSONObject jsonObject = new JSONObject(test);
                    JSONArray jsonArray = jsonObject.getJSONArray("messages");


                    title=new String[jsonArray.length()];
                    message=new String[jsonArray.length()];
                    message_image=new String[jsonArray.length()];
                    date=new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        String title1 = c.getString("title");
                        String message1 = c.getString("message");
                        String message_image1=c.getString("image_title");
                        String date1=c.getString("date");
                      /*  String path="http://117.254.170.154:8080/OpenSIS/UserPhoto/";*/
                        title[i]=title1;
                        message[i]=message1;
                        message_image[i]=imagePath+message_image1;
                        date[i]=date1;
                       /* db.addAttendant(new AttendantBean(attendantId, name, phone, email, beaconId));*/
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            adaptor = new MessageAdaptor(MessageActivity.this, this.title, this.message,this.message_image,this.date);
            newList= (ListView) findViewById(R.id.messageList);
            newList.setAdapter(adaptor);
        }
    }
}
