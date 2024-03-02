package lt.viko.eif.pi21e.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @PrimaryKey
    private UUID lobbyId;
    private Date timestamp;
    private String sentBy;
    private String sentTo;
    private String message;
}