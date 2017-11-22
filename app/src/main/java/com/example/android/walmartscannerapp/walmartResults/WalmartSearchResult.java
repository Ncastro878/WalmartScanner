package com.example.android.walmartscannerapp.walmartResults;

/**
 * Created by nick on 10/22/2017.
 */

public class WalmartSearchResult {
    private WalmartItem[] items;

    public void setItems(WalmartItem[] items){this.items = items;}

    public WalmartItem[] getItems(){return items;}

    @Override
    public String toString() {
        return items.length+" item(s) in the search result";
    }
}
