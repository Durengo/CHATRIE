package lt.viko.eif.pi21e.Repositories;

import lt.viko.eif.pi21e.Entities.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {
    Optional<User> findByUsername(String username);
}
