package lt.viko.eif.pi21e.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@PrimaryKeyClass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyChatKey implements Serializable {
    @PrimaryKeyColumn(name = "lobbyId", type = PrimaryKeyType.PARTITIONED)
    private UUID lobbyId;

    @PrimaryKeyColumn(name = "timestamp", ordinal = 0, ordering = Ordering.DESCENDING)
    private Date timestamp;
}
