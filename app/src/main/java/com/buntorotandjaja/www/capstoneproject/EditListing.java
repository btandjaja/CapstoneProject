package com.buntorotandjaja.www.capstoneproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditListing extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final String TAG = EditListing.class.getSimpleName();
    public static final String POSITION = "position";

    @BindView(R.id.imageButton_upload_file_edit_listing) ImageButton mUploadImage;
    @BindView(R.id.imageButton_take_picture_edit_listing) ImageButton mTakePicture;
    @BindView(R.id.toolbar_edit_listing_acitivty) Toolbar mToolbar;
    @BindView(R.id.et_item_title_edit_listing) EditText mTitle;
    @BindView(R.id.et_item_description_edit_listing) EditText mDescription;
    @BindView(R.id.et_item_price_edit_listing) EditText mPrice;
    @BindView(R.id.pb_uploading_image_edit_listing) ContentLoadingProgressBar mProgressBarItemUploading;
    @BindView(R.id.item_image) ThreeTwoImageView mItemImage;
    @BindView(R.id.button_update_edit_listing) Button mUpdate;

    private Upload mLoadedItem;
    private String mCurrentPhotoPath;
    private Uri mImageUri;
    private boolean mHasImage;
    private boolean meetPostingRequirement;

    private DatabaseReference mDbRef;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        ButterKnife.bind(this);
        setupToolbar();
        extractData();
        fillData();
        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                    return;
                }
                openFile();
            }
        });

        // TODO needed when
        mStorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.app_name));
        mDbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
                dispatchTakePictureIntent();
            }
        });

        // TODO price input convert to currency format
        mPrice.addTextChangedListener(new DecimalCurrency(mPrice, "#,###"));

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                    return;
                }

                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
                dispatchTakePictureIntent();
            }
        });

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmptyViews();
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                } else {
                    if (meetPostingRequirement) {
                        uploadFile();
                    }
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

    private void extractData() {
        Bundle data = getIntent().getExtras();
        if (data == null) errorExtractingData();
        mLoadedItem = data.getParcelable(Upload.DISPLAY_ITEM_STRING);
    }

    private void fillData() {
        String title = mLoadedItem.getTitle();
        String description = mLoadedItem.getDescription();
        String price = mLoadedItem.getPrice();
        mImageUri = Uri.parse(mLoadedItem.getImageUrl());
        mTitle.setText(title);
        mDescription.setText(description);
        mPrice.setText(price);
        mHasImage = true;
        meetPostingRequirement = true;
        Picasso.get().load(mImageUri).fit().centerCrop().into(mItemImage);
    }

    private void openFile() {
        Intent imageFromFileIntent = new Intent();
        imageFromFileIntent.setType("image/*");
        imageFromFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageFromFileIntent, PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurs while creating file
                Log.e(TAG, e.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(this,
                        getPackageName() + getString(R.string.concate_fileprovider),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,     /* prefix */
                ".jpg",     /* suffix */
                storageDir         /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // check for views before confirming to sell
    private void checkEmptyViews() {
        if (!mHasImage) {
            Toast.makeText(this, getString(R.string.error_no_image), Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            mTitle.setError(getString(R.string.error_title_required));
            mTitle.requestFocus();
            return;
        } else if (TextUtils.isEmpty(mDescription.getText().toString().trim())) {
            mDescription.setError(getString(R.string.error_description_required));
            mDescription.requestFocus();
            return;
        } else if (TextUtils.isEmpty(mPrice.getText().toString().trim())) {
            mPrice.setError(getString(R.string.error_price_required));
            mPrice.requestFocus();
            return;
        }
        meetPostingRequirement = true;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // upload to firebaseDatabase and firebaseStorage (image)
    private void uploadFile() {
        if (mImageUri != null) {
            showIndicator();
            final String title = mTitle.getText().toString().trim();
            final String description = mDescription.getText().toString().trim();
            final String price = mPrice.getText().toString().trim();
            // TODO firebaseStorage
            final String uploadInfo = FirebaseAuth.getInstance().getUid() + "_"
                    + title + "_"
                    + System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri);
            final StorageReference fileReference = mStorageRef.child(uploadInfo);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBarItemUploading.setProgress(0);
                                }
                            }, 500);

                            Task<Uri> imageUri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!imageUri.isComplete()) ;

                            mLoadedItem.setUploadInfo(uploadInfo);
                            mLoadedItem.setImageUrl(imageUri.getResult().toString());
                            mLoadedItem.setTitle(title);
                            mLoadedItem.setDescription(description);
                            mLoadedItem.setPrice(price);
                            String uploadId = mLoadedItem.getUploadId();
                            mDbRef.child(uploadId).setValue(mLoadedItem);
                            uploadSuccessful();
                            hideIndicator();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBarItemUploading.setProgress((int) progress);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // TODO hide progress bar
                            mProgressBarItemUploading.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditListing.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            cancelUploadingPicture();
                        }
                    });
        } else {
            noImageSelected();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null &&
                data.getData() != null) {
            if (resultCode == RESULT_OK) {
                mHasImage = true;
                mImageUri = data.getData();
                Picasso.get().load(mImageUri).fit().centerCrop().into(mItemImage);
                meetPostingRequirement = true;
            } else {
                meetPostingRequirement = false;
                operationCancelled();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mHasImage = true;
                mItemImage.setImageURI(Uri.parse(mCurrentPhotoPath));
            } else if (resultCode == RESULT_CANCELED) {
                operationCancelled();
                meetPostingRequirement = false;
            }
        } else {
            cancelUploadingPicture();
            meetPostingRequirement = false;
        }
    }

    private void showIndicator() {
        getSupportActionBar().setHomeButtonEnabled(false);
        mProgressBarItemUploading.setVisibility(View.VISIBLE);
    }

    private void hideIndicator() {
        getSupportActionBar().setHomeButtonEnabled(true);
        mProgressBarItemUploading.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            uploadingInProgress();
            return false;
        }
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(POSITION, mLoadedItem.getPosition());
        setResult(Activity.RESULT_OK);
        finish();
        super.onBackPressed();
    }

    private void errorExtractingData() {
        Toast.makeText(this, "Data corrupted.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void cancelUploadingPicture() { Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show(); }

    private void uploadingInProgress() {
        Toast.makeText(this, "Uploading in progress, please wait", Toast.LENGTH_SHORT).show();
    }

    private void noImageSelected() {
        Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show();
    }

    private void uploadSuccessful() {
        Toast.makeText(this, "Upload succesful", Toast.LENGTH_SHORT).show();
    }

    private void operationCancelled() {
        Toast.makeText(this, getString(R.string.camera_operation_cancelled), Toast.LENGTH_SHORT).show();
    }
}