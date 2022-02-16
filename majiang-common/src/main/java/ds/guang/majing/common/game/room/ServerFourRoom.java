package ds.guang.majing.common.game.room;

import ds.guang.majing.common.game.card.GameEvent;
import ds.guang.majing.common.game.card.GameEventHandler;
import ds.guang.majing.common.game.machines.StateMachines;
import ds.guang.majing.common.game.player.GameState;
import ds.guang.majing.common.game.player.Player;
import ds.guang.majing.common.state.StateMachine;
import io.netty.channel.ChannelHandlerContext;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

import static ds.guang.majing.common.util.DsConstant.preRoomInfoPrev;
import static ds.guang.majing.common.util.DsConstant.preUserMachinekey;

/**
 * @author guangmingdexin
 */
@Accessors(chain = true)
@NoArgsConstructor
public class ServerFourRoom extends Room implements Serializable {



    @Override
    public boolean isCurAround(String userId) {
        if(curRoundIndex < 0) {
            return false;
        }
        Player player = super.players[curRoundIndex % playerCount];
        return player != null && player.id().equals(userId);
    }

    @Override
    public boolean check(String userId) {
        // 1.牌数不能超过 14 ，必须至少为 1
        // 2.牌数必须为奇数
        // 3.同一个数字不能出现超过 4 次
        Player p = this.findPlayerById(userId);
        List<Integer> cards = p.getCards();

        if(cards == null) {
            throw new NullPointerException("手牌为空");
        }

        int size = cards.size();
        if(size > maxHandCardNum && size < minHandCardNum)  {
            throw new IllegalArgumentException("play cards is error！");
        }

        if((size & (size - 1)) == 0) {
            throw  new IllegalArgumentException("play cards is error！");
        }
        return true;
    }

    @Override
    public void eventHandler(GameEvent event, int cardNum) {
        String id = event.getPlayId();
        int eventValue = event.getEvent();
        // 进行回合切换/状态切换
        eventHandler.nextRound(event, this);

        // 事件处理（状态处理）
        Player p = findPlayerById(id);
        p.eventHandler(eventValue, cardNum);

        //
        if(countHu >= playerCount - 1) {
            System.out.println("游戏结束了");
            eventHandler.over(this);
        }

    }


    public ServerFourRoom(int playerCount,
                          int initialCardNum,
                          int maxHandCardNum,
                          int minHandCardNum,
                          Player[] players,
                          GameEventHandler eventHandler) {
        super();
        super.id = UUID.randomUUID().toString().substring(0, 6);
        super.playerCount = playerCount;
        super.players = players;
        super.initialCardNum = initialCardNum;
        super.eventHandler = eventHandler;

        if(getInitialCards() == null || getInitialCards().isEmpty()) {
            // 还未进行初始化
            setInitialCards(shuffle(CARDS));
        }

        super.maxHandCardNum = maxHandCardNum;
        super.minHandCardNum = minHandCardNum;
        super.maxCardNum = CARDS.size();
    }


    /**
     *
     * 设置初始麻将
     */
    protected static final List<Integer> CARDS = initialCards();

    private static List<Integer> initialCards() {

        // 1.服务器生成 108 张棋牌（只可读）
        List<Integer> cards = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 9; j++) {
                for (int k = 1; k <= 4; k++) {
                    // 11 -- 万, 111 -- 条, 1111 -- 筒
                    cards.add((int) (j + Math.pow(10, i)));
                }
            }
        }

        return cards;
    }

    public  void assignCardToPlayer() {
        // 初始手牌
        int initialCardNum = this.initialCardNum;

        // 初始玩家人数
        int playerCount = this.playerCount;

        if(initialCards.size() < initialCardNum * playerCount
                || initialCardNum <= 0
                || players == null
                || players.length < playerCount) {
            throw new IllegalArgumentException("初始化错误！");
        }


        for (int j = 0; j < playerCount; j++) {
            List<Integer> cards = players[j].getCards();
            if(cards != null) {
                cards.clear();
                continue;
            }
            // TODO 具体使用 数组还是链表，有待考虑
            cards = new ArrayList<>();
            players[j].setCards(cards);
            players[j].setGameState(GameState.Game_Process);
        }

        for (int i = 0; i < initialCardNum; i++) {
            for (int j = 0; j < playerCount; j++) {
                List<Integer> cards = players[j].getCards();
                // 从手牌池中取出一张手牌
                cards.add(initialCards.get(markIndex++));
            }
        }

        // 对每个玩家的手牌进行排序
        for (int i = 0; i < playerCount; i++) {
            Collections.sort(players[i].getCards());
        }

    }

    /********************* 初始化 *************************/

    /**
     *
     * 洗牌算法
     * 随机生成一个 1-n 的随机数，从最后一个数组开始
     * 不断交换 card[random]，card[i]
     *
     * @param cards 初始手牌
     */
    protected List<Integer> shuffle(List<Integer> cards) {
        // TODO : 可以考虑使用 ThreadRandom 优化
        Random rand = new Random();

        List<Integer> copyCards = new ArrayList<>(cards);

        // 洗牌 1 - 107
        for (int i = copyCards.size() - 1; i >= 1 ; i--) {

            int randInd = rand.nextInt(i);
            // 交换
            swap(copyCards, i, randInd);
        }
        swap(copyCards, 0, copyCards.size() - 1);
        return Collections.unmodifiableList(copyCards);

    }


    private void swap(List<Integer> copyCards, int i, int randInd) {
        Integer temp = copyCards.get(i);
        copyCards.set(i, copyCards.get(randInd));
        copyCards.set(randInd, temp);
    }


    /****************** 工具方法 **********************/

    /**
     * @param id 玩家 id
     * @return 房间
     */
    public static ServerFourRoom getRoomById(String id) {

        // 获取房间管理器
        RoomManager roomManager = RoomManager.getInstance();
        return (ServerFourRoom) roomManager.get(preRoomInfoPrev(id));
    }

    public static void write(String id, Object message) {

        Room room = getRoomById(id);
        Player p = room.findPlayerById(id);
        ChannelHandlerContext context = (ChannelHandlerContext) p.getContext();

        context.channel().eventLoop().execute(() -> {
            context.writeAndFlush(message);
        });

    }

    /**
     * 获取下一个玩家的 id（正常流程下，即没有事件/事件为过）
     */
    public String nextPlayerId() {

       return players[(prevRoundIndex + 1) % playerCount].id();
    }


    public int skipNextRoundIndex(int nextRoundIndex) {

        if(countHu >= playerCount - 1) {
            throw new IllegalArgumentException("游戏已经结束了");
        }

        if(nextRoundIndex < 0) {
            return -1;
        }

        if(players[nextRoundIndex % playerCount].getGameState() == GameState.Game_Process) {
            return nextRoundIndex;
        }else {
            // 递归，跳过状态结束/异常的玩家
            return skipNextRoundIndex(nextRoundIndex + 1);
        }
    }

    /*************** 事件操作 *********************/

    /**
     *
     * @param id 调用者 id
     *
     */
    public void announce(String id) {
        eventHandler.announce(id, false);
    }


    public void announceNext() {
        eventHandler.announceNext();
    }



    /************** 状态切换 ************************/
    public StateMachine findPlayerState(String userId) {
        return StateMachines.get(preUserMachinekey(userId));
    }


    /**
     *
     * 获取当前回合玩家的状态机
     *
     * @return 状态机
     */
    public StateMachine nextPlayerState() {
        int next = getCurRoundIndex() % getPlayerCount();
        Player nextPlayer = players[next];
        return findPlayerState(nextPlayer.id());
    }
}
