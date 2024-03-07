package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.Lobby;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository extends CassandraRepository<Lobby, String> {
    @Query("SELECT * FROM lobby WHERE user1_nickname = ?0 OR user2_nickname = ?0")
    List<Lobby> findLobbiesByUserNickname(String username);

    @Query("SELECT * FROM lobby WHERE user1_nickname = ?0 AND user2_nickname = ?1 ALLOW FILTERING")
    Lobby findLobbyByUser1NicknameAndUser2Nickname(String user1Nickname, String user2Nickname);
}
