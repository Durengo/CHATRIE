package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.Lobby;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends CassandraRepository<Lobby, String> {
}
