package com.wanyou.aframe.ui.widget.xlistview;

public interface DataQueryInerface {
	public void onRefresh(int pageNo,int pageSize);
    public void onLoadMore(int pageNo,int pageSize);
}
