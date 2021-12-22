package ds.guang.majing.common;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 返回的数据体
 *
 * @author guangyong.deng
 * @date 2021-11-17 14:37
 */
public class DsResult extends LinkedHashMap<String, Object> implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    public DsResult() {
    }

    public DsResult(int code, String msg, Object data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }

    /**
     * 获取code
     *
     * @return code
     */
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    /**
     * 获取msg
     *
     * @return msg
     */
    public String getMsg() {
        return (String) this.get("msg");
    }

    /**
     * 获取data
     *
     * @return data
     */
    public Object getData() {
        return (Object) this.get("data");
    }

    /**
     * 给code赋值，连缀风格
     *
     * @param code code
     * @return 对象自身
     */
    public DsResult setCode(int code) {
        this.put("code", code);
        return this;
    }

    /**
     * 给msg赋值，连缀风格
     *
     * @param msg msg
     * @return 对象自身
     */
    public DsResult setMsg(String msg) {
        this.put("msg", msg);
        return this;
    }

    /**
     * 给data赋值，连缀风格
     *
     * @param data data
     * @return 对象自身
     */
    public DsResult setData(Object data) {
        this.put("data", data);
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
        this.put(key, data);
        return this;
    }

    /**
     * 写入一个Map, 连缀风格
     *
     * @param map map
     * @return 对象自身
     */
    public DsResult setMap(Map<String, ?> map) {
        for (String key : map.keySet()) {
            this.put(key, map.get(key));
        }
        return this;
    }

    // 构建成功
    public static DsResult ok() {
        return new DsResult(DsConstant.CODE_SUCCESS, "ok", null);
    }

    public static DsResult ok(String msg) {
        return new DsResult(DsConstant.CODE_SUCCESS, msg, null);
    }

    public static DsResult code(int code) {
        return new DsResult(code, null, null);
    }

    public static DsResult data(Object data) {
        return new DsResult(DsConstant.CODE_SUCCESS, "ok", data);
    }

    // 构建失败
    public static DsResult error() {
        return new DsResult(DsConstant.CODE_ERROR, "error", null);
    }

    public static DsResult error(String msg) {
        return new DsResult(DsConstant.CODE_ERROR, msg, null);
    }

    // 构建指定状态码 
    public static DsResult get(int code, String msg, Object data) {
        return new DsResult(code, msg, data);
    }

    public static DsResult empty() {
        return new DsResult(-1, null, null);
    }

    public boolean isOk() {
        return this.get("code").equals(DsConstant.CODE_SUCCESS);
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
