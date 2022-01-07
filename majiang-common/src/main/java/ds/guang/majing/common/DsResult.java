package ds.guang.majing.common;


import ds.guang.majing.common.state.Result;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 返回的数据体
 *
 * @author guangyong.deng
 * @date 2021-11-17 14:37
 */
public class DsResult<T>  implements Result, Serializable {

    int code;

    T data;

    String msg;

    Map<String, Object> att;

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    public DsResult() {

    }

    public DsResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 获取code
     *
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取msg
     *
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 获取data
     *
     * @return data
     */
    public T getData() {
        return data;
    }

    /**
     * 给code赋值，连缀风格
     *
     * @param code code
     * @return 对象自身
     */
    public DsResult setCode(int code) {
        this.code = code;
        return this;
    }

    /**
     * 给msg赋值，连缀风格
     *
     * @param msg msg
     * @return 对象自身
     */
    public DsResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 给data赋值，连缀风格
     *
     * @param data data
     * @return 对象自身
     */
    public DsResult setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 写入一个值 自定义key, 连缀风格
     *
     * @param key  key
     * @param data data
     * @return 对象自身
     */
    public DsResult set(String key, Object data) {
        if(att == null) {
            att = new LinkedHashMap<>(16, 0.75f, true);
        }
        att.put(key, data);
        return this;
    }

    /**
     * 写入一个Map, 连缀风格
     *
     * @param map map
     * @return 对象自身
     */
    public DsResult setMap(Map<String, ?> map) {
        if(att == null) {
            att = new LinkedHashMap<>(16, 0.75f, true);
        }
        att.putAll(map);
        return this;
    }

    // 构建成功
    public static DsResult ok() {
        return new DsResult<>(DsConstant.CODE_SUCCESS, "ok", null);
    }

    public static DsResult ok(String msg) {
        return new DsResult<>(DsConstant.CODE_SUCCESS, msg, null);
    }

    public static DsResult code(int code) {
        return new DsResult<>(code, null, null);
    }

    public static DsResult data(Object data) {
        return new DsResult<>(DsConstant.CODE_SUCCESS, "ok", data);
    }

    // 构建失败
    public static DsResult error() {
        return new DsResult<>(DsConstant.CODE_ERROR, "error", null);
    }

    public static DsResult error(String msg) {
        return new DsResult<>(DsConstant.CODE_ERROR, msg, null);
    }

    // 构建指定状态码 
    public static DsResult get(int code, String msg, Object data) {
        return new DsResult<>(code, msg, data);
    }

    public static DsResult empty(String msg) {
        return new DsResult<>(-1, msg, null);
    }

    public static DsResult wait(String msg) {
        return new DsResult<>(DsConstant.CODE_WAIT, msg, null);
    }

    public static DsResult empty() {
        return empty(null);
    }

    public boolean isOk() {
        return this.code == DsConstant.CODE_SUCCESS;
    }

    /**
     * 由于是异步编程所以很多时候，是先返回结果，所以需要等待
     * @return
     */
    public boolean isWait() {
        return this.code == DsConstant.CODE_WAIT;
    }


    @Override
    public boolean success() {
        return isOk();
    }

    @Override
    public String toString() {
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"msg\": \"" + this.getMsg() + "\""
                + ", \"data\": \"" + this.getData() + "\""
                + "}";
    }

}
