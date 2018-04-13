package com.tgzzb.cdc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;

/**
 * Created by Administrator - stick on 2018/3/13.
 * e-mail:253139409@qq.com
 */

public class UpLoadPicProgressDialog {


    private int currentProgress = 0;
    private int totalCount = 0;

    private ProgressDialog progressDialog;

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void resetProgress(){
        this.currentProgress = 0;
    }

    public UpLoadPicProgressDialog(Context context, String title) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 标题
        progressDialog.setTitle(title);
        // 设置ProgressDialog 提示信息
        progressDialog.setMessage("正在上传图片...");
        // 设置ProgressDialog 的进度条是否不明确
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }


    public void setMaxCount(int maxCount){
        totalCount = maxCount;
        progressDialog.setMax(maxCount);
    }

    public void addProgress() {
        currentProgress = currentProgress + 1;
        progressDialog.setProgress(currentProgress);
    }

    public void uploadOver(View.OnClickListener listener) {
        progressDialog.setMessage("上传完成");
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(listener);
       // progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",listener);
    }

    public void uploadError(){
        progressDialog.setMessage("上传图片失败，请您稍后再试。");
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
    }

    public void show() {
        progressDialog.show();
    }
    public void dismiss() {
        progressDialog.dismiss();
        resetProgress();
    }
}
