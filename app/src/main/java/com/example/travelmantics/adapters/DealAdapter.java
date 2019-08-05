package com.example.travelmantics.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelmantics.R;
import com.example.travelmantics.activities.DealActivity;
import com.example.travelmantics.models.TravelDeal;
import com.example.travelmantics.utils.FirebaseUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    private ArrayList<TravelDeal> deals;
    private ImageView imageDeal;

    public DealAdapter() {
        CollectionReference travelDealsRef = FirebaseUtil.travelDealsRef;
        this.deals = FirebaseUtil.mDeals;
        EventListener<QuerySnapshot> mEventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                assert snapshots != null;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            TravelDeal td = dc.getDocument().toObject(TravelDeal.class);
                            Log.d("Deal: ", td.getTitle());
                            td.setId(dc.getDocument().getId());
                            deals.add(td);
                            notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            notifyDataSetChanged();
                            break;
                        case REMOVED:
                            notifyDataSetChanged();
                            break;
                    }
                }
            }
        };
        travelDealsRef.addSnapshotListener(mEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;

        DealViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageDeal = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

        void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
            tvDescription.setText(deal.getDescription());
            tvPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            TravelDeal selectedDeal = deals.get(position);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                Glide.with(imageDeal.getContext())
                        .load(url)
                        .into(imageDeal);
                Glide.with(imageDeal.getContext())
                        .load(url)
                        .override(160, 160)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }
}
