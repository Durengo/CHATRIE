package lt.viko.eif.pi21e.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("lobbies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lobby {
    @PrimaryKey
    private UUID lobbyId;
    private String user1Nickname;
    private String user2Nickname;
}
