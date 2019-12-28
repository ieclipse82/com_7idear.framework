package com_7idear.framework.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com_7idear.framework.intface.IFormat;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.utils.TxtUtils;

import java.util.List;


/**
 * 路由实体类
 * @author iEclipse 2019/8/12
 * @description
 */
public class RouterEntity
        implements IRouter, IFormat, Parcelable {

    private String       link; //URI链接地址
    private List<String> eventList; //事件列表地址

    private Uri    linkUri; //URI
    private String scheme; //协议
    private String host; //标识
    private String path; //路径
    private String query; //参数
    private String ref; //来源

    public RouterEntity(String link) {
        init(link, null);
    }

    public RouterEntity(String link, List<String> eventList) {
        init(link, eventList);
    }

    public RouterEntity(Uri uri) {
        init(uri == null ? "" : uri.toString(), null);
    }

    public RouterEntity(Intent intent) {
        init(intent == null ? "" : TxtUtils.isEmpty(intent.getStringExtra(Params.LINK), ""), null);
    }

    protected RouterEntity(Parcel in) {
        init(in.readString(), in.createStringArrayList());
    }

    public static final Creator<RouterEntity> CREATOR = new Creator<RouterEntity>() {
        @Override
        public RouterEntity createFromParcel(Parcel in) {
            return new RouterEntity(in);
        }

        @Override
        public RouterEntity[] newArray(int size) {
            return new RouterEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeList(eventList);
    }

    private void init(String link, List<String> eventList) {
        this.link = TxtUtils.isEmpty(link, "");
        this.eventList = eventList;

        this.linkUri = Uri.parse(link);
        this.scheme = linkUri.getScheme();
        this.host = linkUri.getHost();
        this.path = linkUri.getPath();
        this.query = linkUri.getQuery();
        this.ref = linkUri.getQueryParameter(Params.REF);
    }

    public String getLink() {
        return link;
    }

    public List<String> getEventList() {
        return eventList;
    }

    public Uri getLinkUri() {
        return linkUri;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getRef() {
        return ref;
    }

    public String getParams(String key) {
        return linkUri == null ? "" : linkUri.getQueryParameter(key);
    }

    public void setEventList(List<String> eventList) {
        this.eventList = eventList;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return new LogEntity().appendLine(super.toString())
                              .append("linkUri", linkUri)
                              .append("scheme", scheme)
                              .append("host", host)
                              .append("path", path)
                              .append("query", query)
                              .append("ref", ref)
                              .append("eventList", eventList)
                              .toString();
    }

    //功能==============================

    /**
     * 页面后退打开新协议
     * @param context 环境对象
     * @return
     */
    public boolean openBackScheme(Context context) {
        return RouterUtils.getInstance()
                          .openLink(context, getParams(Params.BACK_SCHEME), null, null, 0);
    }

}
