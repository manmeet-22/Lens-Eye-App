package com.manmeet.lenseye.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manmeet.lenseye.R;
import com.manmeet.lenseye.model.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {

    private Context mContext;
    private List<Result> mList;

    public ResultAdapter(Context mContext, List<Result> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultHolder(LayoutInflater.from(mContext).inflate(R.layout.result_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        Result result = mList.get(position);
        holder.itemName.setText(result.getResultName());
        String s = Double.toString(result.getResultAccuracy() * 100);
        holder.itemAccuracy.setText(String.format("Probability : %s%%", s));

    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public class ResultHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemName)
        TextView itemName;
        @BindView(R.id.itemAccuracy)
        TextView itemAccuracy;

        public ResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
