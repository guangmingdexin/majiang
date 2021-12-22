package ds.guang.majing.client.event;

/**
 * Response 包装类 
 * @author kong
 *
 */
public interface DsResponse {

	/**
	 * 获取底层源对象 
	 * @return see note 
	 */
	 Object getSource();
	
	/**
	 * 设置响应状态码
	 * @param sc 响应状态码
	 * @return 对象自身
	 */
	 DsResponse setStatus(int sc);
	
	/**
	 * 在响应头里写入一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	 DsResponse setHeader(String name, String value);

	/**
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	 DsResponse addHeader(String name, String value);
	
	/**
	 * 在响应头写入 [Server] 服务器名称 
	 * @param value 服务器名称  
	 * @return 对象自身 
	 */
	 default DsResponse setServer(String value) {
		return this.setHeader("Server", value);
	}

	/**
	 * 重定向 
	 * @param url 重定向地址 
	 * @return 任意值 
	 */
	 Object redirect(String url);
	
}
