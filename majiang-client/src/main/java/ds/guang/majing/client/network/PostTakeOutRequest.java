package ds.guang.majing.client.network;

import ds.guang.majing.common.game.message.DsResult;

/**
 * @author guangyong.deng
 * @date 2022-01-18 17:04
 */
public class PostTakeOutRequest extends Request {

    public PostTakeOutRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        return null;
    }
}
