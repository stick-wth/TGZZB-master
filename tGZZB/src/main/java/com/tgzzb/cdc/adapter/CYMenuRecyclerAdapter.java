package com.tgzzb.cdc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tgzzb.cdc.R;


/**
 * Created by Administrator - stick on 2018/3/8.
 * e-mail:253139409@qq.com
 */

public class CYMenuRecyclerAdapter extends RecyclerView.Adapter<CYMenuRecyclerAdapter.MViewHolder> {

    private String[] titles;
    private int[] images;
    private Context context;
    private OnItemClickListener listener;

    public CYMenuRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_gv_main, parent, false);

        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MViewHolder holder, final int position) {
        holder.title.setText(titles[position]);
        holder.image.setImageResource(images[position]);
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onItemClick(CYMenuRecyclerAdapter.this,holder.v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public void setData(String[] titles, int[] images) {
        this.titles = titles;
        this.images = images;
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        private View v;
        private TextView title;
        private ImageView image;

        public MViewHolder(View v) {
            super(v);
            this.v = v;
            title = v.findViewById(R.id.text);
            image = v.findViewById(R.id.image);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(CYMenuRecyclerAdapter adapter, View view, int position);
    }
}
