package info.android.technologies.indoreconnect.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Win-7 on 5/14/2016.
 */
public class Helper extends SQLiteOpenHelper {

    public static String DBName = "DataBase";
    public static int DBVERSION = 1;


    public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("Create table Notification (id text,msg text,date text)");
        db.execSQL("Create table Cart (id text,status text,bname text,building text,streer text,estb text,area text,city text" +
                ",pincode text,state text,contact_person_title text,contact_person_name text,designation text,landline1 text,landline2 text" +
                ",mobile1 text,mobile2 text,email text,website text,lat text,longi text,image text,rating text,distance float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
