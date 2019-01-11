package com.buntorotandjaja.www.capstoneproject;

import android.content.Context;
import android.widget.ImageView;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class SoldJobService extends JobService {

    public static final String JOB_TAG = "check_sold_service";

    @Override
    public boolean onStartJob(JobParameters job) {
        Context mContext = getApplicationContext();
        if (CheckDBListing.getSold(mContext)) {
            SellActivity.CHANGE_IMAGE = 1;
        }
        // run on seperate thread
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        /* true means, we're not done, please reschedule */
        return true;
    }
}
