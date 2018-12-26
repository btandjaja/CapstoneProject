package com.buntorotandjaja.www.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ItemListActivity extends AppCompatActivity {
    @BindView(R.id.tv_uid)
    TextView mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        String uid = getIntent().getStringExtra(getString(R.string.unique_id));
        mUid.setText(uid);
    }
}
