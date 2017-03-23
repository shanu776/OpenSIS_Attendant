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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shanu on 2/21/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolBar;
    NavigationView navigationView;
    TextView userName;
    ImageView userImage;
    EditText updatePassword;
    EditText updateEmail;
    EditText updatePhone;
    EditText reEnterPassword;
    DatabaseHandler db;
    ImageView menuImage;
    TextView menuTitle;
    View headerView;
    String attendant_id;
    String email;
    String pass;
    String phone;
    String menuImagePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
    String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getString(R.string.url);
        setContentView(R.layout.profile);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.profile_activity);
        mToolBar= (Toolbar) findViewById(R.id.actionbar);
        mToolBar.setTitle("Profile");
        setSupportActionBar(mToolBar);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName= (TextView) findViewById(R.id.username);
        userImage= (ImageView) findViewById(R.id.userImage);
        updateEmail= (EditText) findViewById(R.id.updateEmail);
        updatePhone= (EditText) findViewById(R.id.updatePhoneNumber);
        updatePassword= (EditText) findViewById(R.id.updatePassword);
        reEnterPassword= (EditText) findViewById(R.id.reEnterPassword);

        navigationView= (NavigationView) findViewById(R.id.navigationbar);
        headerView=navigationView.inflateHeaderView(R.layout.menubar_header);
        menuTitle= (TextView) headerView.findViewById(R.id.menu_header_title);
        menuImage= (ImageView) headerView.findViewById(R.id.menu_header_image);
        db=new DatabaseHandler(this);
        List<AttendantBean> attendant=db.getAttendantData();
        for (AttendantBean a:attendant){
            attendant_id=Integer.toString(a.getAttendantId());
            userName.setText(a.getName());
            updateEmail.setText(a.getEmail());
            updatePhone.setText(a.getPhone());
            userImage.setImageBitmap(BitmapFactory.decodeFile(menuImagePath+a.getEmail()+".jpg"));
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
                        Intent main=new Intent(ProfileActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
                        break;
                    case R.id.calendar:
                        Intent calender=new Intent(ProfileActivity.this,CalendarActivity.class);
                        startActivity(calender);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent notifications=new Intent(ProfileActivity.this,NotificationActivity.class);
                        startActivity(notifications);
                        finish();
                        break;
                    case R.id.message:
                        Intent message=new Intent(ProfileActivity.this,MessageActivity.class);
                        startActivity(message);
                        finish();
                        /*Log.d("message","activity calling.....");*/
                        break;
                    case R.id.profile:
                        Intent profile=new Intent(ProfileActivity.this,ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.signout:

                        break;
                }
                return false;
            }
        });

        reEnterPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                boolean flag=true;
                email = updateEmail.getText().toString();

                if (!isValidEmail(email)) {
                    updateEmail.setError("Invalid Email");
                    flag=false;
                }

                pass = updatePassword.getText().toString();
                /*if (!isValidPassword(pass)) {
                    updatePassword.setError("Invalid Password");
                }*/
                final String rePass = reEnterPassword.getText().toString();
                if (!isRePasswordSame(pass,rePass)){
                    reEnterPassword.setError("Password Mismatch");
                    flag=false;
                }
                phone=updatePhone.getText().toString();

                Log.d("message","email="+updateEmail.getText()+" phone="+updatePhone.getText()+" password="+updatePassword.getText()+" Repassword="+reEnterPassword.getText());
               /*Log.d("validate","validemail"+emailValidate+"validpass"+repassValidate);*/
                if(flag) {
                    TestConnection test = new TestConnection();
                    if (test.pingHost()) {
                        UpdateProfile update = new UpdateProfile();
                        update.execute();
                    } else {
                        Toast.makeText(ProfileActivity.this, "network not available", Toast.LENGTH_LONG).show();
                    }
                }
                updateEmail.setText("");
                updatePassword.setText("");
                updatePhone.setText("");
                reEnterPassword.setText("");
                return false;
            }
        });

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


    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    private boolean isRePasswordSame(String pass,String rePass) {
        if (pass.equals(rePass)) {
            return true;
        }
        return false;
    }

    class UpdateProfile extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url+"updateAttendantProfile.htm");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", pass));
                nameValuePairs.add(new BasicNameValuePair("phone",phone ));
                nameValuePairs.add(new BasicNameValuePair("attendant_id",attendant_id));
                InputStream inputStream = null;
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);

                inputStream = response.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String test;
                while ((test=bufferedReader.readLine())!=null)
                {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(getApplicationContext(),"succesFully Update",Toast.LENGTH_LONG).show();
        }
    }
}
