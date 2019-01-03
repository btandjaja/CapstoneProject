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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

//import com.google.api.core.ApiFuture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemListActivity extends AppCompatActivity {

    @BindView(R.id.tv_listing) TextView mTvListing;
    @BindView(R.id.tb_item_list) Toolbar mToolbar;
    @BindView(R.id.rv_item_list) RecyclerView mRecyclerView;

    // Firebase Firestore
    private FirebaseFirestore mFirestore;

    private List<Upload> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        // TODO set toolbar
        setToolbar();
        mItemList = new ArrayList<>();
        // TODO read db
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(getString(R.string.app_name))
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String uploadInfo = (String) document.get(getString(R.string.db_uploadInfo));
                        String sellerUId = (String) document.get(getString(R.string.db_uid));
                        boolean sold =  Boolean.valueOf((String) document.get(getString(R.string.db_sold)));
                        String buyerUId = (String) document.get(getString(R.string.db_buyer));
                        String imageUri = (String) document.get(getString(R.string.db_downloadUrl));
                        String title = (String) document.get(getString(R.string.db_title));
                        String description = (String) document.get(getString(R.string.db_description));
                        String price = (String) document.get(getString(R.string.db_price));
                        Upload retrievedObject = new Upload(uploadInfo, imageUri, title, description,
                                sellerUId, price);
                        retrievedObject.setBuyerUId(buyerUId);
                        retrievedObject.setSold(sold);
                        mItemList.add(retrievedObject);
                    }
                }
            }
        });

        // TODO read images
        // TODO create adapter
        // createAdapter
//        setRecyclerView();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        mTvListing.setText(getString(R.string.app_name));
    }

    private void setRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO create adapter, setAdapter to recyclerView, need to get data first and extract data, past data

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
                break;
            case R.id.menu_sold:
                // TODO call sold activity
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
        // TODO need?
        super.onBackPressed();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
