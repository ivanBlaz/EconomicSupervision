package com.devivan.economicsupervision.Adapters.AttributionAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.devivan.economicsupervision.Objects.Designer.Works.Work;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;
import com.google.errorprone.annotations.concurrent.LazyInit;

import java.util.ArrayList;

public class WorksAdapter extends RecyclerView.Adapter<WorksAdapter.ViewHolder> {

    boolean images;
    ArrayList<Work> works;

    public WorksAdapter(boolean images, ArrayList<Work> works) {
        this.images = images;
        this.works = works;
    }

    @NonNull
    @Override
    public WorksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_work, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorksAdapter.ViewHolder holder, int position) {
        ///////////////
        // UI Design //
        ///////////////
        holder.imgvWork.setVisibility(images ? View.VISIBLE : View.GONE);
        holder.lottieWork.setVisibility(images ? View.GONE : View.VISIBLE);

        /////////////
        // UI Data //
        /////////////
        Work work = works.get(position);
        if (images) holder.imgvWork.setImageResource(work.getResource());
        else holder.lottieWork.setAnimation(work.getAnimation());
    }

    @Override
    public int getItemCount() {
        return works != null ? works.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgvWork;
        LottieAnimationView lottieWork;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //////////
            // Find //
            //////////
            // imgvWork
            imgvWork = itemView.findViewById(R.id.imgvWork);
            imgvWork.setOnClickListener(v -> System.loadUrl(itemView.getContext(), works.get(getAdapterPosition()).getUrl()));


            // lottieWork
            lottieWork = itemView.findViewById(R.id.lottieWork);
            lottieWork.setOnClickListener(v -> System.loadUrl(itemView.getContext(), works.get(getAdapterPosition()).getUrl()));
        }
    }
}
