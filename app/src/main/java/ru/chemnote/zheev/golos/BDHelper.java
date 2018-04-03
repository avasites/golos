package ru.chemnote.zheev.golos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zheev on 18.03.18.
 */

public class BDHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "AccountListDB";
    public static final String TABLE_NAME = "AccountListTable";

    public static final String TABLE_ID = "_id";
    public static final String TABLE_ACCOUNT = "account";

    public BDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+
                " ("+TABLE_ID+" integer primary key, "+TABLE_ACCOUNT+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_NAME);

        onCreate(db);
    }
}
