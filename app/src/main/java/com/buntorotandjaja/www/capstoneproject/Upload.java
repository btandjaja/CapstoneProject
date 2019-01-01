package com.buntorotandjaja.www.capstoneproject;

public class Upload {
    private String mName;
    private String mImageUri;

    // empty constructor required
    public Upload() {}

    public Upload(String name, String imageUri) {
        if (name.trim().equals("")) {
            name = "Title required";
        }
        mName = name;
        mImageUri = imageUri;
    }

    public void setName (String name) { mName = name; }
    public void setImageUri (String imageUri) { mImageUri = imageUri; }
    public String getName () { return mName; }
    public String getImageUri () { return mImageUri; }
}
