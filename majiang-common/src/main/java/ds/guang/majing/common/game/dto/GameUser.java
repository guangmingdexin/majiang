package ds.guang.majing.common.game.dto;

import java.io.Serializable;
import java.util.Objects;

public class GameUser implements Serializable {

    private String userId;

    private String username;

    private int vip;

    private int score;

    public GameUser() {
    }

    public String getUserId() {
        return userId;
    }

    public GameUser setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public GameUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getVip() {
        return vip;
    }

    public GameUser setVip(int vip) {
        this.vip = vip;
        return this;
    }

    public int getScore() {
        return score;
    }

    public GameUser setScore(int score) {
        this.score = score;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameUser gameUser = (GameUser) o;
        return vip == gameUser.vip &&
                score == gameUser.score &&
                Objects.equals(userId, gameUser.userId) &&
                Objects.equals(username, gameUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, vip, score);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"userId\":").append(userId)
                .append(", \"username\":").append(username)
                .append(", \"vip\":").append(vip)
                .append(", \"score\":").append(score)
                .append('}');
        return sb.toString();
    }
}
