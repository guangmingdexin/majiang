package ds.guang.majiang.server.network;

import ds.guang.majiang.server.network.buffer.MessageBuffer;
import ds.guang.majiang.server.network.session.Session;
import io.netty.channel.Channel;

/**
 * @author guangmingdexin
 */
public class SimpleSyncState implements SyncState {

    private Session session;

    public SimpleSyncState(Session session) {
        this.session = session;
    }

    @Override
    public boolean syncState(String userId) {

        int requestOffset = session.getOffset();
        Channel context = session.getContext(userId);
        MessageBuffer buffer = session.getBuffer();
        int offset = buffer.getOffset();
        context.eventLoop().execute(() -> {

            for (int i = requestOffset + 1; i <= offset; i++) {

                // TODO: 这样做相当于客户端只能做单线程了
                context.writeAndFlush(ResponseUtil.response(buffer.getMessage()));

            }

        });

        return true;
        // 获取 Channel 发起一个 http 请求
    }

    @Override
    public boolean isNeedSyncState() {

        int requestOffset = session.getOffset();

        if(requestOffset < 0) {
            throw new IllegalArgumentException("非法的 offset");
        }
        // 1.通过 userId 获取 session，
        // 2.通过 session 获取 buffer
        // 3.通过 buffer 获取偏移量，并获取服务端保存的 offset
        int offset = session.getBuffer().getOffset();
        if(offset == requestOffset) {
            return false;
        }else if(offset > requestOffset) {
            return true;
        }
        throw new IllegalArgumentException("buffer 溢出了！");
    }
}
