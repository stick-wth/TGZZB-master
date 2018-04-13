package com.tgzzb.cdc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.tgzzb.cdc.adapter.DrivaerExpListAdapter;
import com.tgzzb.cdc.bean.DriverItem;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.PublicWay;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.tgzzb.cdc.bean.DriverYwbhDdcode;
import com.tgzzb.cdc.imagepicker.Bimp;
import com.tgzzb.cdc.utils.PicCode;
import com.tgzzb.cdc.utils.PicturePelector.FullyGridLayoutManager;
import com.tgzzb.cdc.utils.PicturePelector.GridImageAdapter;
import com.tgzzb.cdc.utils.SelectPicDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import okhttp3.Call;
import okhttp3.MediaType;

public class DriverActivity extends Activity implements OnClickListener, OnChildClickListener {

    private List<LocalMedia> selectList = new ArrayList<>();
    private Context context;
    private ExpandableListView expList;
    private DrivaerExpListAdapter expAdapter;
    private final int UPLOAD_SUCCESS = 1005;
    private final int UPLOAD_FAILED = 1006;
    private ArrayList<DriverYwbhDdcode> thQsDatas;
    private ProgressDialog uploadPicDialog;
    private int numProgress = 1;

    private Handler handler = new Handler() {
        //private int numProgress = 1;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_SUCCESS:
                    uploadPicDialog.setProgress(numProgress++);
                    if (uploadPicDialog.getMax() == numProgress - 1) {
                        // 所有图片上传完成
                        uploadPicDialog.setMessage("操作完成！");
                        uploadPicDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
                        Commons.ShowToast(context, "操作完成！");
                        numProgress = 1;
//                        clearPic();
                        getDriverData(true);
                        for (int i = 0; i < expAdapter.getDatas().size(); i++) {
                            expList.collapseGroup(i);
                        }
                    } else {
                        uploadOne();
                    }
                    break;

                case UPLOAD_FAILED:
                    uploadPicDialog.dismiss();
                    //Commons.ShowDialog(context, "上传签收单", "第"+numProgress+"张图片上传失败，要重新上传吗？");

                    AlertDialog dialog = new AlertDialog.Builder(context).setTitle("上传签收单").setMessage("第" + numProgress + "张图片上传失败，要重新上传吗？")
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //uploadPicDialog.dismiss();
                                    numProgress = 1;
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPicDialog.show();
                                    uploadOne();
                                }
                            }).create();
                    dialog.show();

                    break;

                default:
                    break;
            }
        }
    };
    private View parentView;
    private PopupWindow qsd_popWindow;
    private MySingleClickListener singleClickListener;
    private Button btn_yth;
    private GridImageAdapter gvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = DriverActivity.this;
        parentView = LayoutInflater.from(context).inflate(R.layout.activity_driver, null);
        setContentView(parentView);
        initMyClickListener();
        initViews();
        getDriverData(false);
    }

    private void initMyClickListener() {
        singleClickListener = new MySingleClickListener() {

            @Override
            protected void onSingleClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_yth:
                        YTH();
                        break;
                    case R.id.btn_positive:
                        try {
                            upLoadQsd();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    private void initViews() {
        Commons.setTitle(this, getApplication().getResources().getString(R.string.driver_title));
        expList = (ExpandableListView) findViewById(R.id.explist);
        expList.setOnChildClickListener(this);
        expAdapter = new DrivaerExpListAdapter(this);
        expList.setAdapter(expAdapter);
        btn_yth = (Button) findViewById(R.id.btn_yth);
        btn_yth.setOnClickListener(singleClickListener);
        findViewById(R.id.btn_submit_qsd).setOnClickListener(this);

        CheckBox ckb = (CheckBox) findViewById(R.id.ckb);
        ckb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    expAdapter.checkAll();
                } else {
                    expAdapter.deleteAll();
                }
                expAdapter.notifyDataSetChanged();
            }
        });


        View popView = LayoutInflater.from(this).inflate(R.layout.pop_uploadpic, null);
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(DriverActivity.this, 4, GridLayoutManager.VERTICAL, false);
        RecyclerView rl = popView.findViewById(R.id.recycler_popwindow);
        gvAdapter = new GridImageAdapter(this, Commons.initOnAddPicClickListener(this, selectList, PicCode.REQUESTPICCODE1));
        gvAdapter.setSelectMax(PicCode.MAXPICSELECTNUM);
        gvAdapter.setList(selectList);
        rl.setLayoutManager(layoutManager);
        rl.setAdapter(gvAdapter);
        qsd_popWindow = Commons.getPopWindowForUploadPic(this, popView, this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
             case R.id.btn_yth:
             YTH();
             break;
            case R.id.btn_submit_qsd:
                Commons.changeWindowBg(this, 0.5f);
                qsd_popWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.btn_positive_upload:
                try {
                    upLoadQsd();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_negative_upload:
                qsd_popWindow.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(context, DriverAddInfoActivity.class);
        intent.putExtra("DriverItem", expAdapter.getDatas().get(groupPosition));
        startActivity(intent);
        return false;
    }

    public void show_qsdPopWindow() {


        if (qsd_popWindow == null) {

        }
        Commons.changeWindowBg(this, 0.5f);
        qsd_popWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
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

                default:
                    break;
            }
        }
    }

    class QsdAdapter extends BaseAdapter {

        private ArrayList<ImageItem> datas;

        public QsdAdapter() {
            super();
            datas = new ArrayList<ImageItem>();
        }

        public ArrayList<ImageItem> getDatas() {
            return datas;
        }

        public void setDatas(ArrayList<ImageItem> datas) {
            this.datas = datas;
        }

        public void addDatas(ArrayList<ImageItem> datas) {
            this.datas.addAll(datas);
        }

        @Override
        public int getCount() {
            if (datas.size() == PublicWay.num) {
                return PublicWay.num;
            }
            return datas.size() == 0 ? 1 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_published_grida, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (datas.size() == 0) {// 如果没有图片显示一张预设图片
                holder.iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_pic));
            } else {// 有图就显示图
                holder.setImage(datas.get(position).getImagePath());
            }
            return convertView;
        }

        class ViewHolder {
            ImageView iv;

            void setImage(String url) {
                Glide.with(context).load(url).into(iv);
            }
        }
    }

    private void YTH() {

        thQsDatas = new ArrayList<DriverYwbhDdcode>();
        SparseBooleanArray checkStates = expAdapter.getCheckStatus();

        System.out.println("size=" + checkStates.size());

        for (int i = 0; i < checkStates.size(); i++) {
            if (checkStates.get(i)) {
                DriverItem item = expAdapter.getDatas().get(i);
                thQsDatas.add(new DriverYwbhDdcode(item.getYwbh(), item.getDdcode(), item.getType()));
            }
        }
        if (thQsDatas.size() == 0) {
            Commons.ShowToast(context, "请先选择单号");
            return;
        }
        Gson g = new Gson();
        OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/UpdateYsStatus")
                .addParams("billcodesJson", g.toJson(thQsDatas))
                .addParams("", "").build().execute(new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                String jsonData = Commons.parseXML(arg0);
                try {
                    int resultCode = Integer.parseInt(jsonData);
                    if (resultCode == 1) {
                        Commons.ShowDialog(context, "提货操作", "提货成功!");
                        getDriverData(true);
                        for (int i = 0; i < expAdapter.getDatas().size(); i++) {
                            expList.collapseGroup(i);
                        }
                    } else {
                        Commons.ShowDialog(context, "提货操作", "提货失败!");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Commons.ShowDialog(context, "提货操作", "提货失败!");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                Commons.ShowDialog(context, "提货操作", "请求超时!");
            }
        });
    }

    private void upLoadQsd() throws NotFoundException, UnsupportedEncodingException {

        thQsDatas = new ArrayList<DriverYwbhDdcode>();
        SparseBooleanArray checkStates = expAdapter.getCheckStatus();
        for (int i = 0; i < checkStates.size(); i++) {
            if (checkStates.get(i)) {
                DriverItem item = expAdapter.getDatas().get(i);
                thQsDatas.add(new DriverYwbhDdcode(item.getYwbh(), item.getDdcode(), item.getType()));
            }
        }
        if (thQsDatas.size() == 0) {
            Commons.ShowToast(context, "请先选择单号");
            return;
        }
        if (selectList.size() == 0) {
            Commons.ShowToast(context, "请先选择照片");
            return;
        }
        uploadPicDialog = Commons.getDialogLoadingPic(context, "上传签收单", selectList.size(), false);
        qsd_popWindow.dismiss();
        uploadPicDialog.show();
        uploadOne();
    }


    public void uploadOne() {
        Gson g = new Gson();
        final String jstrBillCodes = g.toJson(thQsDatas);
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str64 = Commons.getStr64ByImgPath(selectList.get(numProgress - 1).getCompressPath());
                String uploadResult = Commons.getResponse170(context, "UploadFile", "fileBytes", str64,
                        "OrafileNameurl", Commons.getCurrentDaterTimeSSS() + ".jpg",
                        "typeurl",
                        expAdapter.getDatas().get(0).getType(),
                        "billcodesJson", jstrBillCodes,
                        "remarkurl", "",
                        "filetypeurl", "签收单");
                Log.d("TGZZB", "uploadResult = " + uploadResult);
                try {
                    int resultCode = Integer.parseInt(uploadResult);
                    if (resultCode == 1) {
                        handler.sendEmptyMessage(UPLOAD_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(UPLOAD_FAILED);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(UPLOAD_FAILED);
                }

            }
        }.start();

    }

    private void getDriverData(final boolean newData) {

        try {
            OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/GetDriveData")
                    .addParams("carcodeurl", URLEncoder.encode(MyApp.currentUser.getMogilelogin(), "utf-8")).build()
                    .execute(new StringCallback() {

                        @Override
                        public void onResponse(String arg0, int arg1) {
                            String jsonData = Commons.parseXML(arg0);
                            ArrayList<DriverItem> datas = Commons.parseJsonList(jsonData,
                                    new TypeToken<ArrayList<DriverItem>>() {
                                    }.getType());
                            if (datas != null) {
                                if (newData) {
                                    expAdapter.setNewDatas(datas);
                                } else {
                                    expAdapter.setDatas(datas);
                                }

                                expAdapter.notifyDataSetChanged();
                            } else {
                                // 没有获取到数据
                                expAdapter.setDatas(new ArrayList<DriverItem>());
                                expAdapter.notifyDataSetChanged();
                                Commons.ShowToast(context, "没有查询到数据！");
                            }
                        }

                        @Override
                        public void onError(Call arg0, Exception arg1, int arg2) {
                            Commons.ShowToast(context, "请求超时！");
                        }

                    });
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
