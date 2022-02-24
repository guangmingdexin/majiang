package ds.guang.majing.client.game;

import ds.guang.majing.common.game.dto.GameUser;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.util.Algorithm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author guangyong.deng
 * @date 2022-01-07 16:26
 */
@Getter
@Setter
@ToString
public class ClientPlayer extends Player {


    /**
     * 麻将手牌对象
     */
    private List<ClientMaJiang> srcList;

    /**
     * 出牌
     */
    private Deque<ClientMaJiang> outList;

    public ClientPlayer() {
        this.srcList = new ArrayList<>();
        this.outList = new LinkedList<>();
    }

    @Override
    public Object getContext() {
        return null;
    }


    @Override
    public void eventHandler(int eventValue, int cardNum) {}

    // 有序插入
    public void addSrcCard(ClientMaJiang card) {

        // 第一步，获取相应下标

        int index = 0;

        for (ClientMaJiang c : srcList) {
            if(c.getValue() >= card.getValue()) {
                break;
            }else {
                index ++;
            }
        }

        srcList.add(index, card);
    }


}
