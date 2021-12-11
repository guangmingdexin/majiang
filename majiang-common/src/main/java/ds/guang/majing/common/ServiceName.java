package ds.guang.majing.common;

import java.util.Objects;

/**
 * @author guangyong.deng
 * @date 2021-12-10 17:31
 */
public class ServiceName {


    private String serviceNo;

    private String serviceName;

    public ServiceName(String serviceNo, String serviceName) {
        this.serviceNo = serviceNo;
        this.serviceName = serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceName that = (ServiceName) o;
        return Objects.equals(serviceNo, that.serviceNo) &&
                Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceNo, serviceName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"serviceNo\":").append(serviceNo)
                .append(", \"serviceName\":").append(serviceName)
                .append('}');
        return sb.toString();
    }
}
