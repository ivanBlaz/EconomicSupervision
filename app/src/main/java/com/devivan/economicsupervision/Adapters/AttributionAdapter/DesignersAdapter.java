package com.devivan.economicsupervision.Adapters.AttributionAdapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Objects.Designer.Designer;
import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

import java.util.ArrayList;

import io.grpc.internal.SharedResourceHolder;

public class DesignersAdapter extends RecyclerView.Adapter<DesignersAdapter.ViewHolder> {

    boolean images;
    ArrayList<Designer> designers;
    SparseBooleanArray visibility = new SparseBooleanArray();

    public DesignersAdapter(boolean images, ArrayList<Designer> designers) {
        this.images = images;
        this.designers = designers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_designer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ///////////////
        // UI Design //
        ///////////////
        holder.btnShowWorks.setImageResource(visibility.get(position) ? R.drawable.ic_keyboard_arrow_up_50 : R.drawable.ic_keyboard_arrow_down_50);
        holder.rvWorks.setVisibility(visibility.get(position) ? View.VISIBLE : View.GONE);

        /////////////
        // UI Data //
        /////////////
        // Designer
        Designer designer = designers.get(position);

        // Name
        holder.txtvName.setText(designer.getName());

        // Works
        WorksAdapter worksAdapter = new WorksAdapter(images, designer.getWorks());
        System.setAdapter(holder.rvWorks, worksAdapter, true, false, new GridLayoutManager(holder.itemView.getContext(), 3, RecyclerView.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return designers != null ? designers.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtvName;
        ImageView btnShowWorks;
        RecyclerView rvWorks;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //////////
            // Find //
            //////////
            // txtvName
            txtvName = itemView.findViewById(R.id.txtvName);
            txtvName.setOnClickListener(v -> System.loadUrl(itemView.getContext(), designers.get(getAdapterPosition()).getUrl()));

            // btnShowWorks
            btnShowWorks = itemView.findViewById(R.id.btnShowWorks);
            btnShowWorks.setOnClickListener(v -> {
                if (rvWorks.getVisibility() == View.VISIBLE) {
                    rvWorks.setVisibility(View.GONE);
                    btnShowWorks.setImageResource(R.drawable.ic_keyboard_arrow_down_50);
                    visibility.put(getAdapterPosition(), false);
                } else {
                    rvWorks.setVisibility(View.VISIBLE);
                    btnShowWorks.setImageResource(R.drawable.ic_keyboard_arrow_up_50);
                    rvWorks.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in));
                    visibility.put(getAdapterPosition(), true);
                }
            });

            // rvWorks
            rvWorks = itemView.findViewById(R.id.rvWorks);
        }
    }
}
