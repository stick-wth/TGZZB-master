package com.tgzzb.cdc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.tgzzb.cdc.MrCheckAllPicActivity;
import com.tgzzb.cdc.MyApp;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.Traffic_Message;
import com.tgzzb.cdc.imagepicker.PublicWay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MrCheckPicGRAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	public List<Traffic_Message> list;
	private Context context;
	// 存放图片
	private ArrayList<String> imgUrls = new ArrayList<String>();
	// 记录已加载数量
	int count = 0;
	public MrCheckPicGRAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		list = new ArrayList<Traffic_Message>();
	}

	public List<Traffic_Message> getList() {
		return list;
	}

	public void setList(List<Traffic_Message> list) {
		PublicWay.num = list.size();
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	// 使用map存储图片 再赋值给list

	/**
	 * @return the imgUrls
	 */
	public ArrayList<String> getImgUrls() {
		return imgUrls;
	}

	/**
	 * @param imgUrls
	 *            the imgUrls to set
	 */
	public void setImgUrls(ArrayList<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_gv_main, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.text.setTextSize(10);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.setView(list.get(position));
		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
		public TextView text;
		BitmapDisplayConfig config;

		public void setView(Traffic_Message message) {

//			Glide.with(MyApp.getInstance())
//					.load(context.getResources().getString(R.string.request173_pic) + message.getUrl())
//					.error(R.drawable.default_pic).into(image);
			RequestOptions options = new RequestOptions();
			options.placeholder(R.drawable.picloading);
			options.error(R.drawable.default_pic);
			Glide.with(context).load(context.getResources().getString(R.string.request173_pic) + message.getUrl()).apply(options).into(image);

			text.setText(message.getQymc() + "_" + message.getLjh() + "_" + message.getMessage());
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		ArrayList<String> tempArray = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			tempArray.add(context.getResources().getString(R.string.request173_pic) + list.get(i).getUrl());
		}
		imgUrls = tempArray;
	}
}
