package ds.guang.majing.client.event;


import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.cache.Cache;
import ds.guang.majing.common.dto.GameUser;

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
    protected DsResult after(DsResult result) {

        if( result != null && result.isOk()) {
            // 这里应该还需要一个上下文解析器，用来保存用户基本信息和游戏信息
            GameUser data = (GameUser)JsonUtil.mapToObj(result.getData(), GameUser.class);
            Cache.getInstance().setObject(data.getUsername(), data, -1);
            System.out.println("登陆成功！");
            System.out.println("登录成功！缓存用户信息！" + data);
            return DsResult.data("token-123344");
        }

        return DsResult.error("登录错误！");
    }

}
