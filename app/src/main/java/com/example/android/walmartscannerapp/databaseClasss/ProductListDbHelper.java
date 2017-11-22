package com.example.android.walmartscannerapp.databaseClasss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nick on 11/2/2017.
 */

public class ProductListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "productList.db";
    private static final int DATABASE_VERSION = 2;

    public ProductListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE_STATEMENT = "CREATE TABLE " +
                ProductListContract.ProductListEntry.TABLE_NAME + " (" +
                ProductListContract.ProductListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductListContract.ProductListEntry.PRODUCT_NAME + " TEXT," +
                ProductListContract.ProductListEntry.PRODUCT_PRICE + " TEXT," +
                ProductListContract.ProductListEntry.PRODUCT_IMG + " TEXT," +
                ProductListContract.ProductListEntry.PRODUCT_SMALL_IMG + " TEXT," +
                ProductListContract.ProductListEntry.PRODUCT_QUANTITY + " INTEGER);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductListContract.ProductListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
