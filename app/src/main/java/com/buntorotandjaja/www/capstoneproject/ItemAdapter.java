package com.buntorotandjaja.www.capstoneproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context mContext;
    private List<Upload> mItemList;
    private ItemAdapterOnClickHandler mOnClickHandler;

    public ItemAdapter(ItemAdapterOnClickHandler onClickHandler) { mOnClickHandler = onClickHandler; }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int itemCardView = R.layout.each_item_cardview;
        LayoutInflater inflate = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflate.inflate(itemCardView, parent, shouldAttachToParentImmediately);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemView, int position) {
        Upload singleItem = mItemList.get(position);
        if (singleItem == null) return;
        // TODO need to fix
        Uri imageUri = Uri.parse(singleItem.getImageUrl());
        Picasso.get().load(imageUri).fit().centerCrop().into(itemView.mImageView);
        itemView.mItemTitle.setText(singleItem.getTitle());
        itemView.mItemPrice.setText(singleItem.getPrice());
        int color = singleItem.getSold() ? mContext.getResources().getColor(R.color.background_sold) :
                mContext.getResources().getColor(R.color.background_available);
        String itemAvailability = singleItem.getSold() ? mContext.getString(R.string.item_sold) :
                mContext.getString(R.string.item_available);
        itemView.mItemStatus.setText(itemAvailability);
        itemView.mItemStatus.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void setItemList(Context context, List<Upload> itemList) {
        mContext = context;
        mItemList = itemList;
        notifyDataSetChanged();
    }

    public interface ItemAdapterOnClickHandler {
        void OnItemClickListener(Upload eachItem);
    }

    /* view holder */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_itemlist_image) ThreeTwoImageView mImageView;
        @BindView(R.id.tv_itemListing_itemName) TextView mItemTitle;
        @BindView(R.id.tv_itemListing_itemPrice) TextView mItemPrice;
        @BindView(R.id.ll_each_card) LinearLayout mLinearLayout;
        @BindView(R.id.tv_item_availability_status) TextView mItemStatus;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // TODO onclicklistener not working
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Upload uploadData = mItemList.get(getAdapterPosition());
            uploadData.setPosition(getAdapterPosition());
            if (uploadData == null) return;
            mOnClickHandler.OnItemClickListener(uploadData);
        }
    }
}
