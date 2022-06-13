package com.devivan.economicsupervision.Adapters.CategoriesAdapter;

import android.annotation.SuppressLint;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Objects.Account.Categories.Category;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.CategoryFragment;
import com.devivan.economicsupervision.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    CategoryFragment fragment;
    public ArrayList<Category> categories;
    private final SparseBooleanArray visible = new SparseBooleanArray();

    public CategoryAdapter(CategoryFragment fragment, ArrayList<Category> categories) {
        this.fragment = fragment;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @SuppressLint("Recycle")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category c = categories.get(position);

        if (visible.get(position)) {
            // Set recycler visible
            holder.rvCC.setVisibility(View.VISIBLE);

            // Initialize sub categories adapter
            SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(fragment, categories.get(position).getSubCategories(), position);

            // Set adapter
            System.setAdapter(holder.rvCC, subCategoryAdapter, true, false, new LinearLayoutManager(fragment.getActivity(), RecyclerView.VERTICAL, false));
        } else
            // Set recycler visibility gone
            holder.rvCC.setVisibility(View.GONE);

        // Trending image
        if (Math.abs(c.getProf()) > Math.abs(c.getLoss())) {
            holder.imgvCCProfStatus.setImageResource(R.drawable.income_up);
            holder.imgvCCLossStatus.setImageResource(R.drawable.expenses_down);
        } else if (Math.abs(c.getProf()) == Math.abs(c.getLoss())) {
            holder.imgvCCProfStatus.setImageResource(android.R.color.transparent);
            holder.imgvCCLossStatus.setImageResource(android.R.color.transparent);
        } else {
            holder.imgvCCProfStatus.setImageResource(R.drawable.income_down);
            holder.imgvCCLossStatus.setImageResource(R.drawable.expenses_up);
        }

        // Trending values
        holder.txtvCCProf.setText(c.getProf() != 0 ? System.moneyFormat.format(c.getProf()) : "-"); // TODO: error
        holder.txtvCCLoss.setText(c.getLoss() != 0 ? System.moneyFormat.format(c.getLoss()) : "-");
        holder.txtvCCMoney.setText(c.getMoney() != 0 ? System.moneyFormat.format(c.getMoney()) : "-");
        //////////////////////////////////////////////////////////////////////////////////////////////

        // Category image + name
        holder.imgvCC.setImageResource(fragment.system.activity.getResources().obtainTypedArray(R.array.imgCategories).getResourceId(position,-1));
        holder.txtvCC.setText(c.getName());
        ///////////////////////////////////
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgvCC,imgvCCProfStatus,imgvCCLossStatus;
        TextView txtvCC,txtvCCProf,txtvCCLoss,txtvCCMoney;
        RecyclerView rvCC;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find
            imgvCC = itemView.findViewById(R.id.imgvCC);
            imgvCCProfStatus = itemView.findViewById(R.id.imgvCCProfStatus);
            imgvCCLossStatus = itemView.findViewById(R.id.imgvCCLossStatus);
            txtvCC = itemView.findViewById(R.id.txtvCC);
            txtvCCProf = itemView.findViewById(R.id.txtvCCProf);
            txtvCCLoss = itemView.findViewById(R.id.txtvCCLoss);
            txtvCCMoney = itemView.findViewById(R.id.txtvCCMoney);
            rvCC = itemView.findViewById(R.id.rvCC);
            ////////////////////////////////////////

            // itemView + txtvCC click listener
            itemView.setOnClickListener(v -> onClick(getAdapterPosition()));
            txtvCC.setOnClickListener(v -> onClick(getAdapterPosition()));
            //////////////////////////////////////////////////////////////
        }

        private void onClick(int pos) {
            if (pos != -1 && pos < categories.size()) {
                if (pos != 0) {
                    for (int i = 0; i < categories.size(); i++)
                        if (pos != i) visible.put(i, false);
                    if (visible.get(pos)) {
                        rvCC.setVisibility(View.GONE);
                        visible.put(pos, false);
                    } else {
                        rvCC.setVisibility(View.VISIBLE);
                        visible.put(pos, true);
                    }
                    notifyDataSetChanged();
                    fragment.rvCategories.scrollToPosition(pos);
                } else {
                    // Insert movement
                    fragment.system.insertMovement(fragment, "0");
                }
            }
        }
    }
}
