package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.Lv_Mr_Adapter;
import com.tgzzb.cdc.bean.Mr_Data;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MrActivity extends Activity implements OnItemClickListener, OnClickListener {

	private ListView lv_mr;
	private Lv_Mr_Adapter adapter;
	private Context context;
	private final int GETDATA_CODE = 10001;
	// private Handler handler = new Handler() {
	//
	// @Override
	// public void handleMessage(android.os.Message msg) {
	// if (msg.obj == null) {
	// Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
	// return;
	// }
	// if (msg.what == GETDATA_CODE) {
	// try {
	// if (msg.obj == null) {
	// Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
	// return;
	// }
	// if (Integer.parseInt((String) msg.obj) == 0) {
	// Commons.ShowToast(context, "没有查询到数据！");
	// }
	// if (Integer.parseInt((String) msg.obj) < 0) {
	// // Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
	// Commons.ShowToast(context, "没有查询到数据！");
	// }
	// } catch (Exception e) {
	// Type typeOfT = new TypeToken<List<Mr_Data>>() {
	// }.getType();
	// List<Mr_Data> list = Commons.parseJsonList((String) msg.obj, typeOfT);
	// adapter.setDatas(list);
	// adapter.notifyDataSetChanged();
	// }
	// }
	// };
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mr);
		initData();
		initViews();
		// LoadDataThread load = new LoadDataThread();
		// load.start();
		//
		GetPusData();

	}

	private void initData() {
		context = this;
		adapter = new Lv_Mr_Adapter(context);
	}

	private void initViews() {

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("Mr取货");
		Commons.setTitle(MrActivity.this,"Mr取货");
		findViewById(R.id.title_left).setOnClickListener(this);

		lv_mr = (ListView) findViewById(R.id.lv_mr);
		lv_mr.setAdapter(adapter);
		lv_mr.setOnItemClickListener(this);
	}

	// class LoadDataThread extends Thread {
	//
	// @Override
	// public void run() {
	// super.run();
	// Mr_User user = Mr_User.readFromSharedPreferences();
	// String resultCode = Commons.getResponse(context,
	// getResources().getString(R.string.method_getpus),
	// getResources().getString(R.string.method_getpus_params_carno),
	// user.getMogilelogin(),
	// getResources().getString(R.string.method_getpus_params_plandate),
	// Commons.getCurrentDate());// Commons.getCurrentDate()
	// Message msg = new Message();
	// msg.what = GETDATA_CODE;
	// msg.obj = resultCode;
	// handler.sendMessage(msg);
	// }
	// }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Mr_Data data = adapter.getDatas().get(position);
		Intent intent = new Intent(context, MrAddInfoActivity.class);
		intent.putExtra("currentItem", data);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}

	public void GetPusData() {

		OkHttpUtils.post().url(getResources().getString(R.string.request173_url_asmx) + "/GetPusData").addParams("carno",MyApp.getCurrentUser().getMogilelogin())
				.addParams("plandate", Commons.getCurrentDate()).build().execute(new StringCallback() {

			@Override
			public void onResponse(String arg0, int arg1) {
				String jsonData = Commons.parseXML(arg0);
				ArrayList<Mr_Data> datas = Commons.parseJsonList(jsonData, new TypeToken<List<Mr_Data>>() {
				}.getType());
				if (datas != null) {
					adapter.setDatas(datas);
					adapter.notifyDataSetChanged();
				} else {
					Commons.ShowToast(context, "没有查询到数据！");
				}
			}

			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
			}
		});
	}
}
