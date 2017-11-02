package com.example.android.walmartscannerapp;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import com.bumptech.glide.load.data.InputStreamRewinder;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class ViewProduct extends AppCompatActivity  {

    private String WALMART_API_KEY = "86y8cm3ujvuwnp2mncp76jbt";
    Gson gson;
    String upcCode;

    static ImageView mImageView;
    TextView mProductNameTextView;
    TextView mBarcodeNumberTextView;
    TextView mPriceTextView;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        mImageView = (ImageView) findViewById(R.id.thumbnail_image);
        mProductNameTextView = (TextView) findViewById(R.id.product_name);
        mBarcodeNumberTextView = (TextView) findViewById(R.id.barcode_number);
        mPriceTextView = (TextView)findViewById(R.id.price_text_view);

        gson = new Gson();
        upcCode = getIntent().getStringExtra("upcCode");
        mContext = getApplicationContext();

        // close the activity in case of empty barcode
        if (TextUtils.isEmpty(upcCode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            //Lets try not closing it yet, since leads to inventory list
            //finish();
        }
        mBarcodeNumberTextView.setText(upcCode + " is the barcode number.");

        new mAsyncTask().execute(upcCode);
    }

    public class mAsyncTask extends AsyncTask<String, Void, WalmartItem>{

        @Override
        protected WalmartItem doInBackground(String... barcodes) {
            Log.v("ViewProduct.Activity", "barcode in DoInBackground() is :" + barcodes[0]);
            String productStream = retrieveProductInfo(barcodes[0]);
            Log.v("ViewProductActivity", "product stream is: " + productStream);
            WalmartSearchResult result = gson.fromJson(productStream, WalmartSearchResult.class);
            WalmartItem item1 = result.getItems()[0];
            return item1;
        }

        @Override
        protected void onPostExecute(WalmartItem item) {
            super.onPostExecute(item);
            ViewProduct.this.mProductNameTextView.setText(item.getName());
            ViewProduct.setImage(item.getLargeImage());
            String price = item.getSalePrice();
            String fixedSalePrice = "$" + price.substring(0, price.length() - 2) + "." +
                    price.substring(price.length()-2);
            ViewProduct.this.mPriceTextView.setText(fixedSalePrice );


        }
    }

    private static void setImage(String thumbnailImage) {
        //ViewProduct.GlideApp.with(mContext).load(thumbnailImage).into(mImageView);
        Picasso.with(mContext).load(thumbnailImage).into(mImageView);

    }

    private String retrieveProductInfo(String barcode) {
        InputStream stream = null;
        String result = null;
        try{
            String url = buildUrl(barcode);
            URL walmartUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) walmartUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int status = connection.getResponseCode();
            Log.v("ViewProduct.Activity", status + " is the response code");
            if(status == 503 || status == 504)
                result = "503/504 error. Please wait.";
            else if(status != HttpURLConnection.HTTP_OK && status != 503 && status != 504)
                throw new IOException("http code error: " + status);
            stream = connection.getInputStream();
            if(stream != null)
                result = readStream(stream);
            connection.disconnect();
        }catch (IOException e){
            Log.e("ViewProduct.Activity", e.toString());
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
        Log.v("ViewProduct.Activity", "The URL is: " + newUrl);
        return newUrl;
    }
}
