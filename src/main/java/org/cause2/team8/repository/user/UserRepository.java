package org.cause2.team8.repository.user;

import lombok.NonNull;
import org.cause2.team8.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(@NonNull String userId);

    boolean existsByUserId(@NonNull String userId);
}
