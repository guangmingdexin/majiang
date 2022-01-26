package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author asus
 */
public class HandCardRequest extends Request {

    public HandCardRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {

    }

    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<GameInfoResponse>> message = null;

        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<GameInfoResponse>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        return message.getData();
    }


}
