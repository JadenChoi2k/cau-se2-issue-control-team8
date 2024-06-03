package org.cause2.team8.repository.project;

import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssueStatus;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// {"action":"invite","inviter":124124,"invitee":[123123123,324234,23423423]}

public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findAllByProject(Project project, PageRequest pageRequest);

    Page<Issue> findAllByProjectAndTitleContainingIgnoreCase(Project project, String title, PageRequest pageRequest);

    Page<Issue> findAllByProjectAndTitleContainingIgnoreCaseAndStatus(Project project, String title, IssueStatus issueStatus, PageRequest pageRequest);

    Page<Issue> findAllByProjectAndStatus(Project project, IssueStatus issueStatus, PageRequest pageRequest);

    @Query("select i from Issue i" +
        " join fetch i.project" +
        " left join fetch i.comments" +
        " where i.issueId = :issueId")
    Optional<Issue> findByIssueIdOpt(Long issueId);

    // 정렬 순서
    // 1. 해당 프로젝트에 참여 중인 개발자 중 처리 중인 이슈가 적은 순
    // 2. 이전의 처리한 이슈와 현재 이슈의 유사도가 높은 순
    // 3. 이전의 이슈 처리 수가 높은 순
    // 이 중 2번은 쿼리에서 해결할 수 없으므로, 1, 3번만 쿼리로 처리
    // 따라서 1, 3번만 쿼리로 처리하여 최대 3명의 개발자를 추천 후, 2번은 코드를 통해 해결
//    @Query("select d" +
//        " from Developer d" +
//        " where d.participatedIn = :project" +
//        " group by d" +
//        " order by count(i) asc," +  // 1. 처리 중인 이슈가 적은 순
//        " (select count(iss)" +
//        " from Issue iss" +
//        " where iss.assignee = d and iss.status = 'CLOSED') desc")  // 3. 이전의 이슈 처리 수가 높은 순)
//    List<User> recommendAssignees(Project project);
}
