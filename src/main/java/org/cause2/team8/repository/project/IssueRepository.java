package org.cause2.team8.repository.project;

import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findAllByProject(Project project, PageRequest pageRequest);

    @Query("select i from Issue i" +
        " join fetch i.project" +
        " left join fetch i.comments" +
        " where i.issueId = :issueId")
    Optional<Issue> findByIssueIdOpt(Long issueId);
}
