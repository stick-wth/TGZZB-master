package com.tgzzb.cdc.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.ImageAddress;

import java.util.List;

/**
 * Created by Administrator - stick on 2018/3/22.
 * e-mail:253139409@qq.com
 */

public class CheckPicRcAdapter extends BaseQuickAdapter<ImageAddress, BaseViewHolder> {

    public CheckPicRcAdapter(int layoutResId, @Nullable List<ImageAddress> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ImageAddress item) {
        String imgaddress = item.getAddress();
        int lastSlash = imgaddress.lastIndexOf("/");
        String imgName = imgaddress.substring(lastSlash + 1);
        helper.setText(R.id.item_tv,imgName);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.picloading);
        options.error(R.drawable.default_pic);
        Glide.with(mContext).load(imgaddress).apply(options).into((ImageView) helper.getView(R.id.item_iv));
    }
}
