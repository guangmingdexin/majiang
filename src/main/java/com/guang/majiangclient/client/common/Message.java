package com.guang.majiangclient.client.common;

/**
 * @Param
 * @Author guangmingdexin
 * @See
 **/
public interface Message {

    /**
     * @param e 传输的对象
     * @return json 字符串
     */
    String encoder(Object e);

    /**
     * @param json json 字符串
     * @return 对象
     */
    Object decoder(String json);

}
