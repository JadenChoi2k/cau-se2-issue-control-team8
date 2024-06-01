package org.cause2.team8.repository.user;

import lombok.NonNull;
import org.cause2.team8.domain.user.Developer;
import org.cause2.team8.domain.user.ProjectLeader;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.domain.user.UserRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(@NonNull String LoginId);

    boolean existsByLoginId(@NonNull String loginId);

    @Query("select d from Developer d where d.id = :id")
    Optional<Developer> findDeveloperById(Long id);

    @Query("select pl from ProjectLeader pl where pl.id = :id")
    Optional<ProjectLeader> findProjectLeaderById(Long id);

    List<User> findByNameContaining(String name, Pageable pageable);
}
