package com.opensis.shanu.opensis_attendant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout mDrawerLayout;
    Toolbar mToolBar;
    ActionBarDrawerToggle mToggle;
    NavigationView navigationView;
    Button loginButton;
    String[] name;
    String[] beaconID;
    String[] imagePath;
    ListView listView;
    ListView outOfRangeList;
    StudentAdaptor studentAdaptor;
    ImageView menuImage;
    TextView menuTitle;
    View headerView;
    Set<String> studentOutOfRange=new HashSet<>();
    BeaconManager beaconManager;
    List<StudentBean> liststudent;
    String[] outOfRangeChildName;
    String[] outOfRangeChildbeaconID;
    String[] outOfRangeChildImagePath;
    String url;
    boolean check;
    String menuImagePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getString(R.string.url);
        isStoragePermissionGranted();
        DatabaseHandler handler = new DatabaseHandler(this);
        check = handler.isattendantExist();
        Log.d("check", check + "");
        /*======================================================check attendent table is exist or not==========================================*/

        /*======================================================if yes then do to if boby if no then go to else body==========================*/

        /*=======================================================inside if body directly go to dash board======================================*/
        if (check) {
            setContentView(R.layout.activity_main);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
            mToolBar = (Toolbar) findViewById(R.id.actionbar);
            mToolBar.setTitle("Dashboard");
            setSupportActionBar(mToolBar);
            mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            navigationView = (NavigationView) findViewById(R.id.navigationbar);
            headerView=navigationView.inflateHeaderView(R.layout.menubar_header);
            menuTitle= (TextView) headerView.findViewById(R.id.menu_header_title);
            menuImage= (ImageView) headerView.findViewById(R.id.menu_header_image);
            List<AttendantBean> attendant=handler.getAttendantData();
            for (AttendantBean a:attendant){
                menuTitle.setText(a.getName());
                menuImage.setImageBitmap(BitmapFactory.decodeFile(menuImagePath+a.getEmail()+".jpg"));
            }
            navigationView.setNavigationItemSelectedListener(this);
        /*==========================================================beaconConfigurationHere=================================================*/

            /*beaconManager=BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().add(new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            beaconManager.bind(this);*/

        /*=========================================================StudentOutOfRange=========================================================*/

            liststudent = handler.getStudentData();
               /* Thread th1=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (!studentOutOfRange.isEmpty()) {
                                Log.d("message", "insideWhile");
                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
                                outOfRangeChildName = new String[studentOutOfRange.size()];
                                outOfRangeChildbeaconID = new String[studentOutOfRange.size()];
                                outOfRangeChildImagePath = new String[studentOutOfRange.size()];

                                int j = 0;
                                for (StudentBean child : liststudent) {
                                    if (studentOutOfRange.contains(child.getBeacon_id())) {
                                        outOfRangeChildName[j] = child.getName();
                                        outOfRangeChildbeaconID[j] = child.getBeacon_id();
                                        outOfRangeChildImagePath[j] = path + child.getImage();
                                        String log="Id="+child.getId()+" image="+child.getImage()+"  name="+child.getName()+"  email"+child.getEmail()+"  BeaconId="+child.getBeacon_id()+" imageTitle="+child.getImage_title();
                                        Log.d("DATA",log);
                                        j++;
                                    }

                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        outOfRangeList = (ListView) findViewById(R.id.outOfRangeStudentList);
                                        studentAdaptor = new StudentAdaptor(MainActivity.this, outOfRangeChildName, outOfRangeChildbeaconID, outOfRangeChildImagePath);
                                        outOfRangeList.setAdapter(studentAdaptor);
                                    }
                                });

                                try {
                                    sleep(2000);
                                    studentOutOfRange.clear();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {

                            }
                        }
                    }
                });
                th1.start();*/



        /*===============================show student info on dashboard from sqlite database================================================*/

            String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
            this.name = new String[liststudent.size()];
            this.beaconID = new String[liststudent.size()];
            this.imagePath= new String[liststudent.size()];
            int i = 0;

            for (StudentBean a : liststudent) {
                this.name[i] = a.getName();
                this.beaconID[i] = a.getBeacon_id();
                this.imagePath[i]=path+a.getImage();
                    String log="Id="+a.getId()+" image="+a.getImage()+"  name="+a.getName()+"  email"+a.getEmail()+"  BeaconId="+a.getBeacon_id()+" imageTitle="+a.getImage_title();
                    Log.d("DATA",log);
                i++;
            }

            listView = (ListView) findViewById(R.id.studentList);
            studentAdaptor = new StudentAdaptor(MainActivity.this, this.name, this.beaconID,this.imagePath);
            listView.setAdapter(studentAdaptor);
        }
        /*==========================else mean first time use this app send to the loginn page and apply database operations===================*/
        else {
            setContentView(R.layout.login_activity);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.login_activity);
            mToolBar = (Toolbar) findViewById(R.id.actionbar);
            mToolBar.setTitle("Dashboard");
            setSupportActionBar(mToolBar);
            mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            loginButton = (Button) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TestConnection t = new TestConnection();
                            boolean test1 = t.pingHost();
                            Log.d("Test", test1 + "");
                            if (test1) {
                                DatabaseSynch synch = new DatabaseSynch();
                                synch.execute();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                    t.start();

                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("message", "item=" + item.getItemId());
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for(Beacon beacon:collection)
                {
                    Log.d("message",beacon.getBluetoothAddress().toString()+"  "+beacon.getDistance());
                    if(beacon.getDistance()>2.0)
                    {
                        studentOutOfRange.add(beacon.getBluetoothAddress().toString());
                    }
                }
            }
        });
    }*/

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("message", "item=" + item.getItemId());
        switch (item.getItemId()) {
            case R.id.dashboard:
                Intent dashboard = new Intent(MainActivity.this, MainActivity.class);
                startActivity(dashboard);
                finish();
                break;
            case R.id.calendar:
                Intent calender = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(calender);
                finish();
                break;
            case R.id.notifications:
                Intent notifications = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(notifications);
                finish();
                break;
            case R.id.message:
                Intent message = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(message);
                finish();
                Log.d("message", "activity calling.....");
                break;
            case R.id.profile:
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profile);
                finish();
                break;
            case R.id.signout:
                Intent sync=new Intent(MainActivity.this,MainActivity.class);
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TestConnection test=new TestConnection();
                        if(test.pingHost()){
                            DatabaseSyncSecondTime sync=new DatabaseSyncSecondTime(MainActivity.this);
                            sync.execute();
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"Netowork not available",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                t.start();
                startActivity(sync);
                finish();
                break;
        }
        return false;
    }


    public class DatabaseSynch extends AsyncTask {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        StringBuilder stringBuilder;
        EditText loginEmail;
        EditText loginPassword;
        String loginemail;
        String loginpassword;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginEmail = (EditText) findViewById(R.id.login_email);
            loginPassword = (EditText) findViewById(R.id.login_password);
            loginemail = loginEmail.getText().toString();
            loginpassword = loginPassword.getText().toString();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url+"databaseSynch.htm");
                Log.d("messsage",loginemail+"  "+loginpassword);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email", loginemail));
                nameValuePairs.add(new BasicNameValuePair("password", loginpassword));
                InputStream inputStream = null;
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);

                inputStream = response.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringBuilder = new StringBuilder();
                String test;

                while ((test = bufferedReader.readLine()) != null) {
                    Log.d("message", "response Data=" + test);
                    JSONObject jsonObject = new JSONObject(test);
                    JSONArray jsonArray = jsonObject.getJSONArray("students");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("attendant");

                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject c = jsonArray1.getJSONObject(i);
                        Integer attendantId = Integer.parseInt(c.getString("staffId"));
                        String name = c.getString("first_name");
                        String phone = c.getString("phone");
                        String email = c.getString("email");
                        String beaconId = c.getString("beacon_id");
                        String photo_title=c.getString("user_photo");
                        String path=url+"UserPhoto/";
                        getImage(photo_title,path);
                        Log.d("AttendantData", "" + "ID=" + attendantId + " name=" + name + " phone=" + phone + " email=" + email + " beaconId=" + beaconId);
                        db.addAttendant(new AttendantBean(attendantId, name, phone, email, beaconId));
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String name = c.getString("firstName");
                        String password = c.getString("password");
                        String email = c.getString("emailId");
                        String beaconId = c.getString("studentUUID");
                        String imageTitle = c.getString("studentsPhoto");
                        String path=url+"studentsPhoto/";
                        Log.d("StudentData", "Name=" + name + " email=" + email + " beabonId=" + beaconId + "imageTitle=" + imageTitle);
                        getImage(imageTitle,path);
                        db.addStudent(new StudentBean(name, email, beaconId, imageTitle, imageTitle));
                    }
                }
                db.close();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        public void getImage(String name,String location){
            String path = location+name;
            int fileLingth;
            try {
                URL url=new URL(path);
                URLConnection connection=url.openConnection();
                connection.connect();
                fileLingth=connection.getContentLength();
                File newfolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                if(!newfolder.exists())
                {
                    newfolder.mkdir();
                }
                File inputFile= new File(newfolder,name);
                inputFile.createNewFile();
                InputStream stream=new BufferedInputStream(url.openStream(),8192);
                byte[] data=new byte[2048];
                int total=0;
                int count=0;
                OutputStream outputStream= new FileOutputStream(inputFile);
                while ((count=stream.read(data))!=-1)
                {
                    total+=count;
                    outputStream.write(data,0,count);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("message","Permission is granted");
                return true;
            } else {

                Log.v("message","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("message","Permission is granted");
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(check)
         beaconManager.unbind(this);*/
    }
}



