package ds.guang.majing.client.remote.dto.vo;

import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.dto.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 * simple - 简单的 朋友对象
 *
 * @author guangyong.deng
 * @date 2022-02-24 14:44
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FriendVo  {


    private String id;

    private GameUser user;


    /**
     * 好友
     */
    private List<Friend> friends;

}
