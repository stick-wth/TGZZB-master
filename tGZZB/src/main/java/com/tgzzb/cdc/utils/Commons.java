package com.tgzzb.cdc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import com.luck.picture.lib.tools.Constant;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.utils.PicturePelector.GridImageAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import net.bither.util.NativeUtil;

public class Commons {
	public static void ShowToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}

	// 显示普通Dialog
	public static void ShowDialog(Context context, String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("确定", null).create();
		dialog.show();
	}
	// 显示普通Dialog
	public static void ShowDialog(Context context, String title, String message,DialogInterface.OnClickListener listener) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("确定", listener).create();
		dialog.show();
	}

	// 返回dialog对象
	public static AlertDialog getDialog(Context context, String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("确定", null).create();
		return dialog;
	}

	public static void setTitle(final Activity activity, String title) {
		TextView tv_title = activity.findViewById(R.id.tv_title);
		tv_title.setText(title);
		activity.findViewById(R.id.title_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
	}

	/**
	 * 解析json
	 */
	public static <T> T parseJsonClass(String jsonString, Class<T> clazz) {
		T t = null;
		try {
			Gson gson = new Gson();
			String str = jsonString.substring(1, jsonString.length() - 1);
			System.out.println("str = " + str);
			t = gson.fromJson(str, clazz);
			System.out.println("json解析成功！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("json解析失败！");
		}
		return t;
	}

	/**
	 * 解析json集合
	 */
	public static <T> T parseJsonList(String jsonString, Type typeOfT) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, typeOfT);
			System.out.println("json解析成功！");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("json解析失败！");
		}
		return t;
	}

	public static String parseXML(String string) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(string));
			int eventType = parser.getEventType();
			String json = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						// 开始某个节点
						if ("string".equals(nodeName)) {
							json = parser.nextText();
						}
						break;
					case XmlPullParser.END_TAG:
						// 解析完成

						break;
				}
				eventType = parser.next();
			}

			System.out.println("xml解析成功 " + json);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xml解析失败");
		}
		System.out.println("xml解析失败");
		return null;
	}

	/**
	 * 请求服务器MR
	 */
