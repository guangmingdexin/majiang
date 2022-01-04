package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.network.ResponseUtil;
import ds.guang.majing.common.*;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoop;

import java.nio.charset.StandardCharsets;

import static ds.guang.majing.common.DsConstant.*;

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
            DsMessage message = ClassUtil.convert(data, DsMessage.class);
            // 这里还需要将 data 重新反序列化
            User user = (User) JsonUtil.mapToObj(message.getData(), User.class);

            // 调用第三方权限验证框架进行账号验证
            // ...

            if("guangmingdexin".equals(user.getUsername()) && "123".equals(user.getPassword())) {
                System.out.println("登录成功！");
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

                // 绑定玩家 id 和 Channel
                String key = preUserChanelPrev(gameUser.getUserId());
                if (!cache.containsKey(key)) {
                    // 有没有办法可以通过当前 NioEventLoop 获取当前的 Channel
                    // 但是考虑这么一点，NioEventLoop 可能会有多个 Channel 绑定的 Channel
                    if(message.getAttrMap() != null && message.getAttrMap().containsKey(SYS_CONTEXT)) {
                        ChannelHandlerContext context = (ChannelHandlerContext) message.getAttrMap().get(SYS_CONTEXT);
                        cache.setObject(key, context, -1);
                        DsResult reply = DsResult.data(gameUser);
                        //  构造返回消息
                        DsMessage copyMessage = DsMessage
                                .copy(message)
                                .setData(reply)
                                .setAttrMap(null);

                        // 构造一个 http 的响应 即 httpResponse
                        context.writeAndFlush(ResponseUtil.response(copyMessage));
                        return reply;
                    }
                }else {
                    //
                    return DsResult.empty("该玩家已存在，不要重复登陆！");
                }

            }
            return DsResult.error("用户名或者密码错误！");
        });
    }


}
