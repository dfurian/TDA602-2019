package lbs.lab.maclocation;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;

// *******************************************************************
// *** This file does not need to be read, and should not be edited **
// *******************************************************************

/**
 * ItemsAdapter is used to bind the ArrayList of items to the list of items presented in the GUI
 * - this is done in a RecyclerView.
 */
class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private ArrayList<Item> mItemsData;
    private Context mContext;

    ItemsAdapter(Context context, ArrayList<Item> itemsData) {
        this.mItemsData = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        Item currentItem = mItemsData.get(position);
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return mItemsData.size();
    }

    /**
     * Inner class for the View that each list item gets.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText;
        private TextView mInfoText;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.title);
            mInfoText = itemView.findViewById(R.id.info);

            itemView.setOnClickListener(this);
        }

        void bindTo(Item currentItem) {
            mTitleText.setText(currentItem.getTitle());
            mInfoText.setText(currentItem.getInfo());
        }

        @Override
        public void onClick(View v) {
            Item currentItem = mItemsData.get(getAdapterPosition());
            // launches a DetailActivity for this particular item
            Intent detailIntent = new Intent(mContext, DetailActivity.class);
            detailIntent.putExtra(DetailActivity.TITLE_DATA, currentItem.getTitle());
            detailIntent.putExtra(DetailActivity.INFO_DATA, currentItem.getInfo());
            mContext.startActivity(detailIntent);
        }
    }
}
