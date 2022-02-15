package ds.guang.majing.common.game.message;


import ds.guang.majing.common.util.DsConstant;
import ds.guang.majing.common.state.Result;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回的数据体
 *
 * @author guangyong.deng
 * @date 2021-11-17 14:37
 */
@Getter
@Setter
@Accessors(chain = true)
public class DsResult<T>  implements Result, Serializable {

    String code;

    T data;

    String msg;
    
    Map<String, Object> attrMap;


    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    public DsResult() {

    }

    public DsResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public DsResult setAttrMap(String key, Object value) {
        if(this.attrMap == null) {
            this.attrMap = new HashMap<>(8);
        }
        this.attrMap.put(key, value);
        return this;
    }

    public DsResult setAttrMap(Map<String, Object> attrMap) {
        if(this.attrMap == null) {
            this.attrMap = attrMap;
        }else {
            this.attrMap.putAll(attrMap);
        }
        return this;
    }



    // 构建成功
    public static DsResult ok() {
        return new DsResult<>(DsConstant.CODE_SUCCESS, "ok", null);
    }

    public static DsResult ok(String msg) {
        return new DsResult<>(DsConstant.CODE_SUCCESS, msg, null);
    }

    public static DsResult code(String code) {
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
    public static DsResult get(String code, String msg, Object data) {
        return new DsResult<>(code, msg, data);
    }

    public static DsResult empty(String msg) {
        return new DsResult<>("-1", msg, null);
    }

    public static DsResult wait(String msg) {
        return new DsResult<>(DsConstant.CODE_WAIT, msg, null);
    }

    public static DsResult empty() {
        return empty(null);
    }

    /**
     * 由于是异步编程所以很多时候，是先返回结果，所以需要等待
     * @return
     */
    public boolean waitState() {
        return this.code.equals(DsConstant.CODE_WAIT);
    }


    @Override
    public boolean success() {
        return this.code.equals(DsConstant.CODE_SUCCESS);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"code\":").append(code)
                .append(", \"data\":").append(data)
                .append(", \"msg\":").append(msg)
                .append(", \"attrMap\":").append(attrMap)
                .append('}');
        return sb.toString();
    }
}
