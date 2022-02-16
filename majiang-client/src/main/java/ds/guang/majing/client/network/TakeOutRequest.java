package ds.guang.majing.client.network;

import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;

/**
 * @author guangyong.deng
 * @date 2022-01-18 17:04
 */
public class TakeOutRequest extends Request {

    public TakeOutRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        return response(content);
    }
}
