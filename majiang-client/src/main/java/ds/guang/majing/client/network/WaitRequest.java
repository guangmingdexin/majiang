package ds.guang.majing.client.network;

import ds.guang.majing.common.game.message.DsResult;

/**
 * @author guangyong.deng
 * @date 2022-01-19 15:59
 */
public class WaitRequest extends Request {

    public WaitRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        return DsResult.empty("等待");
    }
}
