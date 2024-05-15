package org.cause2.team8.repository.project;

import org.cause2.team8.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    // 1+N query 문제 해결을 위해 fetch join 사용
    @Query("select p from Project p join fetch p.participants where p.projectId = :projectId")
    Optional<Project> findByIdOpt(String projectId);

    @Query("select case when count(p) > 0 then true else false end from Project p join p.participants u where p.projectId = :projectId and u.id = :userId")
    boolean participating(String projectId, Long userId);
}
