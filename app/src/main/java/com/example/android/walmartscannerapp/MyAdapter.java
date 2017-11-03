package com.example.android.walmartscannerapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.load;

/**
 * Created by nick on 11/2/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Cursor mCursor;
    Context mContext;

    public MyAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    //ViewHolder inner class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
            productPriceTextView = itemView.findViewById(R.id.product_price_text_view);
            productImageView = (ImageView) itemView.findViewById(R.id.product_img_view);
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        holder.productNameTextView
                .setText(mCursor.getString(mCursor.getColumnIndex(ProductListContract.ProductListEntry.PRODUCT_NAME)));
        holder.productPriceTextView
                .setText(mCursor.getString(mCursor.getColumnIndex(ProductListContract.ProductListEntry.PRODUCT_PRICE)));
        Picasso.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(ProductListContract.ProductListEntry.PRODUCT_SMALL_IMG)))
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        //TODO: maybe use walmartItemsList.size();
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
