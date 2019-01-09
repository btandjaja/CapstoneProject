package com.buntorotandjaja.www.capstoneproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.buntorotandjaja.www.capstoneproject.ItemListActivity.SELLER_LISTING;

public class YourListingActivity extends AppCompatActivity implements ItemAdapter.ItemAdapterOnClickHandler{

    private static final int OPEN_NEW_ACTIVITY = 813;

    @BindView(R.id.rv_your_listing) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_your_listing) Toolbar mToolbar;

    private ArrayList<Upload> mItemList;
    private ItemAdapter mItemAdapter;
    private String mSellerUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_listing);
        ButterKnife.bind(this);
        setupToolbar();
        getListing();
        initializeVariable();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.chevron_left_white_24dp);
    }

    private void getListing() {
        mItemList = new ArrayList<>();
        mSellerUid = getIntent().getStringExtra(SELLER_LISTING);
        if (mSellerUid != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getValue(Upload.class).getSellerUId().equals(mSellerUid)) {
                            mItemList.add(postSnapshot.getValue(Upload.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    error(databaseError.getMessage());
                }
            });
            return;
        }
        // TODO no data to show
        noDataToShow();
    }

    private void initializeVariable() {
        mItemAdapter = new ItemAdapter(this);
        mItemAdapter.setItemList(this,mItemList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mItemAdapter);
    }

    private void noDataToShow() {
        Toast.makeText(this, "You don't have any listing", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void error(String dbErrorMsg) {
        Toast.makeText(this, dbErrorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void OnItemClickListener(Upload eachItem) {
        Intent intent = new Intent(this, EditListing.class);
        intent.putExtra(Upload.DISPLAY_ITEM_STRING, eachItem);
        startActivityForResult(intent, OPEN_NEW_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_NEW_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                mItemList.clear();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));

                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                            Upload tempUpload = postDataSnapshot.getValue(Upload.class);
                            if (tempUpload.getSellerUId().equals(mSellerUid)){
                                mItemList.add(tempUpload);
                            }
                        }
                        mItemAdapter.setItemList(YourListingActivity.this, mItemList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
