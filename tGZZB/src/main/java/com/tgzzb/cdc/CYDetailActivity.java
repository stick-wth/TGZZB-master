package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tgzzb.cdc.bean.CYItem;
import com.tgzzb.cdc.utils.Commons;
import com.tgzzb.cdc.utils.PicCode;
import com.tgzzb.cdc.utils.PicturePelector.FullyGridLayoutManager;
import com.tgzzb.cdc.utils.PicturePelector.GridImageAdapter;
import com.tgzzb.cdc.utils.UpLoadPicProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;

public class CYDetailActivity extends Activity implements View.OnClickListener {
    private CYItem item;
    private Context context;
    private List<LocalMedia> selectList;
    private PopupWindow popWindow;
    private PopupWindow pop_updateStatus;
    private View parentView;
    private GridImageAdapter gvAdapter;
    private UpLoadPicProgressDialog progressDialog;
    private EditText et_bzl;
    private View popView_updateStatus;
    private TextView tv_plancydate;
    private String cyStatus;
    private TextView tv_hgcyzt;
    private TextView tv_plancydateTop;
    public static void start(Context context, CYItem item) {
        Intent starter = new Intent(context, CYDetailActivity.class);
        starter.putExtra("item", item);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = LayoutInflater.from(this).inflate(R.layout.activity_cydetail, null);
        setContentView(parentView);
        context = this;
        item = getIntent().getExtras().getParcelable("item");
        initViews();
    }

