package com.tencent.newhb.grabings.entity;

public class Plans {
    private String plan_id;
    private String name;
    private String price;
    private int active; //1:开通，0:未开通
    private String intro;
    private String words; //存储标记

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPlanId() {
        return plan_id;
    }

    public void setPlanId(String plan_id) {
        this.plan_id = plan_id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
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

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
