package ds.guang.majing.client.javafx.component;

/**
 * @author guangyong.deng
 * @date 2022-02-17 8:59
 */
public interface Layout {


    /**
     *
     * 界面保存信息，用于跳转获取信息
     *
     * @param name key
     * @param value value
     */
    void set(String name, Object value);


    /**
     *
     * 获取全局值
     *
     * @param name key
     * @return value
     */
    Object get(String name);
}
