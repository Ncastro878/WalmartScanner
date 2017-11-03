package com.example.android.walmartscannerapp;

/**
 * Created by nick on 10/22/2017.
 */

public class WalmartItem {
    private String name;
    private String longDescription;
    private String upc;
    private attributes attributes;
    private String largeImage;
    private String thumbnailImage;
    private String salePrice;

    /**
     * The getters and setters essentially seem pointless.
     */
    public String getThumbnailImage() {return thumbnailImage;}

    public void setThumbnailImage(String thumbnailImage) {this.thumbnailImage = thumbnailImage;}


    public String getName(){return name;}

    public String getShortDescription(){return longDescription;}

    public String getUpc(){return upc;}

    public attributes getAttributes(){return attributes;}

    public String getLargeImage(){return largeImage;}

    public String getSalePrice(){return salePrice;}

    public void setName(String name){this.name = name;}

    public void setShortDescription(String description){this.longDescription = description;}

    public void setUpc(String newUpc){ this.upc = newUpc;}

    public void setAttributes(attributes atts){this.attributes = atts;}

    public void setLargeImage(String imageUrl){ largeImage = imageUrl;}

    @Override
    public String toString() {
        return "Product name is " + name + ".\nShort description is " + longDescription
                + ".\nUPC code is :" + upc;
    }

    public class attributes{
        public String actualColor;

        public String getActualColor(){
            return actualColor;}

        public void setActualColor(String color){
            this.actualColor = color;
        }

        @Override
        public String toString(){
            return "The actual color is: " + actualColor;
        }
    }

}
