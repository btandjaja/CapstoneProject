package com.buntorotandjaja.www.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ItemDetailActivity extends AppCompatActivity {

    public static final String PREVIOUS_ACTIVITY = "previous_activity";
    public static final int ITEM_LIST_ACTIVITY = 1;
    public static final int YOUR_LISTING_ACTIVITY = 2;

    @BindView(R.id.toolbar_item_detail_acitivty) Toolbar mToolbar;
    @BindView(R.id.tv_title_place_holder) TextView mActivityTitle;
    @BindView(R.id.tv_item_title) TextView mTitle;
    @BindView(R.id.tv_item_description) TextView mDescription;
    @BindView(R.id.tv_price) TextView mPrice;
    @BindView(R.id.button_placeholder) Button mButtonHolder;
    @BindView(R.id.item_image_detail) ThreeTwoImageView mImageView;

    private DatabaseReference mDbRef;
    private Upload mLoadedItem;
    private String mBuyerUid;
    private Boolean mSold;
    private int mPreviousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        ButterKnife.bind(this);
        mDbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));
        getItemDetail();
        setupToolbar();
        fillInfo();
        mButtonHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviousActivity == ITEM_LIST_ACTIVITY) {
                    // TODO if it's from item listing, then you can buy if you are not seller
                    buyItem();
                } else if (mPreviousActivity == YOUR_LISTING_ACTIVITY) {
                    // TODO if it's from your listing, button is function as update only
                } else {
                    Toast.makeText(ItemDetailActivity.this, "entered from somewhere else, error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getItemDetail() {
        Bundle data = getIntent().getExtras();
        if (data == null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorToast();
                }
            }, 1000);
            finish();
        }
        mLoadedItem = data.getParcelable(Upload.DISPLAY_ITEM_STRING);
        mPreviousActivity = data.getInt(PREVIOUS_ACTIVITY);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.chevron_left_white_24dp);
    }

    private void fillInfo() {
        mBuyerUid = FirebaseAuth.getInstance().getUid();
        mTitle.setText(mLoadedItem.getTitle());
        mDescription.setText(mLoadedItem.getDescription());
        mPrice.setText(mLoadedItem.getPrice());
        mSold = mLoadedItem.getSold();
        Uri imageUri = Uri.parse(mLoadedItem.getImageUrl());
        Picasso.get().load(imageUri).fit().centerCrop().into(mImageView);
        String title = "";
        String button = "";
        switch (mPreviousActivity) {
            case ITEM_LIST_ACTIVITY:
                title = getString(R.string.title_item_detail_activity);
                button = getString(R.string.buy_button);
                break;
            case YOUR_LISTING_ACTIVITY:
                title = getString(R.string.title_activity_listing);
                button = getString(R.string.update_button);
                break;
            default:
                // TODO came from unknown previous activity
                errorToast();
                finish();
        }
        mActivityTitle.setText(title);
        mButtonHolder.setText(button);
    }

    private void buyItem() {
        // TODO buyer is the seller
        if (mBuyerUid.equals( mLoadedItem.getSellerUId() )) {
            Toast.makeText(this, "Seller cannot buy his own item", Toast.LENGTH_LONG).show();
        } else {
            mLoadedItem.setBuyerUId(mBuyerUid);
            mLoadedItem.setSold(true);
            mSold = mLoadedItem.getSold();
            // TODO update database
            mDbRef.child(mLoadedItem.getUploadInfo()).child("buyerUId").setValue(mBuyerUid);
            mDbRef.child(mLoadedItem.getUploadInfo()).child("sold").setValue(mSold);
            Toast.makeText(this, "Congratulation, purchase complete!", Toast.LENGTH_SHORT).show();
        }
    }

    private void errorToast() {
        Toast.makeText(ItemDetailActivity.this, "Fail to load, please try again.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(Upload.DISPLAY_ITEM_STRING, mLoadedItem);
    }
}
