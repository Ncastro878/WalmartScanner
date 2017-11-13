package com.example.android.walmartscannerapp;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.load;

/**
 * Created by nick on 11/2/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    static Cursor mCursor;
    Context mContext;

    public MyAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    private static Cursor getCursor() {
        return mCursor;
    }

    //ViewHolder inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView;
        ImageView deleteImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
            productPriceTextView = itemView.findViewById(R.id.product_price_text_view);
            productImageView = (ImageView) itemView.findViewById(R.id.product_img_view);
            deleteImageView = (ImageView) itemView.findViewById(R.id.trash_can_img);
            deleteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == deleteImageView.getId()){
                Log.v("MyAdapter.java", "Delete item clicked: " + view.getId());
                Cursor cursor = MyAdapter.getCursor();
                int position = getAdapterPosition();
                if(cursor.moveToPosition(position)){
                    long id = cursor.getLong(cursor.getColumnIndex(ProductListContract.ProductListEntry._ID));
                    if(ProductListActivity.removeProduct(id)){
                        Toast.makeText(view.getContext(), "Deleted product id: " + id, Toast.LENGTH_SHORT).show();
                        swapCursor(ProductListActivity.getAllProductItems());
                    }
                }
            }
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
