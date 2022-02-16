package ds.guang.majing.client.network;

import ds.guang.majing.common.game.message.DsResult;

/**
 * @author guangyong.deng
 * @date 2022-01-26 13:39
 */
public class EventRequest extends Request {

    public EventRequest(Object message) {
        super(message);
    }

    public EventRequest(Object message, String url) {
        super(message, url);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        System.out.println("content: " + content);
        return response(content);
    }
}
