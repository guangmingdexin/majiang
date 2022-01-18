package ds.guang.majiang.server.network.buffer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;

/**
 * 
 * 当缓冲区满了之后，进行一次连接判断，如果连接成功，则将缓冲区消息全部发送出去
 * 否则保存一个玩家状态机副本
 * 
 * @author guangyong.deng
 * @date 2022-01-10 9:58
 */
public class DsMessageBuffer<E> implements MessageBuffer<E> {

    private MpscArrayQueue<E> buffer;

    private int maxCapacity;

    private volatile int offset;

    public DsMessageBuffer() {
        this.maxCapacity = 16;
        this.buffer = new MpscArrayQueue<>(maxCapacity);
    }


    @Override
    public void addMessage(E message,
                           OverflowHandler overflowHandler) {
        // 获取 buffer 的 pIndex

        if(!buffer.offer(message)) {
            overflowHandler.handler(this);
        }
    }

    @Override
    public void filterMessage() {

    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public E getMessage() {
        return buffer.poll();
    }
    

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }


}
