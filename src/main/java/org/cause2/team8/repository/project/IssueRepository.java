package org.cause2.team8.repository.project;

import org.cause2.team8.domain.project.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query("select i from Issue i" +
        " where i.project.projectId = :projectId" +
        " order by i.reportedAt desc")
    List<Issue> paginateIssuesByProjectId(String projectId, int offset, int limit);

    @Query("select i from Issue i" +
        " join fetch i.project" +
        " join fetch i.comments" +
        " where i.issueId = :issueId")
    Optional<Issue> findByIssueIdOpt(Long issueId);
}
