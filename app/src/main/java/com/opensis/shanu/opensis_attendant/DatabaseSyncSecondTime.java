package com.opensis.shanu.opensis_attendant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
import java.util.List;

/**
 * Created by Shanu on 3/1/2017.
 */

public class DatabaseSyncSecondTime extends AsyncTask{

    Context context;
    String email;
    String password;
    DatabaseHandler db;
    public DatabaseSyncSecondTime(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        db=new DatabaseHandler(context);
        List<AttendantBean> beanList=db.getAttendantData();
        for(AttendantBean a:beanList)
        {
            email=a.getEmail();
            password="admin";
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.1.101:8080/OpenSIS/databaseSynch.htm");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            InputStream inputStream = null;
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);

            inputStream = response.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

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
                    String path="http://192.168.1.101:8080/OpenSIS/UserPhoto/";
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
                    String path="http://192.168.1.101:8080/OpenSIS/studentsPhoto/";
                    Log.d("StudentData", "Name=" + name + " email=" + email + " beabonId=" + beaconId + "imageTitle=" + imageTitle);
                    getImage(imageTitle,path);
                    db.addStudent(new StudentBean(name, email, beaconId, imageTitle, imageTitle));
                }
            }
            db.close();
           /* Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();*/

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
