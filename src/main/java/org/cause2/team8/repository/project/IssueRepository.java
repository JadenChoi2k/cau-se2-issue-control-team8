package org.cause2.team8.repository.project;

import org.cause2.team8.domain.project.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
