package com.buntorotandjaja.www.capstoneproject;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.annotation.NonNull;

public class CheckDBListing {

    private static DatabaseReference mDbRef;
    private static String mSellerUid;
    private static boolean mSold;

    public static boolean getSold(Context context) {
        mSold = false;
        mSellerUid = FirebaseAuth.getInstance().getUid();
        if (mSellerUid == null) return false;
        mDbRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.app_name));
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    if (upload.getSellerUId().equals(mSellerUid)) {
                        if (upload.getSold()) {
                            mSold = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // nothing sold, don't have to do anything
            }
        });
        return mSold;
    }
}
