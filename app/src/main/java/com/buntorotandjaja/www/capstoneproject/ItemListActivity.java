package com.buntorotandjaja.www.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.ItemAdapterOnClickHandler {

    public static final String SELLER_LISTING = "seller_listing";

    @BindView(R.id.tv_listing) TextView mTvListing;
    @BindView(R.id.tb_item_list) Toolbar mToolbar;
    @BindView(R.id.rv_item_list) RecyclerView mRecyclerView;
    @BindView(R.id.progressbar_holder_ItemListActivity) View mIndicatorHolder;

    private DatabaseReference mDbRef;
    private ArrayList<Upload> mItemList;
    private ItemAdapter mItemAdapter;
    private boolean mLogOut;
    private boolean mWhileLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        mLogOut = false;
        // TODO set toolbar
        setToolbar();
        // TODO instantiate item list
        initialize();
        // TODO create adapter
         createAdapter();
        // TODO prepare recyclerView
        setRecyclerView();
        // TODO get data from firebase
        createDbReference();
        extractData();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        mTvListing.setText(getString(R.string.app_name));
    }

    private void initialize() {
        mItemList = new ArrayList<>();
        mWhileLoading = false;
    }

    private void createAdapter() { mItemAdapter = new ItemAdapter(this); }

    private void setRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mItemAdapter);
    }

    private void createDbReference() {
        mDbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));
    }

    private void extractData() {
        showIndicator();
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mItemList.add(postSnapshot.getValue(Upload.class));
                }
                mItemAdapter.setItemList(ItemListActivity.this, mItemList);
                hideIndicator();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!mLogOut) errorReadingDb(databaseError);
                hideIndicator();
            }
        });

    }

    private void errorReadingDb(DatabaseError errorMsg) {
        Toast.makeText(this, errorMsg.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void hideIndicator() {
        mWhileLoading = false;
        mIndicatorHolder.setVisibility(View.INVISIBLE);
    }

    private void showIndicator() {
        mWhileLoading = true;
        mIndicatorHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mWhileLoading) {
            loadingMessage();
            return true;
        }

        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                mLogOut = true;
                onBackPressed();
                break;
            case R.id.menu_your_listing:
                // TODO call your_listing activity
                intent = new Intent(this, YourListingActivity.class);
                intent.putExtra(SELLER_LISTING, FirebaseAuth.getInstance().getUid());
                startActivity(intent);
                break;
            case R.id.menu_sell:
                // TODO call sell activity
                intent = new Intent(this, SellActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_logout:
                logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

//    private ArrayList<Upload> getSellerListing() {
//        ArrayList<Upload> currentList = new ArrayList<>();
//        for (Upload eachItem : mItemList) {
//            if (eachItem.getSellerUId().equals(FirebaseAuth.getInstance().getUid())) {
//                currentList.add(eachItem);
//            }
//        }
//        return currentList;
//    }

    private void loadingMessage() {
        Toast.makeText(this, "The page is loading, please wait", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mWhileLoading) {
            mLogOut = true;
            logout();
        } else {
            loadingMessage();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
    }

    private void logout() {
        try {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            signoutSuccessful();
        } catch (Exception e) {
            signoutFail(e.getMessage());
        }
    }

    @Override
    public void OnItemClickListener(Upload eachItem) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(Upload.DISPLAY_ITEM_STRING, eachItem);
        intent.putExtra(ItemDetailActivity.PREVIOUS_ACTIVITY, ItemDetailActivity.ITEM_LIST_ACTIVITY);
        startActivity(intent);
    }

    // TODO for rotating, currently disabled
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList(Upload.DISPLAY_ITEM_STRING, mItemList);
    }

    private void signoutSuccessful() {
        Toast.makeText(this, "You have signed out", Toast.LENGTH_SHORT).show();
    }

    private void signoutFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
