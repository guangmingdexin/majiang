package ds.guang.majing.client.network.idle;

import ds.guang.majing.client.network.Request;
import ds.guang.majing.common.timer.DsTimeout;
import ds.guang.majing.common.timer.DsTimerTask;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author guangyong.deng
 * @date 2022-02-17 10:30
 */
public class IdleHandler {

    private static final long MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1);

    /**
     * 客户端连接通道
     */
   // private final Socket socket;

    /**
     * 读空闲超时时间
     */
    private final long idleTimeNanos;

    @Setter
    private long lastReadTime;

    private boolean firstReaderIdleEvent;

    @Setter
    private boolean reading;

    @Setter
    private byte state; // 0 - none, 1 - initialized, 2 - destroyed


    public IdleHandler(long idleTime) {
        this(idleTime, TimeUnit.SECONDS);
    }

    public IdleHandler(long idleTime,  TimeUnit unit) {

        Objects.requireNonNull(unit, "unit");
        if (idleTime <= 0) {
            idleTimeNanos = 0;
        } else {
            idleTimeNanos = Math.max(unit.toNanos(idleTime), MIN_TIMEOUT_NANOS);
        }

    }


    public void initialize(Socket socket) {
        switch(this.state) {
            case 1:
            case 2:
                return;
            default:
                this.state = 1;

                this.lastReadTime = this.ticksInNanos();
                if (this.idleTimeNanos > 0L) {
                    WorkState.wheelTimer.newTimeout(new ReaderIdleTimeoutTask(socket), idleTimeNanos, TimeUnit.NANOSECONDS);
                }


        }
    }

    /**
     * This method is visible for testing!
     */
    long ticksInNanos() {
        return System.nanoTime();
    }


    /**
     *
     * 使用 netty 的心跳事件
     *
     * Returns a {@link IdleStateEvent}.
     */
    protected IdleStateEvent newIdleStateEvent(IdleState state, boolean first) {
        switch (state) {
            case ALL_IDLE:
                return first ? IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : IdleStateEvent.ALL_IDLE_STATE_EVENT;
            case READER_IDLE:
                return first ? IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : IdleStateEvent.READER_IDLE_STATE_EVENT;
            case WRITER_IDLE:
                return first ? IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : IdleStateEvent.WRITER_IDLE_STATE_EVENT;
            default:
                throw new IllegalArgumentException("Unhandled: state=" + state + ", first=" + first);
        }
    }


    /**
     *
     * 发送心跳包
     *
     * @param socket 通道
     * @param idleStateEvent 心跳事件
     */
    void sendIdleMsg(Socket socket, IdleStateEvent idleStateEvent) {

        // 1.构造一个心跳包

        // 2.获取连接（http ？ （相对于来说，成本较高了，但是使用 tcp 又需要自定义心跳处理解析器））

        if(socket != null) {
//            OutputStream outputStream = null;
//            try {
//                outputStream = socket.getOutputStream();
//                // 1.写入心跳包
//                outputStream.write(Integer.MAX_VALUE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//
//                if(outputStream != null) {
//                    try {
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
            SocketChannel channel = socket.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(32);
            byte[] data = new byte[32];
            for (int i = 0; i < data.length; i++) {
                data[i] = 1;
            }
            buf.put(data);
            buf.flip();
            try {

                channel.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        throw new IllegalArgumentException("获取通道失败：" + socket);
    }




    private final class ReaderIdleTimeoutTask implements DsTimerTask {

        private Socket socket;


        public ReaderIdleTimeoutTask(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run(DsTimeout timeout) throws Exception {

            // 每次读取操作，都会更新 lastReadTime 字段
            // 设置一个定时任务，间隔时间为 idleTimeNanos
            // 每次执行定时任务就会将计算当前时间与 lastReadTime 的间隔，如果大于 间隔时间，则触发 读空闲任务

            long nextDelay = idleTimeNanos;
            //不在读的过程中
            if (!reading) {
                //计算是否超时
                nextDelay -= ticksInNanos() - lastReadTime;
            }


            // 超时
            if(nextDelay <= 0) {
                // 将 秒转换成 unit
                long delay = idleTimeNanos;

                boolean first = firstReaderIdleEvent;
                firstReaderIdleEvent = false;

                // 发送心跳
                IdleStateEvent event = newIdleStateEvent(IdleState.READER_IDLE, first);
                sendIdleMsg(socket, event);

                // 进行下次检测
                WorkState.wheelTimer.newTimeout(this, delay, TimeUnit.NANOSECONDS);
            }else {
                WorkState.wheelTimer.newTimeout(this, nextDelay, TimeUnit.NANOSECONDS);
            }
        }

    }
}
