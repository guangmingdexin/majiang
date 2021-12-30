package ds.guang.majing.common.room;

import ds.guang.majing.common.player.Player;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author asus
 */
public abstract class Room implements Serializable {

    protected String id;

    protected List<Player> players;

    protected int markIndex;

    protected int curRoundIndex;

    protected int playerCount;

    private List<Integer> cards;

    public List<Player> getPlayers() {
        return players;
    }

    public Room setPlayers(List<Player> players) {
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

    public List<Integer> getCards() {
        return cards;
    }

    public Room setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public Player findPlayerById(String userId) {

        Objects.requireNonNull(userId, "userId must be not empty!");

        for (Player player : players) {
            if(userId.equals(player.getGameUserInfo().getUserId())) {
                return player;
            }
        }

        return null;
    }
}