    private void initViews() {

        Commons.setTitle(CYDetailActivity.this, "单号详情");

        popView_updateStatus = LayoutInflater.from(this).inflate(R.layout.pop_updatecystatus, null);
        tv_plancydate = popView_updateStatus.findViewById(R.id.tv_plancydate);
        popView_updateStatus.findViewById(R.id.btn_pickdata).setOnClickListener(this);

        final LinearLayout ll_datapicker = popView_updateStatus.findViewById(R.id.ll_datapicker);

        RadioGroup rg_cystatus = popView_updateStatus.findViewById(R.id.rg_cystatus);
        rg_cystatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                cyStatus = radioButton.getText().toString();
                if(TextUtils.equals("待查验", cyStatus)){
                    ll_datapicker.setVisibility(View.VISIBLE);
                }else {
                    ll_datapicker.setVisibility(View.GONE);
                }
            }
        });

        TextView tv_qymc = findViewById(R.id.tv_qymc);
        tv_qymc.setText(item.getQymc());
        TextView tv_bgdh = findViewById(R.id.tv_bgdh);
        tv_bgdh.setText(item.getBgdh());
        TextView tv_tdh = findViewById(R.id.tv_tdh);
        tv_tdh.setText(item.getTdh());
        TextView tv_tgfs = findViewById(R.id.tv_tgfs);
        tv_tgfs.setText(item.getTgfs());
        TextView tv_jcgq = findViewById(R.id.tv_jcgq);
        tv_jcgq.setText(item.getJcgq());
        TextView tv_ck = findViewById(R.id.tv_ck);
        tv_ck.setText(item.getCk());
        TextView tv_ywy = findViewById(R.id.tv_ywy);
        tv_ywy.setText(item.getYwy());
        TextView tv_js = findViewById(R.id.tv_js);
        tv_js.setText(item.getJs());
        TextView tv_zl = findViewById(R.id.tv_zl);
        tv_zl.setText(item.getZl());
        TextView tv_dlgsmc = findViewById(R.id.tv_dlgsmc);
        tv_dlgsmc.setText(item.getDlgsmc());
        tv_hgcyzt = findViewById(R.id.tv_hgcyzt);
        tv_hgcyzt.setText(item.getHgcyzt());
        TextView tv_hdbh = findViewById(R.id.tv_hdbh);
        tv_hdbh.setText(item.getHdbh());
        TextView tv_xcbz = findViewById(R.id.tv_xcbz);
        tv_xcbz.setText(item.getXcbz());
        et_bzl = findViewById(R.id.et_bzl);
        et_bzl.setText(item.getReamrk());
        tv_plancydateTop = findViewById(R.id.tv_plancydate);
        tv_plancydateTop.setText(item.getPlancydate());

        findViewById(R.id.btn_update_status).setOnClickListener(this);
        findViewById(R.id.btn_sccyd).setOnClickListener(this);
        initPopGrid();
    }

    public void initPopGrid() {
        selectList = new ArrayList<>();
        progressDialog = new UpLoadPicProgressDialog(context, "上传查验单");
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_uploadpic, null);
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
        RecyclerView rl = popView.findViewById(R.id.recycler_popwindow);
        gvAdapter = new GridImageAdapter(this, Commons.initOnAddPicClickListener(CYDetailActivity.this, selectList, PicCode.REQUESTPICCODE1));
        gvAdapter.setSelectMax(4);
        gvAdapter.setList(selectList);
        rl.setLayoutManager(layoutManager);
        rl.setAdapter(gvAdapter);
        popWindow = Commons.getPopWindowForUploadPic(this, popView, this);
        gvAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(CYDetailActivity.this).externalPicturePreview(position, selectList);
                            break;
                    }
                }
            }
        });
    }


    public void uploadPic() {
        progressDialog.setMaxCount(selectList.size());
        String str64 = Commons.getStr64ByImgPath(selectList.get(progressDialog.getCurrentProgress()).getCompressPath());
        OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/UploadHGCyImag")
                .addParams("username", MyApp.currentUser.getMogilelogin())
                .addParams("bgdh", item.getBgdh())
                .addParams("orafileName", Commons.getCurrentDaterTimeSSS() + ".jpg")
                .addParams("filesString", str64)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String arg0, int arg1) {
                        String jsonData = Commons.parseXML(arg0);
                        if (TextUtils.equals(jsonData, "1")) {
                            progressDialog.addProgress();
                        } else {
                            progressDialog.uploadError();
                        }
                        if (progressDialog.getCurrentProgress() < selectList.size()) {
                            uploadPic();
                        } else {
                            progressDialog.uploadOver(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    resetView();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        Commons.ShowToast(context, "请求超时！");
                    }

                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.btn_cyyc:
//                try {
//                    UpdateHGCyStatus("查验异常");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.btn_cywc:
//                try {
//                    UpdateHGCyStatus("查验完成");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                break;
            case R.id.btn_sccyd:
                popWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.btn_positive_upload:
                if (progressDialog.getCurrentProgress() < selectList.size()) {
                    progressDialog.show();
                    uploadPic();
                } else {
                    Commons.ShowDialog(context, "上传查验单", "请您先添加照片。");
                }
                break;
            case R.id.btn_negative_upload:
                popWindow.dismiss();
                break;

            case R.id.btn_update_status:
                if (pop_updateStatus == null) {

                    pop_updateStatus = Commons.getPopWindowForUpdateCYStatus(this, popView_updateStatus, this);
                }
                pop_updateStatus.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.btn_pickdata:
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //Commons.ShowToast(context, date.toString());
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                        tv_plancydate.setText(df.format(date));
                        pop_updateStatus.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                    }
                }).setType(new boolean[]{true, true, true, true, true, true}).setLabel("年","月","日","时","分","秒").build();
                pop_updateStatus.dismiss();
                pvTime.show();
                break;

            case R.id.btn_positive_update:
                if(TextUtils.isEmpty(cyStatus)){
                    Commons.ShowToast(context,"请先选择您要更改的查验状态！");
                    return;
                }
                if(TextUtils.equals(cyStatus,"待查验"))
                if(TextUtils.isEmpty(tv_plancydate.getText())){
                    Commons.ShowToast(context,"请先选择计划查验日期！");
                    return;
                }
                try {
                    UpdateHGCyStatus(cyStatus);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_negative_update:
                pop_updateStatus.dismiss();
                break;
            default:
                break;
        }
    }

    public void UpdateHGCyStatus(final String status) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        String plancydate = tv_plancydate.getText().toString();
        sb.append("[");//URLEncoder.encode(status, "utf-8")
        String str = "{\"bgdh\":\"" + item.getBgdh() + "\",\"status\":\"" +URLEncoder.encode(status, "utf-8") + "\",\"remark\":\"" + URLEncoder.encode(et_bzl.getText().toString(), "utf-8") + "\",\"plancydate\":\""+plancydate+"\"}";
        sb.append(str);
        sb.append("]");
        Log.d("TAGGG", MyApp.currentUser.getMogilelogin() + ",UpdateHGCyStatus: " + sb);
        //

        OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170)+"/UpdateHGCyStatus")
                .addParams("usernameurl", MyApp.currentUser.getMogilelogin())
                .addParams("JsonCYdataUrl", sb.toString()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String arg0, int arg1) {
                        String jsonData = Commons.parseXML(arg0);
                        Log.d("TAGGG", "onResponse: " + jsonData);
                        if (TextUtils.equals(jsonData, "1")) {
                            Commons.ShowDialog(context, "修改查验状态", "修改完成。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pop_updateStatus.dismiss();
                                    tv_hgcyzt.setText(status);
                                    if(tv_plancydate!=null)
                                    tv_plancydateTop.setText(tv_plancydate.getText());
                                }
                            });
                        } else {
                            Commons.ShowDialog(context, "修改查验状态", "单号:" + jsonData + "修改失败，请您稍后再试。");
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        Commons.ShowToast(context, "请求超时！");
                    }

                });
    }

    public void resetView() {
        selectList.clear();
        gvAdapter.setList(selectList);
        gvAdapter.notifyDataSetChanged();
        popWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PicCode.REQUESTPICCODE1:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    gvAdapter.setList(selectList);
                    gvAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }
}