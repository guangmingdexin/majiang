package ds.guang.majing.common;

import java.time.LocalDate;

/**
 * @author guangyong.deng
 * @date 2021-12-10 17:24
 */
public class DsMessage  {

    private String id;

    private String serviceNo;

    private String requestNo;

    private Object data;

    private String version;

    private LocalDate date;

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
