package ds.guang.majing.client.remote.dto.ao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author guangyong.deng
 * @date 2022-02-15 14:23
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAo {

    /**
     * 账号
     */
    String username;
    /**
     * 密码(跟前端交互时不能使用明文)
     */
    String pwd;
}
