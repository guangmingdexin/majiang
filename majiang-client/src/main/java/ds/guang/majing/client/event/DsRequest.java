package ds.guang.majing.client.event;

import ds.guang.majing.common.DsFoxUtil;
import ds.guang.majing.common.exception.DsBasicException;

/**
 *
 */
public interface DsRequest {


    /**
     * 获取底层源对象
     * @return see note
     */
    Object getSource();


    /**
     * 在 [请求体] 里获取一个值
     * @param name 键
     * @return 值
     */
     String getParam(String name);

    /**
     * 在 [请求体] 里获取一个值，值为空时返回默认值
     * @param name 键
     * @param defaultValue 值为空时的默认值
     * @return 值
     */
     default String getParam(String name, String defaultValue) {
        String value = getParam(name);
        if(DsFoxUtil.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 检测提供的参数是否为指定值
     * @param name 键
     * @param value 值
     * @return 是否相等
     */
     default boolean isParam(String name, String value) {
        String paramValue = getParam(name);
        return DsFoxUtil.isNotEmpty(paramValue) && paramValue.equals(value);
    }

    /**
     * 检测请求是否提供了指定参数
     * @param name 参数名称
     * @return 是否提供
     */
     default boolean hasParam(String name) {
        return DsFoxUtil.isNotEmpty(getParam(name));
    }

    /**
     * 在 [请求体] 里获取一个值 （此值必须存在，否则抛出异常 ）
     * @param name 键
     * @return 参数值
     */
     default String getParamNotNull(String name) {
        String paramValue = getParam(name);
        if(DsFoxUtil.isEmpty(paramValue)) {
            throw new DsBasicException("缺少参数：" + name);
        }
        return paramValue;
    }


    /**
     * 在 [请求头] 里获取一个值
     * @param name 键
     * @return 值
     */
     String getHeader(String name);

    /**
     * 在 [请求头] 里获取一个值
     * @param name 键
     * @param defaultValue 值为空时的默认值
     * @return 值
     */
     default String getHeader(String name, String defaultValue) {
        String value = getHeader(name);
        if(DsFoxUtil.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 返回当前请求的url，不带query参数，例：http://xxx.com/test
     * @return see note
     */
     String getUrl();

    /**
     * 返回当前请求的类型
     * @return see note
     */
     String getMethod();
}
