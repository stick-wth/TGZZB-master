package com.tgzzb.cdc.bean;

public class DriverYwbhDdcode {
    private String billcode;
    private String ddcode;
    private String type;

    public DriverYwbhDdcode(String billcode, String ddcode, String type) {
        super();
        this.billcode = billcode;
        this.ddcode = ddcode;
        this.type = type;
    }

    public String getBillcode() {
        return billcode;
    }

    public void setBillcode(String billcode) {
        this.billcode = billcode;
    }

    public String getDdcode() {
        return ddcode;
    }

    public void setDdcode(String ddcode) {
        this.ddcode = ddcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
