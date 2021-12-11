package ds.guang.majing.common.dto;

/**
 * @author guangyong.deng
 * @date 2021-12-08 16:10
 */
public class User  {

    private String username;

    private String password;

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"username\":").append(username)
                .append(", \"password\":").append(password)
                .append('}');
        return sb.toString();
    }
}
