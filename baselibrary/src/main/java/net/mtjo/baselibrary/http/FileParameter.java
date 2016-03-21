package com.wanyou.aframe.http;

/**
 * @ClassName: FileParameter 
 * @Description: 文件暂存类容器
 */
public class FileParameter {
	private byte[] fileBytes;
	private String name;//请求参数名
	private String fileName;
	private String originalFileName; //绝对路径
	private long fileSize;
	private int dataType; //数据方式 1:byte[]数组，2:绝对路径
	private String contenttype =  "application/octetstream";
	
	public FileParameter(){}
	
	public byte[] getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
}
