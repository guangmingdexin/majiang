package ds.guang.majing.client.network;

import ds.guang.majing.common.game.message.DsResult;

/**
 * @author guangyong.deng
 * @date 2022-01-18 17:04
 */
public class TakeOutRequest extends Request {

    public TakeOutRequest(Object message, String url) {
        super(message, url);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        return null;
    }
}
