package com.wanyou.aframe.http.cache;

import com.wanyou.aframe.database.annotate.Id;
import com.wanyou.aframe.database.annotate.Table;

/**
 * 一个Cache的JavaBean类<br>
 * 
 * <b>说明</b> 这是一个私有类，开发者无法访问.为了API使用方便，对Field使用默认访问权限<br>
 * <b>创建时间</b> 2014-9-3
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
@Table(name = "wy_http_cache")
final class CacheBean{
	
	@Id(column = "id")
    private int id;
    long createTime; // 创建时间
    long effectiveTime; // 有效期
    long overdueTime; // 过期时间
    String url;
    String json;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public long getOverdueTime() {
        return overdueTime;
    }

    public void setOverdueTime(long overdueTime) {
        this.overdueTime = overdueTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
