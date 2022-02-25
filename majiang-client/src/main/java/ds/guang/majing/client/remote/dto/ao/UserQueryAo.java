package ds.guang.majing.client.remote.dto.ao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author guangyong.deng
 * @date 2022-02-16 16:36
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserQueryAo implements Serializable {

    private String userId;
}
