package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.client.entity.ClientFourRoom;
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
public class PostTakeCardRequest extends Request {

    public PostTakeCardRequest(Object message) {
        super(message);
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
            return DsResult.error("摸牌失败！");
        }

        DsResult<GameInfoResponse> data = message.getData();

        System.out.println("take card: " + data.getData());
        return data;
    }
}
