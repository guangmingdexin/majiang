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
 * @date 2022-01-27 15:07
 */
public class RoundRequest extends Request {


    public RoundRequest(Object message) {
        super(message);
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
