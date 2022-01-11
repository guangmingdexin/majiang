package ds.guang.majiang.server.network.buffer;

import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;

/**
 * @author guangyong.deng
 * @date 2022-01-10 9:58
 */
public class DsMessageBuffer implements MessageBuffer<DsMessage<DsResult>> {

    private MpscArrayQueue<DsMessage<DsResult>> buffer;

    private int maxCapacity;

    private volatile int offset;


    public DsMessageBuffer() {
        this.maxCapacity = 16;
        this.buffer = new MpscArrayQueue<>(maxCapacity);

    }



    @Override
    public void addMessage(DsMessage<DsResult> message) {
        buffer.add(message);
    }

    @Override
    public void filterMessage() {

    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public DsMessage<DsResult> getMessage() {
        // TODO: buffer的数据结构不满足需求，应该是根据 offset 获取对应的 message
        return buffer.poll();
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;

    }
}
