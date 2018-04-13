package com.tgzzb.cdc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator - stick on 2018/2/28.
 * e-mail:253139409@qq.com
 */

public class CYItem implements Parcelable{
    /**
     * hgcyzt : 查验
     * ywy : 刘娟
     * bgdh : 1143123696
     * xcbz :
     * hdbh : SKI18020007
     * js : 4
     * zl : 401.00
     * qymc : 博世汽车
     * tgfs : 转关
     * jcgq : 虹桥机场
     * ck : 毅帆
     * dlgsmc : 上海鸿翔国际物流有限公司
     */
    private String qymc;//企业名称
    private String bgdh;//报关单号
    private String tdh;//报关单号
    private String ywy;//业务员
    private String tgfs;//通关方式
    private String jcgq;//机场港区
    private String ck;//仓库;
    private String dlgsmc;//代理公司名称
    private String xcbz;//现场备注
    private String js;//件数
    private String zl;//重量
    private String hgcyzt;//查验状态
    private String hdbh;//货代编号
    private String plancydate;
    private String reamrk;

    public CYItem(String qymc, String bgdh, String tdh, String ywy, String tgfs, String jcgq, String ck, String dlgsmc, String xcbz, String js, String zl, String hgcyzt, String hdbh,String plancydate,String reamrk) {
        this.qymc = qymc;
        this.tdh = tdh;
        this.bgdh = bgdh;
        this.ywy = ywy;
        this.tgfs = tgfs;
        this.jcgq = jcgq;
        this.ck = ck;
        this.dlgsmc = dlgsmc;
        this.xcbz = xcbz;
        this.js = js;
        this.zl = zl;
        this.hgcyzt = hgcyzt;
        this.hdbh = hdbh;
        this.plancydate = plancydate;
        this.reamrk = reamrk;
    }

    protected CYItem(Parcel in) {
        qymc = in.readString();
        bgdh = in.readString();
        tdh = in.readString();
        ywy = in.readString();
        tgfs = in.readString();
        jcgq = in.readString();
        ck = in.readString();
        dlgsmc = in.readString();
        xcbz = in.readString();
        js = in.readString();
        zl = in.readString();
        hgcyzt = in.readString();
        hdbh = in.readString();
        plancydate = in.readString();
        reamrk = in.readString();
    }

    public static final Creator<CYItem> CREATOR = new Creator<CYItem>() {
        @Override
        public CYItem createFromParcel(Parcel in) {
            return new CYItem(in);
        }

        @Override
        public CYItem[] newArray(int size) {
            return new CYItem[size];
        }
    };

    public String getHgcyzt() {
        return hgcyzt;
    }

    public void setHgcyzt(String hgcyzt) {
        this.hgcyzt = hgcyzt;
    }

    public String getYwy() {
        return ywy;
    }

    public void setYwy(String ywy) {
        this.ywy = ywy;
    }

    public String getBgdh() {
        return bgdh;
    }

    public void setBgdh(String bgdh) {
        this.bgdh = bgdh;
    }

    public String getTdh() {
        return tdh;
    }

    public void setTdh(String tdh) {
        this.tdh = tdh;
    }

    public String getXcbz() {
        return xcbz;
    }

    public void setXcbz(String xcbz) {
        this.xcbz = xcbz;
    }

    public String getHdbh() {
        return hdbh;
    }

    public void setHdbh(String hdbh) {
        this.hdbh = hdbh;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getZl() {
        return zl;
    }

    public void setZl(String zl) {
        this.zl = zl;
    }

    public String getQymc() {
        return qymc;
    }

    public void setQymc(String qymc) {
        this.qymc = qymc;
    }

    public String getTgfs() {
        return tgfs;
    }

    public void setTgfs(String tgfs) {
        this.tgfs = tgfs;
    }

    public String getJcgq() {
        return jcgq;
    }

    public void setJcgq(String jcgq) {
        this.jcgq = jcgq;
    }

    public String getCk() {
        return ck;
    }

    public void setCk(String ck) {
        this.ck = ck;
    }

    public String getDlgsmc() {
        return dlgsmc;
    }

    public void setDlgsmc(String dlgsmc) {
        this.dlgsmc = dlgsmc;
    }

    public String getPlancydate() {
        return plancydate;
    }

    public void setPlancydate(String plancydate) {
        this.plancydate = plancydate;
    }

    public String getReamrk() {
        return reamrk;
    }

    public void setReamrk(String reamrk) {
        this.reamrk = reamrk;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qymc);
        dest.writeString(bgdh);
        dest.writeString(tdh);
        dest.writeString(ywy);
        dest.writeString(tgfs);
        dest.writeString(jcgq);
        dest.writeString(ck);
        dest.writeString(dlgsmc);
        dest.writeString(xcbz);
        dest.writeString(js);
        dest.writeString(zl);
        dest.writeString(hgcyzt);
        dest.writeString(hdbh);
        dest.writeString(plancydate);
        dest.writeString(reamrk);
    }
}
