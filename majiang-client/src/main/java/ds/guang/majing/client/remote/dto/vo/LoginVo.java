package ds.guang.majing.client.remote.dto.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author guangyong.deng
 * @date 2022-02-15 11:46
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginVo {

    /**
     * AccessToken
     */
    String token;
    /**
     * RefreshToken
     */
    String rtoken;

    /**
     * 用户中心用户ID
     */
    String uid;


}
