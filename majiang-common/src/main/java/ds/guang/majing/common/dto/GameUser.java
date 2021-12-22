package ds.guang.majing.common.dto;

import java.util.StringJoiner;

public class GameUser {

    private String userId;

    private String username;

    private int vip;

    private int score;

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
        return new StringJoiner(", ", GameUser.class.getSimpleName() + "={", "}")
                .add("\"userId\":\"" + userId + "\"")
                .add("\"username\":\"" + username + "\"")
                .add("\"vip\":" + vip)
                .add("\"score\":" + score)
                .toString();
    }
}
