package net.yaiba.timiup.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.yaiba.timiup.utils.Custom;

import java.util.Date;


public class TimiUpDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "TimiUpDB.db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "goods";
    public final static String RECORD_ID = "id";
    public final static String GOOD_NAME = "good_name";//名称
    public final static String PRODUCT_DATE = "product_date";//生产日期
    public final static String END_DATE = "end_date";//保质期
    public final static String BUY_DATE = "buy_date";//购买日期
    public final static String STATUS = "status";//状态，0未使用1已使用
    public final static String REMARK = "remark";//备注

    public TimiUpDB(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //登录用
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + RECORD_ID + " INTEGER primary key autoincrement,"
                + GOOD_NAME + " NVARCHAR(100) NOT NULL, "
                + PRODUCT_DATE +" NVARCHAR(100) NULL, "
                + END_DATE +" NVARCHAR(100) NULL, "
                + BUY_DATE +" NVARCHAR(100) NULL, "
                + STATUS +" NVARCHAR(10) NOT NULL default '0', "
                + REMARK +" TEXT NULL );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);

        onCreate(db);
    }

    public Cursor getAll(String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
        return cursor;
    }

    public Cursor getLimit90(String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_NAME, null, null, null, null, null, orderBy,"0,90");
        return cursor;
    }

    public Cursor getDay30(String orderBy) {

        String start_date = Custom.getDateToString(Custom.getFrontDay(new Date(),30));
        String end_date = Custom.getDateToString(new Date());

        String sql_buy_date = "( "+BUY_DATE+">='" +start_date +"' and " + BUY_DATE + "<='" + end_date +"' )";
        Log.v("debug",sql_buy_date);
        String where = "";
        where = sql_buy_date;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,TABLE_NAME, null, where , null, null, null, orderBy, null);
        return cursor;
    }

    public Cursor getAllForBakup(String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
        return cursor;
    }


    public Cursor getRecordInfo(long rowId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[] {RECORD_ID, GOOD_NAME, PRODUCT_DATE, END_DATE, BUY_DATE, STATUS, REMARK}, RECORD_ID + "=" + rowId, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getStartUsageDay() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_NAME, new String[] {BUY_DATE}, null, null, null, null, BUY_DATE+" asc", "0,1");
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //
    public Cursor getForSearch(String good_name, String buy_date, String status) {
        String where = "";
        String sql_goodname ="";
        if (!good_name.isEmpty()){
            sql_goodname = "("+GOOD_NAME + " LIKE '%" + good_name + "%' or "  + REMARK + " LIKE '%" + good_name + "%' " + ")";
        }

        String createtime =  "";

        String orderby = "buy_date desc";

        String start_date = "";
        String end_date = "";

        if(buy_date.isEmpty()){
            buy_date = "全部";
        }

        switch(buy_date) {
            case "本周":
                start_date = Custom.getDateToString(Custom.getBeginDayOfWeek());
                end_date = Custom.getDateToString(Custom.getEndDayOfWeek());
                break;
            case "本月":
                start_date = Custom.getDateToString(Custom.getBeginDayOfMonth());
                end_date = Custom.getDateToString(Custom.getEndDayOfMonth());
                break;
            case "三个月内":
                start_date = Custom.getDateToString(Custom.getBeginDayOfThreeMonth());
                end_date = Custom.getDateToString(Custom.getEndDayOfMonth());
                break;
            case "六个月内":
                start_date = Custom.getDateToString(Custom.getBeginDayOfSixMonth());
                end_date = Custom.getDateToString(Custom.getEndDayOfMonth());
                break;
            case "本年":
                start_date = Custom.getDateToString(Custom.getBeginDayOfYear());
                end_date = Custom.getDateToString(Custom.getEndDayOfYear());
                break;
            case "三年内":
                start_date = Custom.getDateToString(Custom.getBeginDayOfThreeYear());
                end_date = Custom.getDateToString(Custom.getEndDayOfYear());
                break;
            case "全部":
                start_date = "1900-01-01";
                end_date = "9909-12-31";
                break;
            default: break;
        }

        String sql_buy_date = "( "+BUY_DATE+">='" +start_date +"' and " + BUY_DATE + "<='" + end_date +"' )";

        if(!good_name.isEmpty()){
            where = sql_goodname + " and "+sql_buy_date;
        } else{
            where = sql_buy_date;
        }



        if(!status.isEmpty()){
            String sql_status = "( "+STATUS+ " = '"+ status +"' )";
            where =  where +" and "+sql_status;
        }

        Log.v("debug",where);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[] {RECORD_ID, GOOD_NAME, PRODUCT_DATE, END_DATE,BUY_DATE, STATUS, REMARK},
                where , null, null, null, orderby, null);
        return cursor;
    }


    public Cursor getForSearchName(String good_name_or_remark) {
        String where = "";
        String sql_namemark ="";
        if (!good_name_or_remark.isEmpty()){
            where = "("+GOOD_NAME + " LIKE '%" + good_name_or_remark + "%' or "  + REMARK + " LIKE '%" + good_name_or_remark + "%' " + ")";
        } else {
            where = "1=1";
        }

        String orderby = "id asc";

        Log.v("debug",where);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[] {RECORD_ID, GOOD_NAME, PRODUCT_DATE, END_DATE,BUY_DATE, STATUS, REMARK},
                where , null, null, null, orderby, null);
        return cursor;
    }

    public Cursor getAllGoodName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(false,TABLE_NAME, new String[] {GOOD_NAME}, null, null, null, null, null ,null);
        return cursor;
    }


    public long insert(String good_name, String product_date, String end_date, String buy_date, String status, String remark){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(GOOD_NAME, good_name);
        cv.put(PRODUCT_DATE, product_date);
        cv.put(END_DATE, end_date);
        cv.put(BUY_DATE, buy_date);

        cv.put(STATUS, status);
        if(remark.equals(null)){
            remark = "";
        }
        cv.put(REMARK, remark);

        long row = db.insert(TABLE_NAME, null, cv);
        Log.v("v_insertDB",good_name +"/"+product_date+"/"+end_date+"/"+ buy_date+"/"+ status);
        return row;
    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = RECORD_ID + " = ?";
        String[] whereValue ={ Integer.toString(id) };
        db.delete(TABLE_NAME, where, whereValue);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        //set ai=0
        String where = "name = ?";
        String[] whereValue = { TABLE_NAME };
        ContentValues cv = new ContentValues();
        cv.put("seq", 0);
        db.update("sqlite_sequence", cv, where, whereValue);
    }

    public void update(int id, String good_name, String product_date, String end_date, String buy_date, String status, String remark){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = RECORD_ID + " = ?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put(GOOD_NAME, good_name);
        if(product_date.equals(null)){
            product_date = "";
        }

        cv.put(PRODUCT_DATE, product_date);
        cv.put(END_DATE, end_date);
        cv.put(BUY_DATE, buy_date);

        cv.put(STATUS, status);
        if(remark.equals(null)){
            remark = "";
        }
        cv.put(REMARK, remark);
        db.update(TABLE_NAME, cv, where, whereValue);
    }


}
