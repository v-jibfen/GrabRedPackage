package com.tencent.newhb.grabings.entity;

/**
 * Created by MSI05 on 2017/2/24.
 */
public class Pay {

    private String outer_no;
    private String name;
    private String price;
    private String desc;
    private String pay_type;

    public String getOuter_no() {
        return outer_no;
    }

    public void setOuter_no(String outer_no) {
        this.outer_no = outer_no;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
