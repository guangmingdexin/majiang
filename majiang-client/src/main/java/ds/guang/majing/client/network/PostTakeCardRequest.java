package ds.guang.majing.client.network;

import ds.guang.majing.common.DsResult;

/**
 *
 * 摸牌请求
 *
 * @author asus
 */
public class PostTakeCardRequest extends Request {

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        // 每次摸牌请求，顺带可以判断，是否有特殊事件触发

        return null;
    }
}
