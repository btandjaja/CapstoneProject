package com.buntorotandjaja.www.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Bundle data = getIntent().getExtras();
        Upload loadedItem = data.getParcelable(Upload.DISPLAY_ITEM_STRING);

    }
}
