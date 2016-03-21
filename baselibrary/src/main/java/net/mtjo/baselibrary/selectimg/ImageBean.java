package com.wanyou.aframe.selectimg;

public class ImageBean{
	private String topImagePath;	//文件夹的第一张图片路径
	private String folderName;	//文件夹名 
	private int imageCounts;	//文件夹中的图片数
	private String folderPath;	//文件夹路径
	
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public String getTopImagePath() {
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}
	
}