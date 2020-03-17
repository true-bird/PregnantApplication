package com.example.jhw.newexapplication;

public class ContractDB { // DB 정보 및 sql문 클래스


    private ContractDB() {};
    // 예약 정보 테이블 및 속성
    public static final String TBL_CONTACT2 = "RES_INFO" ;
    public static final String COL_R_STATION = "R_STATION" ;
    public static final String COL_R_NSTATION = "R_NSTAION" ;
    public static final String COL_DRAWABLEID = "DRAWABLEID" ;
    public static final String COL_RES_DATE = "RES_DATE" ;
    public static final String COL_R_POSITION = "R_POSITION" ;
    public static final String COL_R_NUMBER = "R_NUMBER" ;
    public static final String COL_PREGID = "PREGID" ;
    public static final String COL_READERID = "READERID" ;

    // 유저 정보 테이블 및 속성
    public static final String TBL_CONTACT1 = "USER_INFO" ;
    public static final String COL_PREGNUM = "PREGNUM" ;
    public static final String COL_NAME = "NAME" ;
    public static final String COL_PREGDATE = "PREGDATE" ;

    // 예약 테이블 생성 sql문
    public static final String SQL_CREATE_RES_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT2 + " " +
            "(" +
            COL_R_STATION +        " TEXT" +   ", " +
            COL_R_NSTATION +      " TEXT"             +   ", " +
            COL_DRAWABLEID +     " INTEGER"             +   ", " +
            COL_RES_DATE +     " TEXT"             +   ", " +
            COL_R_POSITION +     " TEXT"             +   ", " +
            COL_R_NUMBER +     " TEXT"             +   ", " +
            COL_READERID +     " TEXT"             +   ", " +
            COL_PREGID +    " TEXT NOT NULL"          +
            ")" ;
    // 유저 테이블 생성 sql문
    public static final String SQL_CREATE_USER_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT1 + " " +
            "(" +
            COL_PREGNUM +        " TEXT NOT NULL" +   ", " +
            COL_NAME +      " TEXT"             +   ", " +
            COL_PREGDATE +     " TEXT" +
            ")" ;

    // 테이블 삭제 sql문
    public static final String SQL_DROP_RES_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT2;
    public static final String SQL_DROP_USER_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT1;

    // 테이블 조회 sql문
    public static final String SQL_SELECT_RES_TBL = "SELECT * FROM " + TBL_CONTACT2 ;
    public static final String SQL_SELECT_USER_TBL = "SELECT * FROM " + TBL_CONTACT1 ;

    // 데이터 삽입 sql문
    public static final String SQL_INSERT_RES_TBL = "INSERT OR REPLACE INTO " + TBL_CONTACT2 + " " +
            "(" + COL_R_STATION + ", " + COL_R_NSTATION + ", " + COL_DRAWABLEID + ", " + COL_RES_DATE + ", " + COL_R_POSITION
            + ", " + COL_R_NUMBER + ", " + COL_READERID + ", " + COL_PREGID + ") VALUES " ;
    public static final String SQL_INSERT_USER_TBL = "INSERT OR REPLACE INTO " + TBL_CONTACT1 + " " +
            "(" + COL_PREGNUM + ", " + COL_NAME + ", " + COL_PREGDATE + ") VALUES " ;


    // 데이터 삭제 sql문
    public static final String SQL_DELETE_RES_TBL = "DELETE FROM " + TBL_CONTACT2 ;
    public static final String SQL_DELETE_USER_TBL = "DELETE FROM " + TBL_CONTACT1 ;

}
