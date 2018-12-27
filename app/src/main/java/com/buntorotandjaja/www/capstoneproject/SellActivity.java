package com.buntorotandjaja.www.capstoneproject;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class SellActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_sell_acitivty) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        ButterKnife.bind(this);
        setupToolbar();
//        Toast.makeText(this, FirebaseAuth.getInstance().getUid(), Toast.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
