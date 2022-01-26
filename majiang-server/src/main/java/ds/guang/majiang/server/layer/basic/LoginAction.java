package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.Action;
import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.util.ResponseUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.dto.User;
import ds.guang.majing.common.game.message.DsMessage;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.game.message.GameInfoRequest;
import ds.guang.majing.common.game.message.GameInfoResponse;
import ds.guang.majing.common.state.State;
import ds.guang.majing.common.util.DsConstant;
import io.netty.channel.ChannelHandlerContext;

import static ds.guang.majing.common.util.DsConstant.*;

/**
 *
 * 类似于 Controller
 *
 * @author guangyong.deng
 * @date 2021-12-13 16:28
 */
@StateMatchAction(value = STATE_LOGIN_ID)
public class LoginAction implements Action {

    @SuppressWarnings("unchecked")
    @Override
    public void handler(State state) {
        // 注册事件就行了
        state.onEvent(EVENT_LOGIN_ID, STATE_PLATFORM_ID, data -> {
            // 查询远程数据数据库，比对数据
            DsMessage<GameInfoRequest> message = (DsMessage<GameInfoRequest>) data;

            // 这里还需要将 data 重新反序列化
            User user = message.getData().getUser();

            // 调用第三方权限验证框架进行账号验证
            // ...

            if("guangmingdexin".equals(user.getUsername()) && "123".equals(user.getPassword())) {
                GameUser gameUser = new GameUser()
                        .setUserId(message.getRequestNo())
                        .setUsername(user.getUsername())
                        .setScore(0)
                        .setVip(6);

                // 缓存用户信息
                Cache cache = Cache.getInstance();

                // 一次登陆缓存 5分钟
                cache.setObject(DsConstant.preGameUserInfoKey(gameUser.getUserId()),
                        gameUser, -1);


                // 有没有办法可以通过当前 NioEventLoop 获取当前的 Channel
                // 但是考虑这么一点，NioEventLoop 可能会有多个 Channel 绑定的 Channel
                if(message.getAttrMap() != null && message.getAttrMap().containsKey(SYS_CONTEXT)) {
                    ChannelHandlerContext context = (ChannelHandlerContext) message.getAttrMap().get(SYS_CONTEXT);
                    GameInfoResponse response = new GameInfoResponse().setGameUser(gameUser);
                    DsResult reply = DsResult.data(response);
                    //  构造返回消息
                    DsMessage<DsResult<GameInfoResponse>> respMessage = DsMessage.build(
                            message.getServiceNo(),
                            message.getRequestNo(),
                            reply
                            );

                    // 防止 后期 Context 无法回收
                    message.setAttrMap(SYS_CONTEXT, null);

                    // 构造一个 http 的响应 即 httpResponse
                    context.writeAndFlush(ResponseUtil.response(respMessage));
                    return reply;
                }
            }else {
                return DsResult.empty("该玩家已存在，不要重复登陆！");
            }

            return DsResult.error("用户名或者密码错误！");
        });
    }


}
