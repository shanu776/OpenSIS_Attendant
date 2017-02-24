package com.opensis.shanu.opensis_attendant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanu on 2/22/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="OpenSIS";
    private static final String TABLE_Name = "Attendant";
    private static final String TABLE_NAME_TWO="students";

    //Collumn names
    private static final String KEY_ID="id";
    private static final String ATTENDANT_ID="attendant_id";
    private static final String ATTENDANT_NAME="name";
    private static final String ATTENDANT_EMAIL="email";
    private static final String ATTENDAENT_PHONE="phone";
    private static final String ATTENDANT_BEACON_ID="beacon_id";
    private static final String ATTENDANT_IMAGE="image";

    private static final String KEY_ID_TWO="id";
    private static final String STUDENT_NAME="name";
    private static final String STUDENT_BEACON_ID="beacon_id";
    private static final String STUDENT_EMAIL="email";
    private static final String STUDENT_IMAGE="image";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       /* context.deleteDatabase(DATABASE_NAME);*/
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
       /* db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TWO);*/
    String CREATE_ATTENDANT_TABLE="CREATE TABLE " + TABLE_Name + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"+ ATTENDANT_ID + " INTEGER," + ATTENDANT_NAME + " TEXT,"
            + ATTENDAENT_PHONE + " TEXT," + ATTENDANT_EMAIL + " TEXT,"+ ATTENDANT_BEACON_ID + " TEXT," + ATTENDANT_IMAGE + " BLOB " + ")";

    String CREATE_STUDENT_TABLE="CREATE TABLE " +TABLE_NAME_TWO+ "("+KEY_ID_TWO+ " INTEGER PRIMARY KEY," +STUDENT_NAME+ " TEXT,"
        +STUDENT_BEACON_ID+ " TEXT," +STUDENT_EMAIL+ " " +
            "TEXT UNIQUE," +STUDENT_IMAGE+ " BLOB " +")";

        db.execSQL(CREATE_ATTENDANT_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Name);
        // Create tables again
        onCreate(db);
    }

    public void addAttendant(AttendantBean attendantBean)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues value=new ContentValues();
        value.put(ATTENDANT_NAME,attendantBean.getName());
        value.put(ATTENDANT_ID,attendantBean.getAttendantId());
        value.put(ATTENDAENT_PHONE,attendantBean.getPhone());
        value.put(ATTENDANT_EMAIL,attendantBean.getEmail());
        value.put(ATTENDANT_BEACON_ID,attendantBean.getBeaconId());
        value.put(ATTENDANT_IMAGE,attendantBean.getImage());
        db.insert(TABLE_Name,null,value);
        db.close();
    }

    public void addStudent(StudentBean studentBean)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put(STUDENT_NAME,studentBean.getName());
        value.put(STUDENT_EMAIL,studentBean.getEmail());
        value.put(STUDENT_BEACON_ID,studentBean.getBeacon_id());
        value.put(STUDENT_IMAGE,studentBean.getImage());
        db.insert(TABLE_NAME_TWO,null,value);
        db.close();
    }

    public List<AttendantBean> getAttendantData() {
        List<AttendantBean> contactList = new ArrayList<AttendantBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AttendantBean attendantBean = new AttendantBean();
                attendantBean.setId(Integer.parseInt(cursor.getString(0)));
                attendantBean.setAttendantId(Integer.parseInt(cursor.getString(1)));
                attendantBean.setName(cursor.getString(2));
                attendantBean.setPhone(cursor.getString(3));
                attendantBean.setEmail(cursor.getString(4));
                // Adding contact to list
                contactList.add(attendantBean);
            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    public List<StudentBean> getStudentData(){
        List<StudentBean> studentList=new ArrayList<StudentBean>();
        String selectQuery="SELECT * FROM "+ TABLE_NAME_TWO;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                StudentBean studentBean=new StudentBean();
                studentBean.setId(Integer.parseInt(cursor.getString(0)));
                studentBean.setName(cursor.getString(1));
                studentBean.setEmail(cursor.getString(2));
                studentBean.setBeacon_id(cursor.getString(3));
                studentBean.setImage(cursor.getString(4));
                studentList.add(studentBean);
            }while (cursor.moveToNext());
        }
        return studentList;
    }

    public boolean doesTableExist() {
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_Name + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isattendantExist() {
        SQLiteDatabase db=getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_Name;
        Cursor cursor=db.rawQuery(query,null);
        Log.d("cursor",""+cursor);
        if(cursor.moveToNext())
            return true;
        else
            return false;
    }
}
