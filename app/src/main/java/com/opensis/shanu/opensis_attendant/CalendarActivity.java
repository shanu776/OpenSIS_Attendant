package com.opensis.shanu.opensis_attendant;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Shanu on 2/21/2017.
 */

public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolBar;
    NavigationView navigationView;
    MaterialCalendarView materialCalendarView;
    SwipeRefreshLayout swipeRefreshLayout;
    private Drawable drawable;
    DatabaseHandler db;
    TextView title;
    TextView message;
    ImageView menuImage;
    TextView menuTitle;
    View headerView;
    String url;
    String menuImagePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getString(R.string.url);
        setContentView(R.layout.calendar_activity);
        DatabaseHandler handler=new DatabaseHandler(this);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.calendar_activity);
        mToolBar= (Toolbar) findViewById(R.id.actionbar);
        mToolBar.setTitle("Calendar");
        setSupportActionBar(mToolBar);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.whiteColor));
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.updateCalendarData);

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
                        Intent main=new Intent(CalendarActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
                        break;
                    case R.id.calendar:
                        Intent calender=new Intent(CalendarActivity.this,CalendarActivity.class);
                        startActivity(calender);
                        finish();
                        break;
                    case R.id.notifications:
                        Intent notifications=new Intent(CalendarActivity.this,NotificationActivity.class);
                        startActivity(notifications);
                        finish();
                        break;
                    case R.id.message:
                        Intent message=new Intent(CalendarActivity.this,MessageActivity.class);
                        startActivity(message);
                        finish();
                        /*Log.d("message","activity calling.....");*/
                        break;
                    case R.id.profile:
                        Intent profile=new Intent(CalendarActivity.this,ProfileActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.signout:

                        break;
                }
                return false;
            }
        });


        title= (TextView) findViewById(R.id.eventTitle);
        message= (TextView) findViewById(R.id.eventMessage);
        materialCalendarView= (MaterialCalendarView) findViewById(R.id.eventCalendarView);
        drawable = ContextCompat.getDrawable(this, R.drawable.red_circle);
        db=new DatabaseHandler(this);
        List<CalendarEvent> calenderEvents=db.getCalendarData();
        for(CalendarEvent c:calenderEvents)
        {
            try {
                Date date= (Date) dateFormater(c.getDate());
                Log.d("DateObject","date="+date+"c.get date="+c.getDate());
                final CalendarDay currentDay = CalendarDay.from(date);
                materialCalendarView.addDecorator(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        return day.equals(currentDay);
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        view.setBackgroundDrawable(drawable);
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        swipeRefreshLayout.setSoundEffectsEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        materialCalendarView.setOnDateChangedListener(this);
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        CalendarDay date1=widget.getSelectedDate();
        Date newdate=date1.getDate();
        SimpleDateFormat newformat=new SimpleDateFormat("yyyy/MM/dd");
        String newDate=newformat.format(newdate);
        /*Log.d("newDate",newDate+"");*/

        List<CalendarEvent> list= db.getCalendarMessage(newDate);
        if(!list.isEmpty()) {
            for (CalendarEvent ce : list) {/* Log.d("newDate","title="+ce.getTitle()+" message="+ce.getMessage());*/
                title.setText(ce.getTitle());
                message.setText(ce.getMessage());

            }
        }
        else {
            title.setText("Title");
            message.setText("Message");
        }

    }

    public Object dateFormater(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date objDate=formatter.parse(date);
        return objDate;
    }

    @Override
    public void onRefresh() {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                TestConnection test=new TestConnection();
                if(test.pingHost()) {
                    CalendarDataSynch caldatasynch = new CalendarDataSynch();
                    caldatasynch.execute();
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CalendarActivity.this,"Network Not Available",Toast.LENGTH_LONG).show();
                            if(swipeRefreshLayout.isRefreshing())
                            {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        });
        t.start();
    }

    class CalendarDataSynch extends AsyncTask{
        InputStream inputStream = null;
        DatabaseHandler db=new DatabaseHandler(CalendarActivity.this);
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(url+"calendarDataSynch.htm");

                HttpResponse response = httpClient.execute(httpPost);
                inputStream = response.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String test;
                while((test=bufferedReader.readLine())!=null){
                    Log.d("CalendarData",test);
                    JSONObject jsonObject = new JSONObject(test);
                    JSONArray jsonArray = jsonObject.getJSONArray("calendarData");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject c = jsonArray.getJSONObject(i);
                        Integer event_id=Integer.parseInt(c.getString("event_id"));
                        String title=c.getString("title");
                        String message=c.getString("message");
                        String date=c.getString("date");
                        Log.d("jsonData","id="+event_id+" title="+title+"  message="+message +"  date="+date);
                        db.addCalendarEvent(new CalendarEvent(event_id,title,message,date));
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
            super.onPostExecute(o);
            Intent i=new Intent(CalendarActivity.this,CalendarActivity.class);
            startActivity(i);
            finish();
        }
    }



}


