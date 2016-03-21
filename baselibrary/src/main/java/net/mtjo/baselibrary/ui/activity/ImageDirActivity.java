package com.wanyou.aframe.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wanyou.aframe.bitmap.ImageLoader;
import com.wanyou.aframe.selectimg.ImageBean;
import com.wanyou.aframe.selectimg.ImageService;
import com.wanyou.wylibrary.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 扫描sdcard的图片，并选择图片
 * 要使用此类必须在在mainfest设置ImageDirActivity，和ImageLoadActivity
 * 其中ImageDirActivity为选择入口
 */
public class ImageDirActivity extends Activity {
	private ImageService imageService;
	private HashMap<String, List<String>> images;
	private List<String> folderPathList = new ArrayList<String>();
	private ListView listview;
	private Myadapter adapter;
	private final int MSG_LIST_IMG_OK = 0;
	private final int MUTIIMAGE_REQUEST_CODE = 100;
	
	/**
	 * 图片多选
	 */
	public static void MutiSelect(Activity act, int requestCode){
		if(null != act){
			Intent intent = new Intent(act,ImageDirActivity.class);
			act.startActivityForResult(intent, requestCode);
		}
	}
	
	/**
	 * 头像选择，返回截取头像，目前还没实现
	 */
	public static void HeadSelect(){
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagedir);

		imageService = new ImageService(this);

		allScan();
		
		TextView cancle_tv = (TextView)findViewById(R.id.imageload_cancle);
	    if(null != cancle_tv){
	    	cancle_tv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(v.getId() == R.id.imageload_cancle)
						finish();
				}
	    	});
	    }

		listview = (ListView) findViewById(R.id.listview);
		images = imageService.getImages(); 		// 获得所有的图片
		adapter = new Myadapter(this, subGroupOfImage(images));
		if (images.size() > 0) {
			listview.setAdapter(adapter);
		} else {
			Toast.makeText(ImageDirActivity.this, "没有图片", 1).show();
		}
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(ImageDirActivity.this,ImageLoadActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", folderPathList.get(arg2));
				bundle.putSerializable("pathlist", (Serializable) images.get(folderPathList.get(arg2)));
				intent.putExtras(bundle);
				startActivityForResult(intent, MUTIIMAGE_REQUEST_CODE);
			}
		});
		
		listview.setOnScrollListener(mScrollListener);
		
		
		//默认打开第一个相册
		if(folderPathList.size() > 0){
			Intent intent = new Intent(ImageDirActivity.this,ImageLoadActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("title", folderPathList.get(0));
			bundle.putSerializable("pathlist", (Serializable) images.get(folderPathList.get(0)));
			intent.putExtras(bundle);
			startActivityForResult(intent, MUTIIMAGE_REQUEST_CODE);
		}
	}
	
	/**
	 * 必须在查找前进行全盘的扫描，否则新加入的图片是无法得到显示的(加入对sd卡操作的权限)
	 */
	public void allScan() {
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,	Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			mImageBean.setFolderName(key);
			folderPathList.add(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));	// 获取该组的第一张图片
			list.add(mImageBean);
		}
		return list;
	}

	class Myadapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ImageBean> list;
		private boolean mBusy = false;
		private ImageLoader mImageLoader;
		
        public void setFlagBusy(boolean busy) {
                this.mBusy = busy;
        }

		public Myadapter(Context context, List<ImageBean> list) {
			this.list = list;
			this.inflater = LayoutInflater.from(context);
			mImageLoader = ImageLoader.getInstance(context);
		}
		
		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = null;

			ImageBean mImageBean = list.get(position);
			String path = mImageBean.getTopImagePath();

			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.image_dir_listitem, null);
				holder.imagePath = (TextView) view.findViewById(R.id.name);
				holder.imageCount = (TextView) view.findViewById(R.id.count);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.imagePath.setText(mImageBean.getFolderName());
			holder.imageCount.setText("("+mImageBean.getImageCounts()+")");
			if(!mBusy)
				mImageLoader.displayImage(holder.image, path, null);
			return view;
		}
	}

	class ViewHolder {
		private TextView imagePath; 	// 图片父路径
		private TextView imageCount; // 图片数量
		private ImageView image; 		// 图片
	}

	OnScrollListener mScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				adapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				adapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				adapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
    
    Handler _viewHandler = new Handler() {
    	@Override  
    	    public void handleMessage(Message msg) {  
    	        switch (msg.arg1) {  
    	        case MSG_LIST_IMG_OK:  
    	            // 更新UI  
    	            adapter.notifyDataSetChanged();  
    	            break;  
    	        }
    	        super.handleMessage(msg);  
    	    }  
    	};
    	/**
    	 * resultCode 10 返回相册
    	 * resultCode 20 取消相片选择
    	 * resultCode 30 确定选择
    	 * 确定时返回的是序列化的
    	 * bundle.putSerializable("selectlist", (Serializable)selectedDataList);
    	 */
    	@Override  
        protected void onActivityResult(int requestCode, int resultCode, Intent data)  
        {  
            //可以根据多个请求代码来作相应的操作  
    		if(requestCode != MUTIIMAGE_REQUEST_CODE)return;
    		
            if(20==resultCode)  
            {  
                finish();
            }else if(30 == resultCode){
            	setResult(RESULT_OK, data);
            	finish();
            }
        } 
}
