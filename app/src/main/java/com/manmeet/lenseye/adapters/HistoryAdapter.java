package com.manmeet.lenseye.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manmeet.lenseye.MainActivity;
import com.manmeet.lenseye.R;
import com.manmeet.lenseye.model.History;
import com.manmeet.lenseye.model.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    Cursor mCursor;
    Context mContext;
    public HistoryAdapter(Context mContext, Cursor mCursor) {
        this.mCursor = mCursor;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        Gson gson = new Gson();
        final String historyResultList = mCursor.getString(2);
        final Type listType = new TypeToken<ArrayList<Result>>() {
        }.getType();
        final ArrayList<Result> resultList = gson.fromJson(historyResultList, listType);

        final String historyImage = mCursor.getString(1);
        Type imageType = new TypeToken<byte[]>() {
        }.getType();
        final byte[] bitmapArr = gson.fromJson(historyImage, imageType);
        final Bitmap result = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        final Bitmap previewImage = Bitmap.createBitmap(result, 0, 0, result.getWidth()
                , result.getHeight(), matrix, true);
        holder.historyImageView.setImageBitmap(previewImage);
        holder.historyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //History history = new History(result, resultList);
                Intent intent = new Intent(mContext, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("historyImage",historyImage);
                bundle.putString("historyList",historyResultList);
                intent.putExtra("historyBundle", bundle);
                intent.putExtra("historyFlag", 1);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.history_image_view)
        ImageView historyImageView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
