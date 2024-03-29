package ds.guang.majing.common;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.UUID;

/**
 * @author guangyong.deng
 * @date 2021-12-10 17:24
 */
public class DsMessage implements Serializable {

    private String id;

    private String serviceNo;

    private String requestNo;

    private Object data;

    private String version;

    private LocalDateTime date;

    public DsMessage() {
    }

    public DsMessage(String id, String serviceNo, String requestNo,
                     Object data, String version, LocalDateTime date) {
        this.id = id;
        this.serviceNo = serviceNo;
        this.requestNo = requestNo;
        this.data = data;
        this.version = version;
        this.date = date;
    }

    public static DsMessage build(String serviceNo, String requestNo, Object data) {
        // id 都为随机8位字符串（默认状态）
        return new DsMessage(UUID.randomUUID().toString().substring(0, 8),
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

    public Object getData() {
        return data;
    }

    public DsMessage setData(Object data) {
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
