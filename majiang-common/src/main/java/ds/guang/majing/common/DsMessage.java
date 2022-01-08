package ds.guang.majing.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author guangyong.deng
 * @date 2021-12-10 17:24
 */
public class DsMessage<T> implements Serializable {

    private String id;

    private String serviceNo;

    /**
     * 此次活动生成的连接序号/ 后续所有的活动都会依赖这个请求号/类似于通话 token
     */
    private String requestNo;

    private T data;

    private String version;

    private LocalDateTime date;

    private Map<String, Object> attrMap;

    public DsMessage() {
    }

    public DsMessage(String id, String serviceNo, String requestNo,
                     T data, String version, LocalDateTime date) {
        this.id = id;
        this.serviceNo = serviceNo;
        this.requestNo = requestNo;
        this.data = data;
        this.version = version;
        this.date = date;
    }

    public static DsMessage build(String serviceNo, String requestNo, Object data) {
        // id 都为随机8位字符串（默认状态）
        return new DsMessage<>(UUID.randomUUID().toString().substring(0, 8),
                            serviceNo,
                            requestNo,
                            data,
                            DsConstant.VERSION,
                            LocalDateTime.now()
                );
    }


    public static DsMessage copy(DsMessage dsMessage) {

        return new DsMessage()
                .setId(dsMessage.getId())
                .setServiceNo(dsMessage.getServiceNo())
                .setRequestNo(dsMessage.getRequestNo())
                .setData(dsMessage.getData())
                .setVersion(dsMessage.getVersion())
                .setDate(dsMessage.getDate());

    }

    public static DsMessage copy(DsMessage dsMessage, String[] filedName, Object[] values) {

        Objects.requireNonNull(filedName, "filed don't null");

        DsMessage copy = copy(dsMessage);
        Class<? extends DsMessage> ds = copy.getClass();
        try {
            int index = 0;
            for (String name : filedName) {
                Field field = ds.getDeclaredField(name);
                if(values[index] != null) {
                    field.set(copy, values[index ++]);
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return copy;
    }


    public String getId() {
        return id;
    }

    public DsMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public DsMessage setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public DsMessage setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public T getData() {
        return data;
    }

    public DsMessage setData(T data) {
        this.data = data;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DsMessage setVersion(String version) {
        this.version = version;
        return this;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public DsMessage setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    public Map<String, Object> getAttrMap() {
        return attrMap;
    }

    public DsMessage setAttrMap(String key, Object value) {
        if(this.attrMap == null) {
            this.attrMap = new HashMap<>(8);
        }
        this.attrMap.put(key, value);
        return this;
    }

    public DsMessage setAttrMap(Map<String, Object> attrMap) {
        this.attrMap = attrMap;
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"id\":").append(id)
                .append(", \"serviceNo\":").append(serviceNo)
                .append(", \"requestNo\":").append(requestNo)
                .append(", \"data\":").append(data)
                .append(", \"version\":").append(version)
                .append(", \"date\":").append(date)
                .append('}');
        return sb.toString();
    }
}
