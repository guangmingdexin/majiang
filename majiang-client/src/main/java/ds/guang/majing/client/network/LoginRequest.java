package ds.guang.majing.client.network;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.JsonUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;

import java.io.IOException;
import java.util.Objects;

/**
 * @author guangyong.deng
 * @date 2021-12-10 15:26
 */
public class LoginRequest extends Request {


    public LoginRequest(Object message) {
        super(message);
    }

    @Override
    protected void before(Runnable task) {
        // 根据
    }

    @Override
    protected DsResult after(String content) {

        DsMessage<DsResult<GameUser>> message = null;

        try {
           message = JsonUtil.getMapper().readValue(
                    content,
                    new TypeReference<DsMessage<DsResult<GameUser>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(message, "message is null");

        DsResult<GameUser> result = message.getData();

        if( result != null && result.success()) {
            // 这里应该还需要一个上下文解析器，用来保存用户基本信息和游戏信息
            GameUser data = (GameUser)JsonUtil.mapToObj(result.getData(), GameUser.class);
            Cache.getInstance().setObject(data.getUsername(), data, -1);
            return DsResult.data("token-123344");
        }

        return DsResult.error("登录错误！");
    }

}
