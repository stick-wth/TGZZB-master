package com.tgzzb.cdc;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.adapter.MrCheckPicGRAdapter;
import com.tgzzb.cdc.bean.Mr_Data;
import com.tgzzb.cdc.bean.Traffic_Message;
import com.tgzzb.cdc.imagepicker.GalleryActivity;
import com.tgzzb.cdc.imagepicker.ImageItem;
import com.tgzzb.cdc.imagepicker.Res;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import okhttp3.Call;
import android.widget.GridView;
import android.widget.TextView;

public class MrCheckAllPicActivity extends Activity implements OnClickListener {
	private Context context;
	private MrCheckPicGRAdapter adapter;
	private final int ADAPTER_CHANGE = 10001;
	private final int NOPIC = 10002;
	private GridView mr_checkpic_gr;
	private Mr_Data data;
//	public static ArrayList<ImageItem> checkBitmap;
//	public static boolean fromCheckPicActivity = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mrcheckpic);
		context = this;
		initViews();
		Res.init(context);
		Intent intent = getIntent();
		data = (Mr_Data) intent.getSerializableExtra("Mr_data");
		GetImage();

	}

	private void initViews() {

		Commons.setTitle(MrCheckAllPicActivity.this,"查看照片");
		mr_checkpic_gr = (GridView) findViewById(R.id.mr_checkpic_gr);
		// WindowManager wm = (WindowManager)
		// getSystemService(Context.WINDOW_SERVICE);
		// int width = wm.getDefaultDisplay().getWidth();
		// mr_checkpic_gr.setColumnWidth(width / 4);
		adapter = new MrCheckPicGRAdapter(context);
		mr_checkpic_gr.setAdapter(adapter);
		mr_checkpic_gr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, GalleryActivity.class);
				intent.putExtra("fromAddress", "MrCheckAllPicActivity");
				intent.putStringArrayListExtra("imgUrls", adapter.getImgUrls());
				intent.putExtra("position", "1");
				intent.putExtra("ID", position);
				startActivity(intent);

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void GetImage() {

		OkHttpUtils.post().url(getResources().getString(R.string.request173_url_asmx) + "/GetImage").addParams("dzhm", data.getDzhm())
				.addParams("ljh", data.getLjh()).addParams("carno", MyApp.currentUser.getMogilelogin()).build()
				.execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						ArrayList<Traffic_Message> list = Commons.parseJsonList(jsonData,
								new TypeToken<List<Traffic_Message>>() {
								}.getType());

						if (list != null) {
							adapter.setList(list);
							adapter.notifyDataSetChanged();
						} else {
							mr_checkpic_gr.setVisibility(View.GONE);
							findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowToast(context, "请求失败,请检查您的网络连接。");
						mr_checkpic_gr.setVisibility(View.GONE);
						findViewById(R.id.ll_no_pic).setVisibility(View.VISIBLE);
					}
				});
	}
}
