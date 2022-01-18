package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.room.ClientFourRoom;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.game.room.Room;

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

    @Override
    protected void before(Runnable task) {

    }


    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<Room>> message = null;
        try {
            message = JsonUtil.getMapper().readValue(content, new TypeReference<DsMessage<DsResult<ClientFourRoom>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        DsResult<Room> result = message.getData();

        if(result.success()) {
            // 获取房间信息
            System.out.println("room-info:" + result);
            return result;
        }

        return result;
    }

}
