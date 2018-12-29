package com.buntorotandjaja.www.capstoneproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SellActivity extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int REQUEST_IMAGE_CAPTURE = 2;

    @BindView(R.id.toolbar_sell_acitivty) Toolbar mToolbar;
    @BindView(R.id.imageButton_take_picture) ImageButton mTakePicture;
    @BindView(R.id.imageButton_upload_file) ImageButton mUploadPicture;
    @BindView(R.id.item_image) ThreeTwoImageView mItemImage;
    @BindView(R.id.et_item_title) EditText mItemTitle;

    private String mUId;
    private Uri mImageUri;

    // Firebase
    private FirebaseFirestore mDb;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        ButterKnife.bind(this);
        setupToolbar();


        // TODO needed when
        mDb = FirebaseFirestore.getInstance();
        mUId = FirebaseAuth.getInstance().getUid();
        mUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        // TODO picture from file
        mItemTitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mItemTitle.getText().toString().length() >= Integer.valueOf(getString(R.string.char25))) {
                    Toast.makeText(SellActivity.this, getString(R.string.max_length), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // TODO picture from camera
        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(SellActivity.this, getString(R.string.no_camera_available), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.chevron_left_white_24dp);
    }

    private void openFile() {
        Intent imageFromFileIntent = new Intent();
        imageFromFileIntent.setType("image/*");
        imageFromFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageFromFileIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == REQUEST_IMAGE_CAPTURE) &&
                resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().centerCrop().into(mItemImage);
        } else {
            // TODO show error?
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sell, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_delete:
                // TODO delete all fields
                mItemImage.setImageDrawable(getDrawable(R.drawable.no_image_icon));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
