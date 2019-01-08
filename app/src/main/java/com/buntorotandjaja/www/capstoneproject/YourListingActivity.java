package com.buntorotandjaja.www.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class YourListingActivity extends AppCompatActivity implements ItemAdapter.ItemAdapterOnClickHandler{

    @BindView(R.id.rv_your_listing) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_your_listing) Toolbar mToolbar;

    private ArrayList<Upload> mItemList;
    private ItemAdapter mItemAdapter;

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
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mItemList = data.getParcelableArrayList(ItemListActivity.SELLER_LISTING);
            return;
        }
        errorReadingData();
    }

    private void initializeVariable() {
        mItemAdapter = new ItemAdapter(this);
        mItemAdapter.setItemList(this,mItemList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mItemAdapter);
    }

    private void errorReadingData() {
        Toast.makeText(this, "Data corrupted.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void OnItemClickListener(Upload eachItem) {
        Intent intent = new Intent(this, EditListing.class);
        intent.putExtra(Upload.DISPLAY_ITEM_STRING, eachItem);
        startActivity(intent);
    }
}
