package com.devivan.economicsupervision.Adapters.CategoriesAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.Objects.Account.Categories.SubCategories.SubCategory;
import com.devivan.economicsupervision.System.System;
import com.devivan.economicsupervision.Fragments.CategoryFragment;
import com.devivan.economicsupervision.R;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    int RIGHT = 1,LEFT = 2;
    CategoryFragment fragment;
    List<SubCategory> subCategories;
    int position;

    public SubCategoryAdapter(CategoryFragment fragment, List<SubCategory> subCategories, int position) {
        this.fragment = fragment;
        this.subCategories = subCategories;
        this.position = position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType==LEFT?R.layout.line_subcategory_1:R.layout.line_subcategory_2, parent, false);
        return new SubCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubCategory sc = subCategories.get(position);
        if (position == 0) holder.v.setVisibility(View.VISIBLE);

        // Sub category name
        holder.txtvSubCName.setText(subCategories.get(position).getName());

        if (position == 0) {
            // Sum values
            double sumProf = subCategories.stream().mapToDouble(subCat -> subCat.prof).sum();
            double sumLoss = subCategories.stream().mapToDouble(subCat -> subCat.loss).sum();
            double sumMoney = subCategories.stream().mapToDouble(subCat -> subCat.money).sum();
            ///////////////////////////////////////////////////////////////////////////////////

            // Trending image
            if (Math.abs(sumProf) > Math.abs(sumLoss)) {
                holder.imgvSubCProf.setImageResource(R.drawable.income_up);
                holder.imgvSubCLoss.setImageResource(R.drawable.expenses_down);
            } else if (Math.abs(sumProf) == Math.abs(sumLoss)) {
                holder.imgvSubCProf.setImageResource(android.R.color.transparent);
                holder.imgvSubCLoss.setImageResource(android.R.color.transparent);
            } else {
                holder.imgvSubCProf.setImageResource(R.drawable.income_down);
                holder.imgvSubCLoss.setImageResource(R.drawable.expenses_up);
            }

            // Trending values
            holder.txtvSubCProf.setText(sumProf != 0 ? System.moneyFormat.format(sumProf) : "-");
            holder.txtvSubCLoss.setText(sumLoss != 0 ? System.moneyFormat.format(sumLoss) : "-");
            holder.txtvSubCMoney.setText(sumMoney != 0 ? System.moneyFormat.format(sumMoney) : "-");
            ////////////////////////////////////////////////////////////////////////////////////////
        } else {
            // Trending image
            if (Math.abs(sc.getProf()) > Math.abs(sc.getLoss())) {
                holder.imgvSubCProf.setImageResource(R.drawable.income_up);
                holder.imgvSubCLoss.setImageResource(R.drawable.expenses_down);
            } else if (Math.abs(sc.getProf()) == Math.abs(sc.getLoss())) {
                holder.imgvSubCProf.setImageResource(android.R.color.transparent);
                holder.imgvSubCLoss.setImageResource(android.R.color.transparent);
            } else {
                holder.imgvSubCProf.setImageResource(R.drawable.income_down);
                holder.imgvSubCLoss.setImageResource(R.drawable.expenses_up);
            }

            // Trending values
            holder.txtvSubCProf.setText(sc.getProf() != 0 ? System.moneyFormat.format(sc.getProf()) : "-");
            holder.txtvSubCLoss.setText(sc.getLoss() != 0 ? System.moneyFormat.format(sc.getLoss()) : "-");
            holder.txtvSubCMoney.setText(sc.getMoney() != 0 ? System.moneyFormat.format(sc.getMoney()) : "-");
            /////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    @Override
    public int getItemCount() {
        return subCategories != null ? subCategories.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? LEFT : RIGHT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View v;
        TextView txtvSubCName, txtvSubCMoney, txtvSubCProf, txtvSubCLoss;
        ImageView imgvSubCProf, imgvSubCLoss;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView.findViewById(R.id.divSubCat);
            txtvSubCName = itemView.findViewById(R.id.txtvSubCName);
            txtvSubCMoney = itemView.findViewById(R.id.txtvSubCMoney);
            txtvSubCProf = itemView.findViewById(R.id.txtvSubCProf);
            txtvSubCLoss = itemView.findViewById(R.id.txtvSubCLoss);

            imgvSubCProf = itemView.findViewById(R.id.imgvSubCProf);
            imgvSubCLoss = itemView.findViewById(R.id.imgvSubCLoss);

            // itemView + txtvCC click listener
            itemView.setOnClickListener(v -> onClick(getAdapterPosition()));
            txtvSubCName.setOnClickListener(v -> onClick(getAdapterPosition()));
            ////////////////////////////////////////////////////////////////////

        }

        private void onClick(int pos) {
            if (pos != -1 && pos < subCategories.size()) fragment.system.insertMovement(fragment, position + "#" + pos);
        }
    }
}
