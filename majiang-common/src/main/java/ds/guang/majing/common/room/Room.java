package ds.guang.majing.common.room;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ds.guang.majing.common.player.Player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author asus
 */
public abstract class Room implements Serializable {

    protected String id;

    protected Player[] players;

    protected int markIndex;

    protected int curRoundIndex;

    protected int playerCount;

    private List<Integer> cards;

    public Player[] getPlayers() {
        return players;
    }

    public Room() {
    }

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
            if(userId.equals(player.getId())) {
                return player;
            }
        }

        return null;
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
                .append(", \"cards\":").append(cards)
                .append('}');
        return sb.toString();
    }
}
