package com.tgzzb.cdc.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tgzzb.cdc.CYCheckPicActivity;
import com.tgzzb.cdc.R;
import com.tgzzb.cdc.bean.CYItem;

import java.util.ArrayList;

public class CYExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<CYItem> datas;
    private SparseBooleanArray checkStatus;

    public CYExpListAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<CYItem>();
        checkStatus = new SparseBooleanArray();
    }

    public ArrayList<CYItem> addDatas(CYItem item) {
        if (!datas.contains(item)) {
            datas.add(item);
        }
        return datas;
    }

    public SparseBooleanArray getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(SparseBooleanArray checkStatus) {
        this.checkStatus = checkStatus;
    }

    public ArrayList<CYItem> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<CYItem> datas) {
        this.datas = datas;
        for (int i = 0; i < datas.size(); i++) {
            checkStatus.put(i, false);
        }
    }

    public void setNewDatas(ArrayList<CYItem> datas) {
        this.datas = datas;
        //判断新数据和就数据是否数目相同。
        if (this.datas.size() != datas.size()) {
            for (int i = 0; i < datas.size(); i++) {
                checkStatus.put(i, false);
            }
        }
    }

//	public HashMap<Integer, Boolean> getCheckStates() {
//		return checkStates;
//	}
//
//	public void setCheckStates(HashMap<Integer, Boolean> checkStates) {
//		this.checkStates = checkStates;
//	}

    public void checkAll() {// 更改全部状态为选中；
        for (int i = 0; i < checkStatus.size(); i++) {
            checkStatus.put(i, true);
        }
    }

    public void deleteAll() {// 更改全部状态为取消；
        for (int i = 0; i < checkStatus.size(); i++) {
            checkStatus.put(i, false);
        }
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return datas.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderF holderF = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cy_explistitem_father, null);
            holderF = new ViewHolderF();
            holderF.tv_qymc =  convertView.findViewById(R.id.tv_qymc);
            holderF.tv_bgdh =  convertView.findViewById(R.id.tv_bgdh);
            holderF.tv_xcbz =  convertView.findViewById(R.id.tv_xcbz);
            holderF.ckb = (CheckBox) convertView.findViewById(R.id.ckb);
            convertView.setTag(holderF);
        } else {
            holderF = (ViewHolderF) convertView.getTag();
        }
        holderF.setDatas(datas.get(groupPosition));

        holderF.ckb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox ck = (CheckBox) v;
                if (ck.isChecked()) {
                    checkStatus.put(groupPosition, true);
                } else {
                    checkStatus.put(groupPosition, false);

                }
            }
        });
        holderF.ckb.setChecked(checkStatus.get(groupPosition));// 每次都从记录中读取状态并设置;
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ViewHolderC holderC;
        if (convertView == null) {
            holderC = new ViewHolderC();
            convertView = LayoutInflater.from(context).inflate(R.layout.cy_explistitem_child, null);
            holderC.tv_qymc =  convertView.findViewById(R.id.tv_qymc);
            holderC.tv_bgdh =  convertView.findViewById(R.id.tv_bgdh);
            holderC.tv_tdh =  convertView.findViewById(R.id.tv_tdh);
            holderC.tv_tgfs =  convertView.findViewById(R.id.tv_tgfs);
            holderC.tv_jcgq =  convertView.findViewById(R.id.tv_jcgq);
            holderC.tv_ck =  convertView.findViewById(R.id.tv_ck);
            holderC.btn_checkpic = convertView.findViewById(R.id.btn_checkpic);
            holderC.tv_dlgsmc =  convertView.findViewById(R.id.tv_dlgsmc);
            holderC.tv_ywy =  convertView.findViewById(R.id.tv_ywy);
            holderC.tv_xcbz =  convertView.findViewById(R.id.tv_xcbz);
            holderC.tv_js =  convertView.findViewById(R.id.tv_js);
            holderC.tv_zl =  convertView.findViewById(R.id.tv_zl);
            holderC.tv_hdbh = convertView.findViewById(R.id.tv_hdbh);
            holderC.tv_hgcyzt = convertView.findViewById(R.id.tv_hgcyzt);
            holderC.tv_plancydate = convertView.findViewById(R.id.tv_plancydate);
            holderC.tv_reamrk = convertView.findViewById(R.id.tv_bzl);
            holderC.btn_checkpic = convertView.findViewById(R.id.btn_checkpic);
            convertView.setTag(holderC);
        } else {
            holderC = (ViewHolderC) convertView.getTag();
        }
        final CYItem item = datas.get(groupPosition);
        holderC.setDatas(item);
        holderC.btn_checkpic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CYCheckPicActivity.start(context,item.getBgdh());
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderF {

        TextView tv_qymc;
        TextView tv_bgdh;
        TextView tv_xcbz;
        CheckBox ckb;

        void setDatas(CYItem item) {
            tv_qymc.setText(item.getQymc());
            tv_bgdh.setText(item.getBgdh());
            tv_xcbz.setText(item.getXcbz());
        }
    }

    class ViewHolderC {
        TextView tv_qymc;
        TextView tv_bgdh;
        TextView tv_tdh;
        TextView tv_tgfs;
        TextView tv_jcgq;
        TextView tv_ck;
        TextView tv_ywy;
        TextView tv_xcbz;
        TextView tv_js;
        TextView tv_zl;
        TextView tv_dlgsmc;
        TextView tv_hgcyzt;
        TextView tv_plancydate;
        TextView tv_hdbh;
        TextView tv_reamrk;
        Button btn_checkpic;
        void setDatas(CYItem item) {
            tv_qymc.setText(item.getQymc());
            tv_bgdh.setText(item.getBgdh());
            tv_tdh.setText(item.getTdh());
            tv_tgfs.setText(item.getTgfs());
            tv_jcgq.setText(item.getJcgq());
            tv_ck.setText(item.getCk());
            tv_dlgsmc.setText(item.getDlgsmc());
            tv_ywy.setText(item.getYwy());
            tv_xcbz.setText(item.getXcbz());
            tv_js.setText(item.getJs());
            tv_zl.setText(item.getZl());
            tv_hgcyzt.setText(item.getHgcyzt());
            tv_hdbh.setText(item.getHdbh());
            tv_plancydate.setText(item.getPlancydate());
            tv_reamrk.setText(item.getReamrk());
        }
    }
}
