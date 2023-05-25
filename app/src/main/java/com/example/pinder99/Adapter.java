package com.example.pinder99;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pinder99.data.ReportEntity;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder>  {
    public ArrayList<ReportEntity> reportList;
    private final ReportItemClickListener reportClickListener;


    public Adapter(ReportItemClickListener reportClickListener) {
        this.reportClickListener = reportClickListener;
    }



    @NonNull
    @Override
    public Adapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ItemViewHolder holder, int position) {
        holder.onBind(reportList.get(position));
        clickReportItem(holder);
    }

    @Override
    public int getItemCount() {
        if(reportList != null) {
            return reportList.size();
        } else {
            return 0;
        }
    }


    public void clickReportItem(ItemViewHolder holder) {
        holder.itemView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportEntity report = reportList.get(holder.getAdapterPosition());
                reportClickListener.clickReportItem(report);
            }
        });
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private final View itemView;
        TextView report_time;
        TextView report_type;
        TextView locate;
        TextView detail_locate;
        TextView type;
        TextView feature;
        TextView phoneNum;
        TextView pet_gender;
        ConstraintLayout cv;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.type = itemView.findViewById(R.id.textview_type);
            this.locate = itemView.findViewById(R.id.textview_location);
            this.detail_locate=itemView.findViewById(R.id.textview_Detail_location);
            this.report_time = itemView.findViewById(R.id.textview_upload_date);
            this.report_type = itemView.findViewById(R.id.textview_animal_type);
            this.cv = itemView.findViewById(R.id.conView);
            this.feature = itemView.findViewById(R.id.textview_animal_feature);
            this.phoneNum=itemView.findViewById(R.id.textview_phoneNum);
            this.pet_gender=itemView.findViewById(R.id.textview_animal_gender);

        }

        public void onBind(ReportEntity reportData){
            /*TextView locate = itemView.findViewById(R.id.textview_location);
            TextView report_time = itemView.findViewById(R.id.textview_upload_date);
            TextView kind = itemView.findViewById(R.id.textview_animal_type);
            TextView detail_locate = itemView.findViewById(R.id.textview_Detail_location);*/
            ImageView animalImage = itemView.findViewById(R.id.imageview_animal_picture);
            type.setText(reportData.getReport_type());
            locate.setText(reportData.getLocate());
            report_time.setText(reportData.getReport_time());
            report_type.setText(reportData.getPet_kind());
            detail_locate.setText(reportData.getDetail_locate());
            feature.setText(reportData.getFeature());
            phoneNum.setText(reportData.getPhoneNum());
            pet_gender.setText(reportData.getPet_Gender());
            Glide.with(animalImage.getContext()).load(reportData.getReport_image()).into(animalImage);
        }
    }
}

