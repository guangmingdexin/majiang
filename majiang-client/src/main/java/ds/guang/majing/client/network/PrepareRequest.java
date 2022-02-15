package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;
import java.util.Objects;

/**
 * 匹配玩家
 * @author guangmingdexin
 */
public class PrepareRequest extends Request {


    public PrepareRequest(Object message) {
        super(message);
    }

    public PrepareRequest(Object message, String url) {
        super(message, url);
    }

    @Override
    protected void before(Runnable task) {}


    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<GameInfoResponse>> message = null;
        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<GameInfoResponse>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        if(message.getData().success()) {
            return message.getData();
        }

        return DsResult.error("请求失败");
    }

}
