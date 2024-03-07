package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.Chat;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends CassandraRepository<Chat, UUID> {
}
