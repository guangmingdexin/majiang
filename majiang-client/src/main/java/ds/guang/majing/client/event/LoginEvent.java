package ds.guang.majing.client.event;


import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.dto.User;

/**
 * @author guangyong.deng
 * @date 2021-12-10 15:26
 */
public class LoginEvent extends AbstractEvent {

    private User user;

    public LoginEvent(User user) {
        super(DsConstant.USER_EVENT);
        this.user = user;
    }


    public User data() {
        return user;
    }
}
