package ds.guang.majiang.server.network.buffer;

/**
 *
 * 该缓冲区服务于游戏进行阶段，作为客户端-服务端的一种数据同步方案
 *
 * @author guangyong.deng
 * @date 2022-01-10 9:57
 */
public interface MessageBuffer<E> {



    /**
     *
     * 向缓冲区添加消息
     *
     * @param e 消息
     * @param overflowHandler 当出现缓冲区溢出时的处理方案
     */
    void addMessage(E e, OverflowHandler overflowHandler);


    /**
     * 过滤缓冲区的消息，判断加入缓冲区的数据
     */
    void filterMessage();


    /**
     *
     * 获取当前缓冲区的偏移量
     *
     * @return
     */
    int getOffset();

    /**
     *
     * 获取缓冲区消息
     *
     * @return 消息
     */
    E getMessage();


    /**
     *
     * 设置缓冲区偏移量
     *
     * @param offset 偏移量
     */
    void setOffset(int offset);

}
