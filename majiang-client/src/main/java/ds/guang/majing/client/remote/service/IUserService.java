package ds.guang.majing.client.remote.service;

import ds.guang.majing.client.remote.dto.ao.AccountAo;
import ds.guang.majing.client.remote.dto.ao.UserQueryAo;
import ds.guang.majing.client.remote.dto.vo.LoginVo;
import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.message.DsResult;

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
}
