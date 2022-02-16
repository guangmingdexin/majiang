package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * @author guangyong.deng
 * @date 2022-01-19 15:59
 */
public class WaitRequest extends Request {

    public WaitRequest(Object message) {
        super(message);
    }

    public WaitRequest(Object message, String url) {
        super(message, url);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<GameInfoResponse>> message = null;

        try {
            message = JsonUtil.getMapper().readValue(content,
                    new TypeReference<DsMessage<DsResult<GameInfoResponse>>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        // 1.判断是否是接受到了其他玩家的手牌

        return message.getData();
    }
}
