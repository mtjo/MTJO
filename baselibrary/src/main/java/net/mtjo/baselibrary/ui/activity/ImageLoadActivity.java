package com.wanyou.aframe.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;

import com.wanyou.aframe.bitmap.ImageLoader;
import com.wanyou.wylibrary.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ImageLoadActivity extends Activity {
	private GridView gridview;
	private ArrayList<String> dataList = new ArrayList<String>();
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	private ImageLoadAdapter gridImageAdapter;
	private static int MAX_SELECT = 20;//最多只能选择5张相片
	
	private TextView return_tv, titile_tv, cancle_tv, sure_tv;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageload);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(null != bundle && bundle.containsKey("pathlist")&& bundle.getSerializable("pathlist") != null){
			dataList = (ArrayList<String>) bundle.getSerializable("pathlist");
		}
		titile_tv = (TextView) findViewById(R.id.imageload_title);
		if(null != bundle && bundle.containsKey("title")&& bundle.getString("title") != null){
			if(null != titile_tv)titile_tv.setText(bundle.getString("title"));
		}
		initView();
		initListener();
	}

	private void initView() {
		gridview = (GridView) findViewById(R.id.gridview);
		gridImageAdapter = new ImageLoadAdapter(this, dataList,selectedDataList);
		gridview.setAdapter(gridImageAdapter);
		
		return_tv = (TextView) findViewById(R.id.imageload_return);
		cancle_tv = (TextView) findViewById(R.id.imageload_cancle);
		sure_tv = (TextView) findViewById(R.id.imageload_sure);
	}
    
	private void initListener() {
		gridImageAdapter.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, final String path,boolean isChecked) {
				if(isChecked)
					if(selectedDataList.size()>=MAX_SELECT){
						toggleButton.setChecked(false);
						Toast.makeText(ImageLoadActivity.this, "最多只能选择"+MAX_SELECT+"张图片", Toast.LENGTH_SHORT).show();
						return;
					}
				
				if(isChecked){
					selectedDataList.add(path);
				}else{
					for(int i =0;i<selectedDataList.size();i++){
						if(selectedDataList.get(i).equals(path)){
							selectedDataList.remove(i);
							return;
						}
					}
				}
			}
		});
		
		if(null != return_tv)return_tv.setOnClickListener(clickListener);
		if(null != cancle_tv)cancle_tv.setOnClickListener(clickListener);
		if(null != sure_tv)sure_tv.setOnClickListener(clickListener);
	}
	
	private OnClickListener clickListener=new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.imageload_return)
			{
				setResult(10);
				//返回相册
				finish();
			}else if(v.getId() == R.id.imageload_cancle){
				setResult(20);
				//取消
				finish();
			}else if(v.getId() == R.id.imageload_sure){
				if(selectedDataList.size() <= 0)
					Toast.makeText(ImageLoadActivity.this, "请选择图片...", Toast.LENGTH_SHORT).show();
				else{
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("selectlist", (Serializable)selectedDataList);
					intent.putExtras(bundle);
					setResult(30,intent);
					//确定
					finish();
				}
			}
			
		}
	};
	
	class ImageLoadAdapter extends BaseAdapter implements OnClickListener {
		private Context mContext;
		private ArrayList<String> dataList;
		private ArrayList<String> selectedDataList;
		private ImageLoader mImageLoader;
		
		public ImageLoadAdapter(Context c, ArrayList<String> dataList,
				ArrayList<String> selectedDataList) {

			mContext = c;
			this.dataList = dataList;
			this.selectedDataList = selectedDataList;
			mImageLoader = ImageLoader.getInstance(mContext);
		}
		
		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.night_item, parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.image_view);
				viewHolder.toggleButton = (ToggleButton) convertView
						.findViewById(R.id.toggle_button);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String path = null;
			if (dataList != null && dataList.size() > position)
				path = dataList.get(position);
			if(null != path)
				mImageLoader.displayImage(viewHolder.imageView,path,null);
			
			viewHolder.toggleButton.setTag(position);
			viewHolder.toggleButton.setOnClickListener(this);
			if (isInSelectedDataList(path)) {
				viewHolder.toggleButton.setChecked(true);
			} else {
				viewHolder.toggleButton.setChecked(false);
			}

			return convertView;
		}
		
		private boolean isInSelectedDataList(String selectedString) {
			for (int i = 0; i < selectedDataList.size(); i++) {
				if (selectedDataList.get(i).equals(selectedString)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 存放列表项控件句柄
		 */
		private class ViewHolder {
			public ImageView imageView;
			public ToggleButton toggleButton;
		}
		
		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null
						&& position < dataList.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position,
							dataList.get(position), toggleButton.isChecked());
				}
			}
		}

		private OnItemClickListener mOnItemClickListener;

		public void setOnItemClickListener(OnItemClickListener l) {
			mOnItemClickListener = l;
		}
	}
	
	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position, String path,
				boolean isChecked);
	}
}
