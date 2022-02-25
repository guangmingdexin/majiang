package ds.guang.majing.client.remote.dto.vo;

import ds.guang.majing.common.game.dto.GameUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author guangyong.deng
 * @date 2022-02-25 10:57
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Friend {

    private String id;

    private GameUser friend;
}
