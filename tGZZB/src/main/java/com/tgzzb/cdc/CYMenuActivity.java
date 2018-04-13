package com.tgzzb.cdc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tgzzb.cdc.adapter.CYMenuRecyclerAdapter;
import com.tgzzb.cdc.utils.Commons;

import java.util.ArrayList;

public class CYMenuActivity extends Activity implements View.OnClickListener{


    private Context context;
    private ArrayList<String> cyPermissions;

    public static void start(Context context,ArrayList<String> permissions) {
        Intent starter = new Intent(context, CYMenuActivity.class);
        starter.putExtra("permissions",permissions);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cymenu);
        context = this;
        cyPermissions = getIntent().getExtras().getStringArrayList("permissions");
        findViewById(R.id.title_left).setOnClickListener(this);
        Commons.setTitle(CYMenuActivity.this,"查验模块");


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        CYMenuRecyclerAdapter adapter = new CYMenuRecyclerAdapter(this);
        adapter.setOnItemClickListener(new CYMenuRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CYMenuRecyclerAdapter adapter, View view, int position) {
                TextView tv = view.findViewById(R.id.text);
                String menu = tv.getText().toString();
                if(TextUtils.equals(tv.getText().toString(),"总卡口")){
                    CYActivity.start(context,"APP_HGCY_ZKK","总卡口");
                }
                if(TextUtils.equals(tv.getText().toString(),"西卡口")){
                    CYActivity.start(context,"APP_HGCY_XKK","西卡口");
                }
                if(TextUtils.equals(tv.getText().toString(),"上海口")){
                    CYActivity.start(context,"APP_HGCY_SH","上海口");
                }
            }
        });
        recyclerView.setAdapter(adapter);

        int len = cyPermissions.size();
        String []titles = new String[len];
        int []images = new int[len];
        for(int i = 0;i<len;i++){
            if(TextUtils.equals((cyPermissions.get(i)),"APP_HGCY_ZKK")){
                titles[i] = "总卡口";
                images[i] = R.drawable.cycz_zkk;
            }
            if(TextUtils.equals((cyPermissions.get(i)),"APP_HGCY_XKK")){
                titles[i] = "西卡口";
                images[i] = R.drawable.cycz_xkk;
            }
            if(TextUtils.equals((cyPermissions.get(i)),"APP_HGCY_SH")){
                titles[i] = "上海口";
                images[i] = R.drawable.cycz_shk;
            }
        }
        adapter.setData(titles,images);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
                default:
                    break;
        }
    }
}
