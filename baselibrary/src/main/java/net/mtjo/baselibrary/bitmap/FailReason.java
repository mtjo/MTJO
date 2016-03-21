package com.wanyou.aframe.bitmap;

/**
 * 图片加载失败原因
 */
public class FailReason {
	private final FailType type;

	private final Throwable cause;

	public FailReason(FailType type, Throwable cause) {
		this.type = type;
		this.cause = cause;
	}

	public FailType getType() {
		return type;
	}

	public Throwable getCause() {
		return cause;
	}
	
	/**
	 * 图片加载异常类型
	 */
	public static enum FailType {
		IO_ERROR,
		DECODING_ERROR,
		NETWORK_DENIED,
		OUT_OF_MEMORY,
		UNKNOWN
	}
}
