package com.opensis.shanu.opensis_attendant;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity{

    DrawerLayout mDrawerLayout;
    Toolbar mToolBar;
    ActionBarDrawerToggle mToggle;
    NavigationView navigationView;
    Button loginButton;
    String[] name;
    String[] beaconID;
    ListView listView;
    StudentAdaptor studentAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler handler=new DatabaseHandler(this);
        boolean check=handler.isattendantExist();
        Log.d("check",check+"");
        if(check){
            setContentView(R.layout.activity_main);
            mDrawerLayout= (DrawerLayout) findViewById(R.id.activity_main);
            mToolBar= (Toolbar) findViewById(R.id.actionbar);
            mToolBar.setTitle("Dashboard");
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

                            break;
                        case R.id.calendar:
                            Intent calender=new Intent(MainActivity.this,CalendarActivity.class);
                            startActivity(calender);
                            finish();
                            break;
                        case R.id.notifications:
                            Intent notifications=new Intent(MainActivity.this,NotificationActivity.class);
                            startActivity(notifications);
                            finish();
                            break;
                        case R.id.message:
                            Intent message=new Intent(MainActivity.this,MessageActivity.class);
                            startActivity(message);
                            finish();
                            Log.d("message","activity calling.....");
                            break;
                        case R.id.profile:
                            Intent profile=new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(profile);
                            finish();
                            break;
                        case R.id.signout:

                            break;
                    }
                    return false;
                }
            });
            List<StudentBean> liststudent=handler.getStudentData();
            this.name=new String[liststudent.size()];
            this.beaconID=new String[liststudent.size()];
            int i=0;
            for(StudentBean a:liststudent)
            {
                this.name[i]=a.getName();
                this.beaconID[i]=a.getBeacon_id();
                    /*String log="Id="+a.getId()+"  name="+a.getName()+"  email"+a.getEmail()+"  BeaconId="+a.getBeacon_id();
                    Log.d("DATA",log);*/
                i++;
            }
            listView= (ListView) findViewById(R.id.studentList);
            studentAdaptor=new StudentAdaptor(MainActivity.this,this.name,this.beaconID);
            listView.setAdapter(studentAdaptor);
        }
        else {
            setContentView(R.layout.login_activity);
            mDrawerLayout= (DrawerLayout) findViewById(R.id.login_activity);
            mToolBar= (Toolbar) findViewById(R.id.actionbar);
            mToolBar.setTitle("Dashboard");
            setSupportActionBar(mToolBar);
            mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            loginButton= (Button) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseSynch synch=new DatabaseSynch();
                    synch.execute();
                }
            });
        }
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



    public class DatabaseSynch extends AsyncTask
    {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        StringBuilder stringBuilder;
        EditText loginEmail;
        EditText loginPassword;
        String loginemail;
        String loginpassword;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginEmail= (EditText) findViewById(R.id.login_email);
            loginPassword= (EditText) findViewById(R.id.login_password);
            loginemail=loginEmail.getText().toString();
            loginpassword=loginPassword.getText().toString();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.1.101:8080/OpenSIS/databaseSynch.htm");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email", loginemail));
            nameValuePairs.add(new BasicNameValuePair("password", loginpassword));
            InputStream inputStream= null;
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);

            inputStream = response.getEntity().getContent();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            stringBuilder=new StringBuilder();
            String test;

            while((test=bufferedReader.readLine())!= null) {
                Log.d("message", "response Data=" + test);
                JSONObject jsonObject=new JSONObject(test);
                JSONArray jsonArray=jsonObject.getJSONArray("students");
                JSONArray jsonArray1 = jsonObject.getJSONArray("attendant");

                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject c = jsonArray1.getJSONObject(i);
                    Integer attendantId=Integer.parseInt(c.getString("staffId"));
                    String name = c.getString("first_name");
                    String phone = c.getString("phone");
                    String email = c.getString("email");
                    String beaconId = c.getString("beacon_id");
                    Log.d("AttendantData", "" + "ID="+attendantId+" name="+name+" phone="+phone+" email="+email+" beaconId="+beaconId);
                    db.addAttendant(new AttendantBean(attendantId,name,phone,email,beaconId));
                }
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    String name = c.getString("firstName");
                    String password = c.getString("password");
                    String email = c.getString("emailId");
                    String beaconId=c.getString("studentUUID");
                    db.addStudent(new StudentBean(name,email,beaconId,"8923987498347837389"));
                }
            }

                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);


        }
    }



}
