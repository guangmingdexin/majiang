package ds.guang.majing.common.room;

import ds.guang.majing.common.player.Player;

import java.io.Serializable;
import java.util.List;

/**
 * @author asus
 */
public abstract class Room implements Serializable {

    protected List<Player> players;

    protected int markIndex;

    protected int curRoundIndex;

    protected int playerCount;

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
}
