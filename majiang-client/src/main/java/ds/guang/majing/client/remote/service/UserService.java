package ds.guang.majing.client.remote.service;

import com.fasterxml.jackson.core.type.TypeReference;
import ds.guang.majing.client.network.Request;
import ds.guang.majing.client.remote.dto.ao.AccountAo;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.dto.vo.FriendVo;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.client.cache.Cache;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.message.DsResult;
import ds.guang.majing.common.util.DsConstant;
import ds.guang.majing.common.util.JsonUtil;

import java.io.IOException;
import java.util.Objects;

import static ds.guang.majing.common.util.DsConstant.REMOTE_LOGIN_URL;
import static ds.guang.majing.common.util.DsConstant.REMOTE_USER_URL;

/**
 *
 * 不保证线程安全，所以必须在单线程内使用
 *
 * @author guangyong.deng
 * @date 2022-02-15 15:05
 */
public class UserService implements IUserService {


    @Override
    public GameUser getOne(UserQueryAo query) {
        // 1.创建一个连接，发起请求
        Request request = new Request(query, REMOTE_LOGIN_URL + "ds-user/user") {

            @Override
            protected void before(Runnable task) {

            }

            @Override
            protected DsResult after(String content) {

                DsResult<GameUser> responseVo = null;

                try {
                    responseVo = JsonUtil.getMapper().readValue(
                            content,
                            new TypeReference<DsResult<GameUser>>() {});
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Objects.requireNonNull(responseVo, "response is null");

                if(responseVo.success()) {
                    // 这里应该还需要一个上下文解析器，用来保存用户基本信息和游戏信息
                    GameUser data = responseVo.getData();
                    Cache.getInstance().setObject("User-Session:", data, -1);
                    return responseVo;
                }else {

                    System.out.println("调用远程用户服务获取失败: " + responseVo);

                }
                return responseVo;
            }
        };
        return (GameUser) request.execute(null).getData();
    }

    @Override
    public DsResult<LoginVo> login(AccountAo accountAo) {

        Request request = new Request(accountAo, REMOTE_LOGIN_URL + "ds-auth/login") {
            @Override
            protected void before(Runnable task) {

            }

            @Override
            protected DsResult after(String content) {

                DsResult<LoginVo> responseVo = null;

                try {
                    responseVo = JsonUtil.getMapper().readValue(
                            content,
                            new TypeReference<DsResult<LoginVo>>() {});
                    System.out.println("responseVo: " + responseVo);
                } catch (IOException e) {
                     e.printStackTrace();
                    System.out.println("登陆失败： " + e.getMessage());
                }

                Objects.requireNonNull(responseVo, "response is null");

                if(responseVo.success()) {
                    // 这里应该还需要一个上下文解析器，用来保存用户基本信息和游戏信息
                    LoginVo data = responseVo.getData();
                    System.out.println("登录成功：" + data);
                    Cache.getInstance().setObject("User-Token:", data, -1);
                    return responseVo;
                }

                return responseVo;
            }
        };
        return request.execute(null);
    }


    @Override
    public DsResult<FriendVo> getFriends(UserQueryAo queryAo) {

        Request request = new Request(queryAo, REMOTE_LOGIN_URL + "ds-friend/friends") {

            @Override
            protected void before(Runnable task) {

            }

            @Override
            protected DsResult after(String content) {

                DsResult<FriendVo> responseVo = null;

                try {
                    responseVo = JsonUtil.getMapper().readValue(
                            content,
                            new TypeReference<DsResult<FriendVo>>() {});
                    System.out.println("responseVo: " + responseVo);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("失败： " + e.getMessage());
                }


                return responseVo;
            }
        };


        return request.execute(null);

    }
}
