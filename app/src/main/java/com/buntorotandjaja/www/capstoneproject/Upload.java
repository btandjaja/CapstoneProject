package com.buntorotandjaja.www.capstoneproject;

public class Upload {
    private String mName;
    private String mImageUri;
    private String mTitle;
    private String mDescription;
    private String mSellerUId;
    private boolean mSold;
    private double mPrice;

    // empty constructor required
    public Upload() {}

    public Upload(String name, String imageUri, String title, String description, String sellerUId, double price) {
        if (name.trim().equals("")) {
            name = "Title required";
        }
        mName = name;
        mImageUri = imageUri;
        mTitle = title;
        mDescription = description;
        mSellerUId = sellerUId;
        mPrice = price;
        mSold = false;
    }

    public void setName (String name) { mName = name; }
    public void setImageUri (String imageUri) { mImageUri = imageUri; }
    public void setTitle (String title) { mTitle = title; }
    public void setDescription (String description) { mDescription = description; }
    public void setPrice (double price) { mPrice = price; }
    public void setSold (boolean sold) { mSold = sold; }
    public String getName () { return mName; }
    public String getImageUri () { return mImageUri; }
    public String getTitle () { return mTitle; }
    public String getDescription () { return mDescription; }
    public double getPrice () { return mPrice; }
    public boolean getSold () { return mSold; }
}
