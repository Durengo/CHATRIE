package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.Lobby;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LobbyRepository extends CassandraRepository<Lobby, UUID> {
    @Query("SELECT * FROM lobbies WHERE user1nickname = ?0 ALLOW FILTERING")
    List<Lobby> findByUser1Nickname(String username);

    @Query("SELECT * FROM lobbies WHERE user2nickname = ?0 ALLOW FILTERING")
    List<Lobby> findByUser2Nickname(String username);
}
