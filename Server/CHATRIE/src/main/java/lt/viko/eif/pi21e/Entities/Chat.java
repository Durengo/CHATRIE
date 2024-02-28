package lt.viko.eif.pi21e.Entities;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("chats")
public class Chat {
    @PrimaryKey
    private UUID lobbyId;
    private Date timestamp;
    private String sentBy;
    private String sentTo;
    private String message;

    public Chat() {
    }

    public Chat(UUID lobbyId, Date timestamp, String sentBy, String sentTo, String message) {
        this.lobbyId = lobbyId;
        this.timestamp = timestamp;
        this.sentBy = sentBy;
        this.sentTo = sentTo;
        this.message = message;
    }

    public UUID getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(UUID lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}