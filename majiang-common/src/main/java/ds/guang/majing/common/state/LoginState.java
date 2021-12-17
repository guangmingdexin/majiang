package ds.guang.majing.common.state;

import ds.guang.majing.common.ClassUtil;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.JsonUtil;
import ds.guang.majing.common.dto.User;

/**
 * @author guangyong.deng
 * @date 2021-12-13 16:28
 */
public class LoginState extends AbstractStateImpl<String, String, DsResult> {

    /**
     * @param id 状态ID
     */
    public LoginState(String id) {
        super(id);
    }

    @Override
    public State<String, String, DsResult> onEvent(String eventId, String nextState) {

        return onEvent(eventId, nextState, data -> {
            // 查询远程数据数据库，比对数据
            DsMessage message = ClassUtil.convert(data, DsMessage.class);
            // 这里还需要将 data 重新反序列化
            User user = (User) JsonUtil.mapToObj(message.getData(), User.class);
            if("guangmingdexin".equals(user.getUsername()) && "123".equals(user.getPassword())) {
                System.out.println("登录成功！");
                return DsResult.ok();
            }
            return DsResult.error();
        });
    }
}
