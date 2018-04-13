package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tgzzb.cdc.bean.Mr_User;
import com.tgzzb.cdc.interfaces.MySingleClickListener;
import com.tgzzb.cdc.utils.Commons;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;

public class LoginActivity extends Activity {
	private CheckBox ckb_save;// 复选框-记住密码;
	private EditText actv_uName;// 用户名输入框;
	private EditText et_mima;// 密码输入框;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		context = this;
		initViews();
		initDatas();

	}

	private void initDatas() {
		Mr_User user = Mr_User.loginFromSharedPreferences();
		if (user != null) {
			actv_uName.setText(user.getMogilelogin());
			et_mima.setText(user.getPassword());
		}
	}

	private void initViews() {
		actv_uName = (EditText) findViewById(R.id.actv_uName);
		et_mima = (EditText) findViewById(R.id.et_miMa);
		ckb_save = (CheckBox) findViewById(R.id.ckb_remember);
		ckb_save.setChecked(true); // 设置复选框默认为选中状态;

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getResources().getString(R.string.app_name));
		findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

		findViewById(R.id.btn_login).setOnClickListener(new MySingleClickListener() {

			@Override
			protected void onSingleClick(View view) {
				try {
					connect();

					//context.startActivity(new Intent(context,ATestActivity.class));

				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void connect() throws NotFoundException, UnsupportedEncodingException {

		final String name = actv_uName.getText().toString();
		final String password = et_mima.getText().toString();

		
		//http://58.210.237.170:8083/Ifsweb/Service/IfsWebAPPWebservice2.asmx/GetLoginData
		//getResources().getString(R.string.GetLoginData)
		OkHttpUtils.post().url(getResources().getString(R.string.request_url_asmx170)+"/GetLoginData")
				.addParams("usernameurl", URLEncoder.encode(name, "UTF-8")).addParams("passwordurl", password).build()
				.execute(new StringCallback() {

					@Override
					public void onResponse(String arg0, int arg1) {
						String jsonData = Commons.parseXML(arg0);
						ArrayList<UserPermission> permissions = Commons.parseJsonList(jsonData,
								new TypeToken<ArrayList<UserPermission>>() {
						}.getType());
						if (permissions != null) {
							// 登录成功且至少有一个权限
							MyApp.setPermissions(permissions);
							MyApp.currentUser = new Mr_User(name, password);
							if (ckb_save.isChecked()) {
								MyApp.currentUser.loginToSharedPreferences(MyApp.currentUser);
							} else {
								MyApp.currentUser.clearSharedPreference("login");
							}
							MyApp.currentUser.writeToSharedPreferences(MyApp.currentUser);
							context.startActivity(new Intent(context, MainActivity.class));
							finish();
						} else {
							// 登录失败给出提示
							Commons.ShowToast(context, "登录失败。");
						}
					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						Commons.ShowToast(context, "登录超时，请您重新登录！");
					}
				});
	}

	class UserPermission implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String fun;

		public UserPermission(String fun) {
			super();
			this.fun = fun;
		}

		public String getFun() {
			return fun;
		}

		public void setFun(String fun) {
			this.fun = fun;
		}
	}

}
