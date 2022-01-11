package ds.guang.majiang.server.network.buffer;

/**
 * @author guangyong.deng
 * @date 2022-01-10 9:57
 */
public interface MessageBuffer<E> {


    void addMessage(E e);


    void filterMessage();


    int getOffset();


    E getMessage();

    void setOffset(int offset);

}
