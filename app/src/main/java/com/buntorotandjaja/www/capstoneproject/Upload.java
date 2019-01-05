package com.buntorotandjaja.www.capstoneproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Upload implements Parcelable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Upload(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new Upload[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUploadInfo);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mSellerUId);
        dest.writeString(mImageUrl);
        dest.writeString(mBuyerUId);
        dest.writeString(mPrice);
        dest.writeByte((byte) (mSold ? 1: 0));
    }

    private String mUploadInfo;
    private String mTitle;
    private String mDescription;
    private String mSellerUId;
    private String mImageUrl;
    private String mBuyerUId;
    private boolean mSold;
    private String mPrice;

    // empty constructor required
    public Upload() {}

    public Upload(Parcel in) {
        mUploadInfo = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mSellerUId = in.readString();
        mImageUrl = in.readString();
        mBuyerUId = in.readString();
        mPrice = in.readString();
        mSold = in.readByte() != 0;
    }

    public Upload(String uploadInfo, String imageUrl, String title,
                  String description, String sellerUId, String price) {
        if (uploadInfo.trim().equals("")) {
            uploadInfo = "Title required";
        }
        mUploadInfo = uploadInfo;
        mImageUrl = imageUrl;
        mTitle = title;
        mDescription = description;
        mSellerUId = sellerUId;
        mPrice = price;
        mSold = false;
        mBuyerUId = "";
    }
    // setter
    public void setUploadInfo (String uploadInfo) { mUploadInfo = uploadInfo; }
    public void setImageUrl (String imageUrl) { mImageUrl = imageUrl; }
    public void setTitle (String title) { mTitle = title; }
    public void setDescription (String description) { mDescription = description; }
    public void setSellerUId (String sellerUid) { mSellerUId = sellerUid; }
    public void setPrice (String price) { mPrice = price; }
    public void setSold (boolean sold) { mSold = sold; }
    public void setBuyerUId (String buyerUId) { mBuyerUId = buyerUId; }
    // getter
    public String getUploadInfo () { return mUploadInfo; }
    public String getImageUrl () { return mImageUrl; }
    public String getTitle () { return mTitle; }
    public String getDescription () { return mDescription; }
    public String getSellerUId () { return mSellerUId; }
    public String getPrice () { return mPrice; }
    public String getBuyerUId () { return mBuyerUId; }
    public boolean getSold () { return mSold; }
}
