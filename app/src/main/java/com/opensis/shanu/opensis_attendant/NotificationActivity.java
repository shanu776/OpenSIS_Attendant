package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Shanu on 2/21/2017.
 */

public class NotificationActivity extends AppCompatActivity{
    public enum MonthSpel {
        JAN(1),FEB(2),MAR(3),APR(4),MAY(5),JUN(6),JUL(7),AUG(8),SEP(9),OCT(10),NEV(11),DEC(12);

        private final int value;

        private MonthSpel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolBar;
    NavigationView navigationView;
    NotificationAdaptor adaptor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.notification_activity);
        mToolBar= (Toolbar) findViewById(R.id.actionbar);
        mToolBar.setTitle("Notification");
        setSupportActionBar(mToolBar);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView= (NavigationView) findViewById(R.id.navigationbar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.d("message","item="+item.getItemId());
                switch (item.getItemId())
                {
                    case R.id.dashboard:
                        Intent main=new Intent(NotificationActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
                        break;
                    case R.id.calendar:
                        Intent calender=new Intent(NotificationActivity.this,CalendarActivity.class);
                        startActivity(calender);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent notifications=new Intent(NotificationActivity.this,NotificationActivity.class);
                        startActivity(notifications);
                        finish();
                        break;
                    case R.id.message:
                        Intent message=new Intent(NotificationActivity.this,MessageActivity.class);
                        startActivity(message);
                        finish();
                        /*Log.d("message","activity calling.....");*/
                        break;
                    case R.id.profile:
                        Intent profile=new Intent(NotificationActivity.this,ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.signout:

                        break;
                }
                return false;
            }
        });

        NotificationData notificationData=new NotificationData();
        notificationData.execute();


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


    class NotificationData extends AsyncTask<String,Void,Map<String,Object>>{

    StringBuilder stringBuilder;
        String[] message;
        String[]date;
        String[] month;
        ListView newList;
        @Override
        protected Map<String, Object> doInBackground(String... params) {
            try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.1.101:8080/OpenSIS/getBroadcastdata.htm");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("teacher", "teacher"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);

                InputStream inputStream=response.getEntity().getContent();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                stringBuilder=new StringBuilder();
                String test;

                while((test=bufferedReader.readLine())!= null)
                {
                    Log.d("message","response Data="+test);
                    JSONObject broadcast=new JSONObject(test);
                    JSONArray jsonArray=broadcast.getJSONArray("broadcast");
                        message=new String[jsonArray.length()];
                        date=new String[jsonArray.length()];
                        month=new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject c=jsonArray.getJSONObject(i);
                        String id = c.getString("id");
                        String title = c.getString("title");
                        String message = c.getString("message");
                        Boolean parents = c.getBoolean("parents");
                        Boolean attendant =c.getBoolean("attendant");
                        int date = c.getInt("date");
                        int month = c.getInt("month");
                        Log.d("message","response Data="+id+" "+title+" "+message+" "+date+" "+month);
                        this.date[i]=Integer.toString(date);
                        this.month[i]= String.valueOf(MonthSpel.values()[month]); /*Integer.toString(month);*/
                        this.message[i]=message;
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
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            adaptor = new NotificationAdaptor(NotificationActivity.this, this.date, this.month, this.message);
            newList= (ListView) findViewById(R.id.notificationList);
            newList.setAdapter(adaptor);
        }
    }

}
