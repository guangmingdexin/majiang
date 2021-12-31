package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majing.common.*;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.dto.GameUser;
import ds.guang.majing.common.dto.User;
import ds.guang.majing.common.state.AbstractStateImpl;
import ds.guang.majing.common.state.State;

import static ds.guang.majing.common.DsConstant.EVENT_LOGIN_ID;
import static ds.guang.majing.common.DsConstant.STATE_LOGIN_ID;
import static ds.guang.majing.common.DsConstant.STATE_PLATFORM_ID;

/**
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
                        .setUserId(StringUtil.generateIdUUid())
                        .setUsername(user.getUsername())
                        .setScore(0)
                        .setVip(6);

                // 缓存用户信息
                Cache cache = Cache.getInstance();

                // 一次登陆缓存 5分钟
                cache.setObject(DsConstant.preGameUserInfoKey(gameUser.getUserId()),
                        gameUser, -1);

                return DsResult.data(gameUser);
            }
            return DsResult.error("用户名或者密码错误！");
        });
    }


}
