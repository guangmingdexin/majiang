package ds.guang.majing.client.network;

import ds.guang.majing.common.DsResult;

/**
 *
 * 初始化操作
 * @author guangyong.deng
 * @date 2022-01-06 16:39
 */
public class InitRequest extends Request {

    public InitRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {
        // 1.
    }

    @Override
    protected DsResult after(DsResult result) {
        return null;
    }
}
