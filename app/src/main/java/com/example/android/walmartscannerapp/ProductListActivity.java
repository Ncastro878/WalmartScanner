package com.example.android.walmartscannerapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.walmartscannerapp.databaseClasss.ProductListContract;
import com.example.android.walmartscannerapp.databaseClasss.ProductListDbHelper;
import com.example.android.walmartscannerapp.walmartResults.WalmartItem;
import com.example.android.walmartscannerapp.walmartResults.WalmartSearchResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductListActivity extends AppCompatActivity  {

    private String WALMART_API_KEY = "86y8cm3ujvuwnp2mncp76jbt";
    Gson gson;
    String upcCode;

    static ImageView mImageView;
    TextView mProductNameTextView;
    TextView mBarcodeNumberTextView;
    TextView mPriceTextView;
    static Context mContext;

    /**
     * New RecyclerView/SQLite Variables
     */
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    static SQLiteDatabase mDatabase;
    Cursor mainCursor;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mImageView = (ImageView) findViewById(R.id.thumbnail_image);
        mProductNameTextView = (TextView) findViewById(R.id.product_name);
        mBarcodeNumberTextView = (TextView) findViewById(R.id.barcode_number);
        mPriceTextView = (TextView)findViewById(R.id.price_text_view);

        gson = new Gson();
        upcCode = getIntent().getStringExtra("upcCode");
        mContext = getApplicationContext();

        ProductListDbHelper dbHelper = new ProductListDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        mainCursor = getAllProductItems();
        mAdapter = new MyAdapter(this, mainCursor);
        mRecyclerView = (RecyclerView) findViewById(R.id.walmart_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // close the activity in case of empty barcode
        if (! TextUtils.isEmpty(upcCode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();

            mBarcodeNumberTextView.setText(upcCode + " is the barcode number.");
            new mAsyncTask().execute(upcCode);
        }

    }

    public static Cursor getAllProductItems() {
        return mDatabase.query(ProductListContract.ProductListEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    public class mAsyncTask extends AsyncTask<String, Void, WalmartItem>{

        @Override
        protected WalmartItem doInBackground(String... barcodes) {
            Log.v("ProductListActivity", "barcode in DoInBackground() is :" + barcodes[0]);
            String productStream = retrieveProductInfo(barcodes[0]);
            Log.v("ViewProductActivity", "product stream is: " + productStream);
            WalmartSearchResult result = gson.fromJson(productStream, WalmartSearchResult.class);
            WalmartItem item1 = result.getItems()[0];
            return item1;
        }

        @Override
        protected void onPostExecute(WalmartItem item) {
            super.onPostExecute(item);
            ProductListActivity.this.mProductNameTextView.setText(item.getName());
            ProductListActivity.setImage(item.getLargeImage());
            ProductListActivity.this.mPriceTextView.setText("$" + item.getSalePrice() );
            addItemDialog(item);
        }
    }

    private void addItemDialog(final WalmartItem item) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_view, null);

        TextView tv = dialogView.findViewById(R.id.dialog_product_name);
        TextView priceView = dialogView.findViewById(R.id.dialog_product_price);
        ImageView imgView = dialogView.findViewById(R.id.dialog_product_img_view);
        tv.setText(item.getName());
        priceView.setText(item.getSalePrice());
        Picasso.with(mContext).load(item.getLargeImage()).into(imgView);

        dialogBuilder.setTitle("Add to cart?");
        dialogBuilder.setView(dialogView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO- ADD TO CART
                Toast.makeText(ProductListActivity.this, "Item Added", Toast.LENGTH_SHORT).show();
                addItemToDb(item);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    private void addItemToDb(WalmartItem item) {
        ContentValues cv = new ContentValues();
        cv.put(ProductListContract.ProductListEntry.PRODUCT_NAME, item.getName());
        cv.put(ProductListContract.ProductListEntry.PRODUCT_PRICE, item.getSalePrice());
        cv.put(ProductListContract.ProductListEntry.PRODUCT_IMG, item.getLargeImage());
        cv.put(ProductListContract.ProductListEntry.PRODUCT_SMALL_IMG, item.getThumbnailImage());
        Long num = mDatabase.insert(ProductListContract.ProductListEntry.TABLE_NAME, null, cv);
        Log.v("ProductListActivity", "Book is inserted. Num value is: " + num);
        mainCursor = getAllProductItems();
        mAdapter.swapCursor(mainCursor);
    }

    private static void setImage(String thumbnailImage) {
        //ProductListActivity.GlideApp.with(mContext).load(thumbnailImage).into(mImageView);
        Picasso.with(mContext).load(thumbnailImage).into(mImageView);
    }

    private String retrieveProductInfo(String barcode) {
        InputStream stream;
        String result = null;
        try{
            String url = buildUrl(barcode);
            URL walmartUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) walmartUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();
            Log.v("ProductListActivity", status + " is the response code");
            if(status == 503 || status == 504)
                result = "503/504 error. Please wait.";
            else if(status != HttpURLConnection.HTTP_OK && status != 503 && status != 504)
                throw new IOException("http code error: " + status);
            stream = connection.getInputStream();
            if(stream != null)
                result = readStream(stream);
            connection.disconnect();
        }catch (IOException e){
            Log.e("ProductListActivity", e.toString());
        }
        return result;
    }

    private String readStream (InputStream stream) throws IOException {
        StringBuffer sb = new StringBuffer();
        String inputLine = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
        while ((inputLine = br.readLine()) != null){
            sb.append(inputLine);
        }
        return sb.toString();
    }

    private String buildUrl(String upcCode) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.walmartlabs.com")
                .appendPath("v1")
                .appendPath("items")
                .appendQueryParameter("apiKey", WALMART_API_KEY)
                .appendQueryParameter("upc", upcCode);
        String newUrl = builder.build().toString();
        Log.v("ProductListActivity", "The URL is: " + newUrl);
        return newUrl;
    }

    public static boolean removeProduct(long id){
        return mDatabase.delete(ProductListContract.ProductListEntry.TABLE_NAME,
                ProductListContract.ProductListEntry._ID + " = " + id, null) > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.main_activity_menu_item:
                startActivity(new Intent(ProductListActivity.this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
