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

    @BindView(R.id.tv_listing) TextView mTvListing;
    @BindView(R.id.tb_item_list) Toolbar mToolbar;
    @BindView(R.id.rv_item_list) RecyclerView mRecyclerView;

    private DatabaseReference mDbRef;
    private ArrayList<Upload> mItemList;
    private ItemAdapter mItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        // TODO set toolbar
        setToolbar();
        // TODO instantiate item list
        initializeItemList();
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

    private void createDbReference() { mDbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name)); }

    private void extractData() {
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItemList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mItemList.add(postSnapshot.getValue(Upload.class));
                }
                mItemAdapter.setItemList(ItemListActivity.this, mItemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorReadingDb(databaseError);
            }
        });
    }

    private void initializeItemList() { mItemList = new ArrayList<>(); }

    private void createAdapter() { mItemAdapter = new ItemAdapter(this); }

    private void setRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mItemAdapter);
    }

    private void errorReadingDb(DatabaseError errorMsg) {
        Toast.makeText(this, errorMsg.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_your_listing:
                // TODO call your_listing activity
                // store the items with this user's id to a list and past the list
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logout();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnItemClickListener(Upload eachItem) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(Upload.DISPLAY_ITEM_STRING, eachItem);
        intent.putExtra(ItemDetailActivity.PREVIOUS_ACTIVITY, ItemDetailActivity.ITEM_LIST_ACTIVITY);
        startActivity(intent);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList(Upload.DISPLAY_ITEM_STRING, mItemList);
    }
}
