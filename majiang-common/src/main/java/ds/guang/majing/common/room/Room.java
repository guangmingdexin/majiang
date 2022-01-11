package ds.guang.majing.common.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ds.guang.majing.common.player.Player;

import java.io.Serializable;
import java.util.*;

/**
 * @author asus
 */
public abstract class Room implements Serializable {

    protected String id;

    protected Player[] players;

    protected int markIndex;

    protected int curRoundIndex;

    protected int playerCount;

    protected int initialCardNum;

    @JsonIgnore
    private transient List<Integer> initialCards;

    public Player[] getPlayers() {
        return players;
    }

    public Room() {}

    public Room setPlayers(Player[] players) {
        this.players = players;
        return this;
    }

    public int getMarkIndex() {
        return markIndex;
    }

    public Room setMarkIndex(int markIndex) {
        this.markIndex = markIndex;
        return this;
    }

    public int getCurRoundIndex() {
        return curRoundIndex;
    }

    public Room setCurRoundIndex(int curRoundIndex) {
        this.curRoundIndex = curRoundIndex;
        return this;
    }

    public String getId() {
        return id;
    }

    public Room setId(String id) {
        this.id = id;
        return this;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Room setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        return this;
    }

    public List<Integer> getInitialCards() {
        return initialCards;
    }

    public Room setInitialCards(List<Integer> initialCards) {
        this.initialCards = initialCards;
        return this;
    }

    public int getInitialCardNum() {
        return initialCardNum;
    }

    public Room setInitialCardNum(int initialCardNum) {
        this.initialCardNum = initialCardNum;
        return this;
    }

    public Player findPlayerById(String userId) {

        Objects.requireNonNull(userId, "userId must be not empty!");

        for (Player player : players) {
            if(userId.equals(player.id())) {
                return player;
            }
        }

        return null;
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
        }

        for (int i = 0; i < initialCardNum; i++) {
            for (int j = 0; j < playerCount; j++) {
                List<Integer> cards = players[j].getCards();
                cards.add(initialCards.get(i));
                System.out.print("cards: " + cards);
            }
        }

        for (int i = 0; i < playerCount; i++) {
            Collections.sort(players[i].getCards());
        }

    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"id\":").append(id)
                .append(", \"players\":").append(Arrays.toString(players))
                .append(", \"markIndex\":").append(markIndex)
                .append(", \"curRoundIndex\":").append(curRoundIndex)
                .append(", \"playerCount\":").append(playerCount)
                .append(", \"cards\":").append(initialCards)
                .append('}');
        return sb.toString();
    }
}
