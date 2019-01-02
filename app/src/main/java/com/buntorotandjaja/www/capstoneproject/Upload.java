package com.buntorotandjaja.www.capstoneproject;

public class Upload {
    private String mUploadInfo;
    private String mTitle;
    private String mDescription;
    private String mSellerUId;
    private String mImageUri;
    private String mBuyerUId;
    private boolean mSold;
    private String mPrice;

    // empty constructor required
    public Upload() {}

    public Upload(String uploadInfo, String imageUri, String title,
                  String description, String sellerUId, String price) {
        if (uploadInfo.trim().equals("")) {
            uploadInfo = "Title required";
        }
        mUploadInfo = uploadInfo;
        mImageUri = imageUri;
        mTitle = title;
        mDescription = description;
        mSellerUId = sellerUId;
        mPrice = "$" + price;
        mSold = false;
        mBuyerUId = "";
    }
    // setter
    public void setUploadInfo (String uploadInfo) { mUploadInfo = uploadInfo; }
    public void setImageUri (String imageUri) { mImageUri = imageUri; }
    public void setTitle (String title) { mTitle = title; }
    public void setDescription (String description) { mDescription = description; }
    public void setSellerUId (String sellerUid) { mSellerUId = sellerUid; }
    public void setPrice (String price) { mPrice = price; }
    public void setSold (boolean sold) { mSold = sold; }
    public void setBuyerUId (String buyerUId) { mBuyerUId = buyerUId; }
    // getter
    public String getUploadInfo () { return mUploadInfo; }
    public String getImageUri () { return mImageUri; }
    public String getTitle () { return mTitle; }
    public String getDescription () { return mDescription; }
    public String getSellerUId () { return mSellerUId; }
    public String getPrice () { return mPrice; }
    public String getBuyerUId () { return mBuyerUId; }
    public boolean getSold () { return mSold; }
}
