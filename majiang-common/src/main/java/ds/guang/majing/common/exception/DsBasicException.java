package ds.guang.majing.common.exception;

import ds.guang.majing.common.util.DsFoxUtil;

/**
 * Sa-Token框架内部逻辑发生错误抛出的异常 
 * (自定义此异常方便开发者在做全局异常处理时分辨异常类型)
 * 
 * @author kong
 *
 */
public class DsBasicException extends RuntimeException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130132L;

	/**
	 * 构建一个异常
	 *
	 * @param message 异常描述信息
	 */
	public DsBasicException(String message) {
		super(message);
	}

	/**
	 * 构建一个异常
	 *
	 * @param cause 异常对象
	 */
	public DsBasicException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构建一个异常
	 *
	 * @param message 异常信息
	 * @param cause 异常对象
	 */
	public DsBasicException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 如果flag==true，则抛出message异常 
	 * @param flag 标记
	 * @param message 异常信息 
	 */
	public static void throwBy(boolean flag, String message) {
		if(flag) {
			throw new DsBasicException(message);
		}
	}

	/**
	 * 如果value==null或者isEmpty，则抛出message异常 
	 * @param value 值 
	 * @param message 异常信息 
	 */
	public static void throwByNull(Object value, String message) {
		if(DsFoxUtil.isEmpty(value)) {
			throw new DsBasicException(message);
		}
	}
	
}
