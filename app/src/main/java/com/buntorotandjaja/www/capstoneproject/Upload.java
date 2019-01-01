package com.buntorotandjaja.www.capstoneproject;

public class Upload {
    private String mName;
    private String mImageUri;
    private String mTitle;
    private String mDescription;
    private double mPrice;

    // empty constructor required
    public Upload() {}

    public Upload(String name, String imageUri, String title, String description, double price) {
        if (name.trim().equals("")) {
            name = "Title required";
        }
        mName = name;
        mImageUri = imageUri;
        mTitle = title;
        mDescription = description;
        mPrice = price;
    }

    public void setName (String name) { mName = name; }
    public void setImageUri (String imageUri) { mImageUri = imageUri; }
    public void setTitle (String title) { mTitle = title; }
    public void setDescription (String description) { mDescription = description; }
    public void setPrice (double price) { mPrice = price; }
    public String getName () { return mName; }
    public String getImageUri () { return mImageUri; }
    public String getTitle () { return mTitle; }
    public String getDescription () { return mDescription; }
    public double getPrice () { return mPrice; }
}
