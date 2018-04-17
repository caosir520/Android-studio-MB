package com.aspsine.irecyclerview.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分页信息 默认一页10条
 */
public class PageBean implements Parcelable {
    private int page = 1;
    private int size = 20;
    private boolean refresh=true;   //是否有新的数据

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public static Creator<PageBean> getCREATOR() {
        return CREATOR;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void loadPage() {
        page++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeInt(this.size);
        dest.writeByte(refresh ? (byte) 1 : (byte) 0);
    }

    public PageBean() {
    }

    protected PageBean(Parcel in) {
        this.page = in.readInt();
        this.size = in.readInt();
        this.refresh = in.readByte() != 0;
    }

    public static final Creator<PageBean> CREATOR = new Creator<PageBean>() {
        @Override
        public PageBean createFromParcel(Parcel source) {
            return new PageBean(source);
        }

        @Override
        public PageBean[] newArray(int size) {
            return new PageBean[size];
        }
    };
}