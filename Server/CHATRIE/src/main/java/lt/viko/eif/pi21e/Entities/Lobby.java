package lt.viko.eif.pi21e.Entities;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("lobbies")
public class Lobby {
    @PrimaryKey
    private UUID lobbyId;
    private String user1Nickname;
    private String user2Nickname;

    public Lobby() {
    }

    public Lobby(UUID lobbyId, String user1Nickname, String user2Nickname) {
        this.lobbyId = lobbyId;
        this.user1Nickname = user1Nickname;
        this.user2Nickname = user2Nickname;
    }

    public UUID getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(UUID lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getUser1Nickname() {
        return user1Nickname;
    }

    public void setUser1Nickname(String user1Nickname) {
        this.user1Nickname = user1Nickname;
    }

    public String getUser2Nickname() {
        return user2Nickname;
    }

    public void setUser2Nickname(String user2Nickname) {
        this.user2Nickname = user2Nickname;
    }
}