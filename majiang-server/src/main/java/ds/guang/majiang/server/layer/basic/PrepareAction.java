package ds.guang.majiang.server.layer.basic;

import ds.guang.majiang.server.layer.StateMatchAction;
import ds.guang.majiang.server.room.RoomManager;
import ds.guang.majing.common.ClassUtil;
import ds.guang.majing.common.DsConstant;
import ds.guang.majing.common.DsMessage;
import ds.guang.majing.common.DsResult;
import ds.guang.majing.common.player.Player;
import ds.guang.majing.common.room.Room;
import ds.guang.majing.common.state.State;

import java.util.*;

import static ds.guang.majing.common.DsConstant.*;

/**
 * @author guangmingdexin
 */
@StateMatchAction(value = STATE_PREPARE_ID)
public class PrepareAction implements Action {


    @Override
    @SuppressWarnings("unchecked")
    public void handler(State state) {

        state.onEntry(data -> {

            System.out.println("进入 prepare 状态！" + data);
            // 随便洗牌
           // Room room = getRoomById(data);

            return null;
        });

        state.onEvent(EVENT_POST_HANDCARD_ID, data -> {
            Objects.requireNonNull(data, "data must be not empty!");
            Room room = getRoomById(data);
            Player player = room.findPlayerById(preGameUserInfoKey(((DsMessage)data).getRequestNo()));
            if(player != null) {
                return DsResult.data(player.getCards());
            }
            return DsResult.error("获取手牌失败！");
        });

    }


    /**
     * @param data
     * @return
     */
    private Room getRoomById(Object data) {

        // 获取房间管理器
        RoomManager roomManager = RoomManager.getInstance();
        // 获取 房间 id
        DsMessage message = ClassUtil.convert(data, DsMessage.class);

        return roomManager.get(DsConstant.preRoomInfoPrev(message.getRequestNo()));
    }


    /**
     *
     * 洗牌算法
     * 随机生成一个 1-n 的随机数，从最后一个数组开始
     * 不断交换 card[random]，card[i]
     *
     * @param cards 初始手牌
     */
    private List<Integer> shuffle(List<Integer> cards) {

        Random rand = new Random();

        List<Integer> copyCards = new ArrayList<>(cards);

        // 洗牌
        for (int i = copyCards.size() - 1; i >= 0 ; i--) {
            int randInd = rand.nextInt(i);
            // 交换
            Integer temp = copyCards.get(i);
            copyCards.set(i, copyCards.get(randInd));
            copyCards.set(randInd, temp);
        }

        return Collections.unmodifiableList(copyCards);

    }

}
