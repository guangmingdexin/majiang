package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;

/**
 *
 * 摸牌请求
 *
 * @author asus
 */
public class TakeCardRequest extends Request {

    public TakeCardRequest(Object message, String url) {
        super(message, url);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {
        // 每次摸牌请求，顺带可以判断，是否有特殊事件触发

        DsMessage<DsResult<GameInfoResponse>> message = null;

        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<GameInfoResponse>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(message == null) {
            // TODO : 这里应该抛异常
            return DsResult.error("摸牌失败！");
        }
        return message.getData();
    }
}
