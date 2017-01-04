package com.vaibhav.applock.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaibhav.applock.R;
import com.vaibhav.applock.Utilities;
import com.vaibhav.applock.bean.AppInfo;

import java.util.List;

/**
 * Created by vaibhav on 2/1/17.
 */

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    Context context;
    List<AppInfo> appInfos;

    public PackageAdapter(Context context, List<AppInfo> appInfos) {
        this.context = context;
        this.appInfos = appInfos;

    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder holder, int position) {

        holder.checkedTextView.setText(appInfos.get(position).getAppname());
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public AppCompatCheckedTextView checkedTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkedTextView = (AppCompatCheckedTextView) itemView.findViewById(R.id.textview);
            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (checkedTextView.isChecked())
                        checkedTextView.setChecked(false);
                    else
                        checkedTextView.setChecked(true);
                    Utilities.getInstance().action(context, checkedTextView.isChecked(), appInfos.get(getAdapterPosition()).getPname());

                }
            });
        }


    }
}
