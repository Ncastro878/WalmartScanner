package com.example.android.walmartscannerapp;

import android.provider.BaseColumns;

/**
 * Created by nick on 11/2/2017.
 */

public class ProductListContract {

    public static final class ProductListEntry implements BaseColumns {
        public static final String TABLE_NAME = "productTabele";
        public static final String PRODUCT_NAME = "productName";
        public static final String PRODUCT_PRICE = "productPrice";
        public static final String PRODUCT_IMG = "productImg";
        public static final String PRODUCT_QUANTITY = "productQuantity";
    }
}
