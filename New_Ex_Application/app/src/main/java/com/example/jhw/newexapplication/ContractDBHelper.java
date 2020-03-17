package com.example.jhw.newexapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContractDBHelper extends SQLiteOpenHelper { // DB를 관리하고 ContractDB의 sql문을 실행

    public static final int DB_VERSION = 1 ;
    public static final String DBFILE_CONTACT = "user.db" ;

    public ContractDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) { // 생성된 DB가 없을 때 한번만 호출
        db.execSQL(ContractDB.SQL_CREATE_RES_TBL) ;
        db.execSQL(ContractDB.SQL_CREATE_USER_TBL) ;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ContractDB.SQL_DROP_RES_TBL) ;
        db.execSQL(ContractDB.SQL_DROP_USER_TBL) ;
        onCreate(db) ;
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
