package ds.guang.majing.client.remote.service;

import ds.guang.majing.client.remote.dto.ao.AccountAo;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.dto.vo.FriendVo;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.message.DsResult;

import java.io.IOException;

/**
 * @author guangyong.deng
 * @date 2022-02-15 14:49
 */
public interface IUserService {


    /**
     *
     * 获取用户信息
     *
     * @param query 用户 id
     * @return 游戏用户信息
     */
    GameUser getOne(UserQueryAo query);


    /**
     *
     * 登录
     *
     * @param accountAo
     * @return
     */
    DsResult<LoginVo> login(AccountAo accountAo);


    /**
     *
     * 获取好友信息
     *
     * @param queryAo 查询条件
     * @return 好友列表
     */
    DsResult<FriendVo> getFriends(UserQueryAo queryAo);



}
