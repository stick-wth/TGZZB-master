package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tgzzb.cdc.adapter.CheckPicRcAdapter;
import com.tgzzb.cdc.bean.ImageAddress;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CYCheckPicActivity extends Activity {

    private CheckPicRcAdapter adapter;
    private RecyclerView recyclerView;

    private List<LocalMedia> images = new ArrayList<>();

    public static void start(Context context, String billcodeurl) {
        Intent starter = new Intent(context, CYCheckPicActivity.class);
        starter.putExtra("billcodeurl", billcodeurl);
        context.startActivity(starter);
    }

    private Context context;
    private ArrayList<ImageAddress> imgDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycheck_pic);
        context = this;
        imgDatas = new ArrayList<>();
        Commons.setTitle(this, "查看照片");

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(manager);
        adapter = new CheckPicRcAdapter(R.layout.item_iv_tv, imgDatas);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PictureSelector.create(CYCheckPicActivity.this).externalPicturePreview(position, images);
            }
        });
        String billcodeurl = getIntent().getStringExtra("billcodeurl");
        getImageAddress(billcodeurl);
    }

    public void getImageAddress(String billcodeurl) {

        OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/GetImagAdd")
                .addParams("billcodeurl", billcodeurl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String arg0, int arg1) {
                        String jsonData = Commons.parseXML(arg0);
                        imgDatas = Commons.parseJsonList(jsonData, new TypeToken<ArrayList<ImageAddress>>() {
                        }.getType());
                        if (imgDatas != null) {
                            adapter.setNewData(imgDatas);

                            for(int i=0;i<imgDatas.size();i++){
                                LocalMedia media = new LocalMedia();
                                String path = imgDatas.get(i).getAddress();
                                media.setCompressPath(path);
                                media.setPath(path);
                                images.add(media);
                            }

                        } else {
                            Commons.ShowToast(context, "您还没有上传图片!");
                            recyclerView.setVisibility(View.GONE);
                            findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        Commons.ShowToast(context, "请求超时！");
                    }

                });

    }
}