//	public static String getResponse(Context context, String methodString, String... params) {
//		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request_namespace), methodString);
//		// SoapObject rpc = new SoapObject("http://steve.cw169.4everdns.com/",
//		// methodString);
//
//		envelope.bodyOut = rpc;
//		envelope.dotNet = true;
//		// Log.e(methodString, methodString);
//		if (params.length % 2 == 1) {
//			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
//			return "-1";
//		}
//		PropertyInfo propertyInfo = null;
//		for (int i = 0; i < params.length; i++) {
//			if (i % 2 == 0) {
//				if (params[i] != null) {
//					propertyInfo = new PropertyInfo();
//					propertyInfo.setName(params[i]);
//				}
//			} else if (propertyInfo != null) {
//				propertyInfo.setValue(params[i] == null ? "" : params[i]);
//				rpc.addProperty(propertyInfo);
//				propertyInfo = null;
//			}
//		}
//		envelope.setOutputSoapObject(rpc);
//		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request_url_asmx));
//		ht.debug = true;
//		String info = null;
//		try {
//			ht.call(context.getResources().getString(R.string.request_url) + methodString, envelope);
//			if (envelope.bodyIn != null) {
//				if (envelope.bodyIn.getClass() == SoapObject.class) {
//					SoapObject soapObject = (SoapObject) envelope.bodyIn;
//					info = soapObject.getProperty(0).toString();
//				} else {
//					// 错误！可能原因：错误的方法名或参数...
//					info = "-1";
//				}
//			} else {
//				// 网络错误，无法连接数据库!
//				info = "-2";
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			Log.e("Soap error:", e.toString());
//		} catch (XmlPullParserException e) {
//			e.printStackTrace();
//		} finally {
//
//		}
//		return info;
//	}


	//	MR测试
	public static String getResponseMR173(Context context, String methodString, String... params) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request173_namespace), methodString);


		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		// Log.e(methodString, methodString);
		if (params.length % 2 == 1) {
			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
			return "-1";
		}
		PropertyInfo propertyInfo = null;
		for (int i = 0; i < params.length; i++) {
			if (i % 2 == 0) {
				if (params[i] != null) {
					propertyInfo = new PropertyInfo();
					propertyInfo.setName(params[i]);
				}
			} else if (propertyInfo != null) {
				propertyInfo.setValue(params[i] == null ? "" : params[i]);
				rpc.addProperty(propertyInfo);
				propertyInfo = null;
			}
		}
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request173_url_asmx));
		ht.debug = true;
		String info = null;
		try {
			ht.call(context.getResources().getString(R.string.request173_url) + methodString, envelope);
			if (envelope.bodyIn != null) {
				if (envelope.bodyIn.getClass() == SoapObject.class) {
					SoapObject soapObject = (SoapObject) envelope.bodyIn;
					info = soapObject.getProperty(0).toString();
				} else {
					// 错误！可能原因：错误的方法名或参数...
					info = "-1";
				}
			} else {
				// 网络错误，无法连接数据库!
				info = "-2";
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Soap error:", e.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return info;
	}

	/**
	 * 请求服务器170
	 */
	public static String getResponse170(Context context, String methodString, String... params) {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject rpc = new SoapObject(context.getResources().getString(R.string.request_namespace170), methodString);
		// SoapObject rpc = new SoapObject("http://steve.cw169.4everdns.com/",
		// methodString);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		// Log.e(methodString, methodString);
		if (params.length % 2 == 1) {
			Toast.makeText(context, "错误的参数队列来自函数：Commons.getResponse()", Toast.LENGTH_SHORT).show();
			return "-1";
		}
		PropertyInfo propertyInfo = null;
		for (int i = 0; i < params.length; i++) {
			if (i % 2 == 0) {
				if (params[i] != null) {
					propertyInfo = new PropertyInfo();
					propertyInfo.setName(params[i]);
				}
			} else if (propertyInfo != null) {
				propertyInfo.setValue(params[i] == null ? "" : params[i]);
				rpc.addProperty(propertyInfo);
				propertyInfo = null;
			}
		}
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(context.getResources().getString(R.string.request_url_asmx170));
		ht.debug = true;
		String info = null;
		try {
			ht.call(context.getResources().getString(R.string.request_url170) + methodString, envelope);
			if (envelope.bodyIn != null) {
				if (envelope.bodyIn.getClass() == SoapObject.class) {
					SoapObject soapObject = (SoapObject) envelope.bodyIn;
					info = soapObject.getProperty(0).toString();
				} else {
					// 错误！可能原因：错误的方法名或参数...
					info = "-1";
				}
			} else {
				// 网络错误，无法连接数据库!
				info = "-2";
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Soap error:", e.toString());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}
		return info;
	}

	/**
	 * 获取当前日期
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());// 设置日期格式;
		return df.format(date);
	}

	public static String getCurrentDaterTimeSSS() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());// 设置日期格式;
		return df.format(date);
	}

	/**
	 * 检查网络连接状态
	 */

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "---状态---" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Bitmap compressImage(Context context, Bitmap image, int quality, int size) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		float widthPercent = 0, heightPercent = 0;
		widthPercent = 1280;
		heightPercent = 960;
		System.out.println("image.getWidth() = " + image.getWidth());
		System.out.println("image.getHeight()" + image.getHeight());
		if (image.getWidth() < image.getHeight()) {
			widthPercent = widthPercent + heightPercent;
			heightPercent = widthPercent - heightPercent;
			widthPercent = widthPercent - heightPercent;
		}

		System.out.println("widthPercent = " + widthPercent);
		System.out.println("heightPercent = " + heightPercent);

		widthPercent = (float) (1.0 * image.getWidth() / widthPercent);
		heightPercent = (float) (1.0 * image.getHeight() / heightPercent);
		System.out.println("widthPercent = " + widthPercent);
		System.out.println("heightPercent = " + heightPercent);
		options.inSampleSize = Math.round(Math.max(widthPercent, heightPercent));
		if (Math.max(widthPercent, heightPercent) > 1) {
			image = scaleBitmapAsWidthAndHeight(context, image,
					Math.round((image.getWidth() / Math.max(widthPercent, heightPercent))),
					Math.round((image.getHeight() / Math.max(widthPercent, heightPercent))));
		}
		// Log.e("", image.getWidth()+"-"+image.getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (quality <= 0) {
			quality = 100;
		}
		image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		if (size > 0) {
			System.out.println("baos.size = " + baos.size());
			System.out.println("size = " + size);
			while (baos.size() / 1024 > size) { // 循环判断如果压缩后图片是否大于size,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				if (quality <= 5) {
					break;
				} else {
					quality -= 5;// 每次都减少5
				}
			}
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		try {
			return BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);// 把ByteArrayInputStream数据生成图片
		} catch (Exception e) {
		} finally {
			try {
				baos.reset();
				baos.close();
				// image.recycle();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap scaleBitmapAsWidthAndHeight(Context context, Bitmap bitmap, int width, int height) {
		if (width <= 0 || height <= 0) {
			return bitmap;
		} else {
			Matrix matrix = new Matrix();
			float sx = 1.0f * width / bitmap.getWidth();
			BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
			float sy = 1.0f * height / bitmap.getHeight();
			matrix.postScale(sx, sy);
			try {
				return Bitmap.createBitmap(bitmap, 0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight(), matrix, true);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				bd.getBitmap().recycle();
				bitmap.recycle();
			}
			return null;
		}
	}

	// 存储进SD卡
	public static void saveBitmapFile(Bitmap bm, String filePath) {
		BufferedOutputStream bos = null;
		try {
			File dirFile = new File(filePath);
			// 检测图片是否存在
			if (dirFile.exists()) {
				dirFile.delete(); // 删除原图片
			}
			File myCaptureFile = new File(filePath);
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			// 100表示不进行压缩，70表示压缩率为30%
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		} catch (Exception e) {
		} finally {
			try {
				if (bos != null) {
					bos.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	public static void changeWindowBg(Activity act, float alpha) {

		WindowManager.LayoutParams lp = act.getWindow().getAttributes();
		// 解决6.0屏幕不变暗的问题
		act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		lp.alpha = alpha;
		act.getWindow().setAttributes(lp);

	}

	public static String getImageBase64(Bitmap bitmap, File file) {

		// Bitmap bp = Commons.compressImage(context, bitmap, 100, 640);
		// Commons.saveBitmapFile(bp, initFile().getPath());
		// // Commons.saveBitmapFile(bp, initFile().getParent());
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = null;

		NativeUtil.compressBitmap(bitmap, file.getPath());
		Bitmap bp = BitmapFactory.decodeFile(file.getPath());

		try {
			bStream = new ByteArrayOutputStream();
			bp.compress(CompressFormat.JPEG, 100, bStream);// 100表示不压缩
			byte[] bytes = bStream.toByteArray();
			string = Base64.encode(bytes);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (bStream != null) {
				try {
					bStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return string;
	}

	public static File initFile() {
		String fileName = UUID.randomUUID().toString();
		File tempFile = new File(Environment.getExternalStorageDirectory(), fileName + ".jpg");
		if (tempFile.exists()) {
			tempFile.delete();
		} else {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tempFile;
	}

	public static ProgressDialog getDialogLoadingPic(Context context, String title, int max,
													 final boolean finish) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(max);
		// 设置ProgressDialog 标题
		progressDialog.setTitle("司机操作");
		// 设置ProgressDialog 提示信息
		progressDialog.setMessage("正在上传图片...");
		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台上传", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
//				if (finish) {
					//((Activity) context).finish();
//				}
				//progressDialog.dismiss();
//                progressDialog.cancel();
			}
		});
		return progressDialog;
	}


	public static PopupWindow getPopWindowForUploadPic(final Activity act, View popView, OnClickListener listener){
		//View popView = LayoutInflater.from(act).inflate(R.layout.pop_uploadpic, null);
		popView.findViewById(R.id.btn_positive_upload).setOnClickListener(listener);
		popView.findViewById(R.id.btn_negative_upload).setOnClickListener(listener);
		PopupWindow popWindow = new PopupWindow(act);
		popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popWindow.setContentView(popView);
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popWindow.setOutsideTouchable(false);
		popWindow.setFocusable(true);
		popWindow.setAnimationStyle(R.style.popwindow_anim_style);
		popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				Commons.changeWindowBg(act, 1f);
			}
		});
		return popWindow;
	}

	public static PopupWindow getPopWindowForUpdateCYStatus(final Activity act, View popView, OnClickListener listener){
		//View popView = LayoutInflater.from(act).inflate(R.layout.pop_uploadpic, null);
		popView.findViewById(R.id.btn_positive_update).setOnClickListener(listener);
		popView.findViewById(R.id.btn_negative_update).setOnClickListener(listener);
		PopupWindow popWindow = new PopupWindow(act);
		popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popWindow.setContentView(popView);
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		popWindow.setOutsideTouchable(false);
		popWindow.setFocusable(true);
		popWindow.setAnimationStyle(R.style.popwindow_anim_style);
		popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				Commons.changeWindowBg(act, 1f);
			}
		});
		return popWindow;
	}



	public static GridImageAdapter.onAddPicClickListener initOnAddPicClickListener(final Activity act, final List<LocalMedia> selectList, final int reqestCode) {

		GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
			@Override
			public void onAddPicClick() {
				PictureSelector.create(act)
						.openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
						//.theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
						.maxSelectNum(4)// 最大图片选择数量
						.minSelectNum(1)// 最小选择数量
						.imageSpanCount(4)// 每行显示个数
						.selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE or PictureConfig.SINGLE
						.previewImage(false)// 是否可预览图片
						.isCamera(true)// 是否显示拍照按钮
						.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
						.enableCrop(false)// 是否裁剪
						.compress(true)// 是否压缩
						.synOrAsy(true)//同步true或异步false 压缩 默认同步
						.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
						.selectionMedia(selectList)// 是否传入已选图片
						.minimumCompressSize(100)// 小于100kb的图片不压缩
						.forResult(reqestCode);//结果回调onActivityResult code
			}
		};
		return onAddPicClickListener;
	}

	public static String getStr64ByImgPath(String imgPath){

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imgPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(fis);
		// 将Bitmap转换成字符串
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//读取图片到ByteArrayOutputStream
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //参数如果为100那么就不压缩
		byte[] bytes = baos.toByteArray();
		return Base64.encode(bytes);
	}

	public static byte[] getStr64ByImgPathBytes(String imgPath){

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imgPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(fis);
		// 将Bitmap转换成字符串
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//读取图片到ByteArrayOutputStream
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //参数如果为100那么就不压缩
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
}
