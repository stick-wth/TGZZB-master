package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.CYExpListAdapter;
import com.tgzzb.cdc.bean.CYItem;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;

public class CYActivity extends Activity implements View.OnClickListener, ExpandableListView.OnChildClickListener {

    private CYExpListAdapter expAdapter;
    private Context context;
    private String titleName;
    private String permission;

    public static void start(Context context, String permission, String titleName) {
        Intent starter = new Intent(context, CYActivity.class);
        starter.putExtra("permission", permission);
        starter.putExtra("titleName", titleName);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cy);
        context = this;
        permission = getIntent().getExtras().getString("permission");
        titleName = getIntent().getExtras().getString("titleName");
        initViews();
        getCyDatas(false);
    }

    private void getCyDatas(final boolean newData) {
        try {
            OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/GetCYData")
                    .addParams("Usernameurl", URLEncoder.encode(MyApp.currentUser.getMogilelogin(), "utf-8"))
                    .addParams("KKtypeurl", permission).build()
                    .execute(new StringCallback() {

                        @Override
                        public void onResponse(String arg0, int arg1) {
                            String jsonData = Commons.parseXML(arg0);
                            ArrayList<CYItem> datas = Commons.parseJsonList(jsonData,
                                    new TypeToken<ArrayList<CYItem>>() {
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
                                expAdapter.setDatas(new ArrayList<CYItem>());
                                expAdapter.notifyDataSetChanged();
                                Commons.ShowToast(context, "没有查询到数据！");
                            }
                        }

                        @Override
                        public void onError(Call arg0, Exception arg1, int arg2) {
                            Commons.ShowToast(context, "请求超时！");
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        ExpandableListView expList;
        Commons.setTitle(this, titleName);
        expList = findViewById(R.id.explist);
        expList.setOnChildClickListener(this);
        expAdapter = new CYExpListAdapter(this);
        expList.setAdapter(expAdapter);
        CheckBox ckb = findViewById(R.id.ckb);
        ckb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        //findViewById(R.id.btn_cyyc).setOnClickListener(this);
        findViewById(R.id.btn_cywc).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_cywc:
                try {
                    UpdateHGCyStatus("查验完成");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void UpdateHGCyStatus(final String status) throws UnsupportedEncodingException {
        SparseBooleanArray checkStates = expAdapter.getCheckStatus();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int chenkedCount = 0;
        for (int i = 0; i < checkStates.size(); i++) {
            if (checkStates.get(i)) {
                String bgdh = expAdapter.getDatas().get(i).getBgdh();
                String str = "{\"bgdh\":\"" + bgdh + "\",\"status\":\"" + URLEncoder.encode(status, "utf-8") + "\",\"remark\":\"\"}";
                sb.append(str);
                chenkedCount++;
            }
        }
        sb.append("]");
        if (chenkedCount == 0) {
            Commons.ShowToast(context, "请先选择单号");
            return;
        }
        //"http://58.210.237.170:8083/ifsweb/Service/IfswebAPPWebservice3.asmx"
        //getResources().getString(R.string.request_url_asmx170) +
        Log.d("TAGGG", MyApp.currentUser.getMogilelogin() + ",UpdateHGCyStatus: " + sb);
        OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170) + "/UpdateHGCyStatus")
                .addParams("usernameurl", MyApp.currentUser.getMogilelogin())
                .addParams("JsonCYdataUrl", sb.toString()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String arg0, int arg1) {
                        String jsonData = Commons.parseXML(arg0);
                        if (TextUtils.equals(jsonData, "1")) {
                            Commons.ShowDialog(context, "修改查验状态", "修改完成。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getCyDatas(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        getCyDatas(true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        CYItem item = expAdapter.getDatas().get(groupPosition);
        CYDetailActivity.start(context, item);
        return false;
    }
}
