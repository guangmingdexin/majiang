package ds.guang.majing.common.dto;

import java.io.Serializable;

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
