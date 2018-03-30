package com.example.zhujia.dx_dms.Data;

/**
 * Created by ZHUJIA on 2018/3/15.
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class BasicresourceData implements Serializable {
    private String str;
    private String text;
    private String pid;

    public BasicresourceData() {
    }

    public BasicresourceData(String str, String text,String pid) {
        super();
        this.str = str;
        this.text = text;
        this.pid=pid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 为什么要重写toString()呢？
     *
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return text;

    }


}
