package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Entities.LobbyChatKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends CassandraRepository<Chat, LobbyChatKey> {
    @Query("SELECT * FROM chats WHERE \"lobbyId\" = ?0")
    List<Chat> findByLobbyUUID(UUID lobbyId);
}
