package com.buntorotandjaja.www.capstoneproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SellActivity extends AppCompatActivity {

    public static int CHANGE_IMAGE = 0;
    private static final int RIGHT_AWAY = 0;
    private static final int THIRTY_MINUTES = 18000;
    private final static int PICK_IMAGE_REQUEST = 1;
    private final static int REQUEST_IMAGE_CAPTURE = 2;
    private final static int PERMISSION_REQUEST_CODE = 3;
    private final static String TAG = SellActivity.class.getSimpleName();

    @BindView(R.id.toolbar_sell_acitivty) Toolbar mToolbar;
    @BindView(R.id.imageButton_take_picture) ImageButton mTakePicture;
    @BindView(R.id.imageButton_upload_file) ImageButton mUploadPicture;
    @BindView(R.id.item_image) ThreeTwoImageView mItemImage;
    @BindView(R.id.et_item_title) EditText mItemTitle;
    @BindView(R.id.et_item_description) EditText mItemDescription;
    @BindView(R.id.et_item_price) EditText mPrice;
    @BindView(R.id.button_sell) Button mSell;
    @BindView(R.id.pb_uploading_image) ContentLoadingProgressBar mProgressBarItemUploading;

    private Uri mImageUri;
    private String mCurrentPhotoPath;
    private Boolean mHasImage;
    private Boolean meetPostingRequirement;

    // Firebase
    private StorageReference mStorageReference;
    private DatabaseReference mDbRef;
    private StorageTask mUploadTask;
    private FirebaseJobDispatcher mJobDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        ButterKnife.bind(this);
        setupToolbar();
        mHasImage = false;
        meetPostingRequirement = false;
        mJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        // TODO needed when
        mStorageReference = FirebaseStorage.getInstance().getReference(getString(R.string.app_name));
        mDbRef = FirebaseDatabase.getInstance().getReference(getString(R.string.app_name));
        mUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                    return;
                }

                openFile();
            }
        });

        // TODO picture from camera
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

        // TODO sell button
        mSell.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this, getString(R.string.add_picture_or_camera_error), Toast.LENGTH_LONG).show();
            meetPostingRequirement = false;
        }
    }

    // check for views before confirming to sell
    private void checkEmptyViews() {
        if (!mHasImage) {
            Toast.makeText(this, getString(R.string.error_no_image), Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(mItemTitle.getText().toString().trim())) {
            mItemTitle.setError(getString(R.string.error_title_required));
            mItemTitle.requestFocus();
            return;
        } else if (TextUtils.isEmpty(mItemDescription.getText().toString().trim())) {
            mItemDescription.setError(getString(R.string.error_description_required));
            mItemDescription.requestFocus();
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
            final String title = mItemTitle.getText().toString().trim();
            final String description = mItemDescription.getText().toString().trim();
            final String price = mPrice.getText().toString().trim();
            // TODO firebaseStorage
            final String uploadInfo = FirebaseAuth.getInstance().getUid() + "_"
                    + title + "_"
                    + System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri);
            final StorageReference fileReference = mStorageReference.child(uploadInfo);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            hideIndicator();
                        }
                    })
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
                            while (!imageUri.isComplete());
                            // TODO for firebase database
                            Upload upload = new Upload(uploadInfo,
                                    imageUri.getResult().toString(),
                                    title,
                                    description,
                                    FirebaseAuth.getInstance().getUid(),
                                    price);
                            String uploadId = mDbRef.push().getKey();
                            upload.setUploadId(uploadId);
                            if (uploadId != null) {
                                mDbRef.child(uploadId).setValue(upload);
                                clearInput();
                                uploadSuccessful();
                                hideIndicator();
                                // todo think
//                                startJob();
                            }
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
                            mProgressBarItemUploading.setVisibility(View.INVISIBLE);
                            Toast.makeText(SellActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            // TODO hide progress bar
                            hideIndicator();
                        }
                    });
        } else {
            noImageSelected();
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

    private void operationCancelled() {
        Toast.makeText(this, getString(R.string.camera_operation_cancelled), Toast.LENGTH_SHORT).show();
    }

    // TODO confused
    private void startJob() {
        Job job = mJobDispatcher.newJobBuilder()
                .setService(SoldJobService.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag(SoldJobService.JOB_TAG)
                .setTrigger(Trigger.executionWindow(RIGHT_AWAY, THIRTY_MINUTES))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        mJobDispatcher.mustSchedule(job);
    }

    private void stopJob() {
        mJobDispatcher.cancel(SoldJobService.JOB_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sell, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                } else {
                    // TODO delete all fields
                    clearInput();
                }
                return true;
            case android.R.id.home:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadingInProgress();
                } else {
                    onBackPressed();
                }
                return true;
            default:
                errorMessage();
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearInput() {
        mItemImage.setImageDrawable(getDrawable(R.drawable.no_image_icon));
        mItemTitle.setText("");
        mItemDescription.setText("");
        mPrice.clearComposingText();
//        mPrice.setText("0");
        mHasImage = false;
        meetPostingRequirement = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            uploadingInProgress();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void errorMessage() {
        showToast(R.string.error);
    }

    private void uploadingInProgress() {
        showToast(R.string.uploading_in_progress);
    }

    private void noImageSelected() {
        showToast(R.string.no_image_selected);
    }

    private void uploadSuccessful() {
        showToast(R.string.upload_successful);
    }

    private void showToast(int stringInt) {
        Toast.makeText(this, getString(stringInt), Toast.LENGTH_SHORT).show();
    }
}