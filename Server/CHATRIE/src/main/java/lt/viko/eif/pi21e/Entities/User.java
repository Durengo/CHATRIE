package lt.viko.eif.pi21e.Entities;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("users")
public class User {
    @PrimaryKey
    private String nickname;
    private String password;

    public User(String nickname, String password) {
        // TODO: Implement logic to not have duplicate nicknames in the database.
        this.nickname = nickname;
        this.password = password;
    }

    protected User() {

    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    // Well, it looks like we NEED a setter otherwise things absolutely break...
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void changePassword(String newPassword, String oldPassowrd) throws Exception {
        if (oldPassowrd.equals(this.password))
            this.password = newPassword;
        else
            throw new Exception("Old Password is invalid.");
    }
}
