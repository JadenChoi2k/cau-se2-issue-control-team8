package org.cause2.team8.domain;

import org.cause2.team8.domain.project.*;
import org.cause2.team8.domain.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {
    Tester testTester() {
        return new Tester("tester1", "테스터", "testPassword123!");
    }

    Developer testDev() {
        return new Developer("devdev1", "개발자", "testPassword123!");
    }

    Developer testDev2() {
        return new Developer("devdev2", "개발자2", "testPassword123!");
    }

    ProjectLeader testPL() {
        return new ProjectLeader("plpl123", "프로젝트장", "testPassword123!");
    }

    Admin testAdmin() {
        return new Admin("admin", "관리자", "testPassword123!");
    }

    static <T extends User> T getUser(Project project, Class<T> clazz) {
        for (User user : project.getParticipants()) {
            if (clazz.isInstance(user)) {
                return clazz.cast(user);
            }
        }
        throw new RuntimeException("User not found");
    }

    Project testProject() {
        Admin admin = testAdmin();
        Project project = admin.createProject("project1", "프로젝트1", "예시 프로젝트입니다.", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        admin.participate(project, testTester());
        admin.participate(project, testDev());
        admin.participate(project, testPL());
        return project;
    }

    Issue testIssue() {
        // given
        Project project = testProject();
        User reporter = project.getParticipants().get(new Random().nextInt(project.getParticipants().size()));
        IssuePriority priority = IssuePriority.MAJOR;
        String title = "이슈1";
        String description = "이슈1 설명";
        return reporter.reportIssue(project, priority, title, description, LocalDateTime.of(2024, 6, 10, 10, 0));
    }

    @Test
    @DisplayName("프로젝트 생성")
    void createProject() {
        // given
        Admin admin = testAdmin();
        String projectId = "project1";
        String title = "프로젝트1";
        String description = "프로젝트1 설명";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 12, 31);
        // when
        Project project = admin.createProject(projectId, title, description, startDate, dueDate);
        // then
        assertEquals(projectId, project.getProjectId());
        assertEquals(title, project.getTitle());
        assertEquals(description, project.getDescription());
        assertEquals(project.getParticipants().get(0), admin);
    }

    @Test
    @DisplayName("프로젝트 수정")
    void editProject() {
        // given
        Project project = testProject();
        String newTitle = "프로젝트1 수정";
        String newDescription = "프로젝트1 설명 수정";
        // when
        project.edit(newTitle, newDescription);
        // then
        assertEquals(newTitle, project.getTitle());
        assertEquals(newDescription, project.getDescription());
    }

    @Test
    @DisplayName("프로젝트 참여")
    void participateToProject() {
        // given
        Admin admin = testAdmin();
        Project project = admin.createProject("project1", "프로젝트1", "예시 프로젝트입니다.", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        Tester tester = testTester();
        Developer dev = testDev();
        ProjectLeader pl = testPL();
        // when
        admin.participate(project, tester);
        admin.participate(project, dev);
        admin.participate(project, pl);
        // then
        assertIterableEquals(project.getParticipants(), List.of(admin, tester, dev, pl));
    }

    @Test
    @DisplayName("이슈 생성")
    void createIssue() {
        // given
        Project project = testProject();
        User reporter = project.getParticipants().get(new Random().nextInt(project.getParticipants().size()));
        IssuePriority priority = IssuePriority.MAJOR;
        String title = "이슈1";
        String description = "이슈1 설명";
        // when
        Issue issue = reporter.reportIssue(project, priority, title, description, LocalDateTime.of(2024, 6, 10, 10, 0));
        // then
        assertEquals(issue.getReporter(), reporter);
        assertEquals(issue.getPriority(), priority);
        assertEquals(issue.getTitle(), title);
        assertEquals(issue.getDescription(), description);
        assertEquals(issue.getStatus(), IssueStatus.NEW);
        assertEquals(issue.getProject(), project);
        assertEquals(issue.getComments().size(), 0);
        assertNotNull(issue.getReportedAt());
    }

    @Test
    @DisplayName("이슈 정보 수정")
    void editIssue() {
        // given
        Issue issue = testIssue();
        String newTitle = "이슈1 수정";
        String newDescription = "이슈1 설명 수정";
        IssuePriority newPriority = IssuePriority.CRITICAL;
        // when
        issue.edit(newTitle, newDescription, newPriority);
        // then
        assertEquals(newTitle, issue.getTitle());
        assertEquals(newDescription, issue.getDescription());
        assertEquals(newPriority, issue.getPriority());
        assertNotNull(issue.getLastEditedAt());
    }

    @Test
    @DisplayName("이슈 할당")
    void assignToIssue() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        // when
        pl.assign(issue, dev);
        // then
        assertEquals(issue.getAssignee(), dev);
        assertEquals(issue.getStatus(), IssueStatus.ASSIGNED);
    }

    @Test
    @DisplayName("이슈 fix 처리")
    void fixIssue() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        // when
        dev.fix(issue);
        // then
        assertEquals(issue.getFixer(), dev);
        assertEquals(issue.getStatus(), IssueStatus.FIXED);
        assertNotNull(issue.getFixedAt());
    }

    @Test
    @DisplayName("이슈 resolve 처리")
    void resolveIssue() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        dev.fix(issue);
        // when
        issue.resolve();
        // then
        assertEquals(issue.getStatus(), IssueStatus.RESOLVED);
        assertNotNull(issue.getResolvedAt());
    }

    @Test
    @DisplayName("이슈 close 처리")
    void closeIssue() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        dev.fix(issue);
        issue.resolve();
        // when
        issue.close();
        // then
        assertEquals(issue.getStatus(), IssueStatus.CLOSED);
        assertNotNull(issue.getClosedAt());
    }

    @Test
    @DisplayName("이슈 reopen 처리")
    void reopenIssue() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        dev.fix(issue);
        issue.resolve();
        // when
        issue.reopen();
        // then
        assertEquals(issue.getStatus(), IssueStatus.REOPENED);
    }

    @Test
    @DisplayName("이슈 reopen 후 fix 처리")
    void fixAfterReopen() {
        // given
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        dev.fix(issue);
        issue.resolve();
        issue.reopen();
        // when
        dev.fix(issue);
        // then
        assertEquals(issue.getStatus(), IssueStatus.FIXED);
    }

    @Test
    @DisplayName("이슈 fix 오류 - assignee 아닌 사람이 fix")
    void fixError() {
        // given
        Issue issue = testIssue();
        Admin admin = getUser(issue.getProject(), Admin.class);
        Developer dev2 = testDev2();
        admin.participate(issue.getProject(), dev2);
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        pl.assign(issue, dev);
        // when & then
        assertThrows(RuntimeException.class, () -> dev2.fix(issue));
    }

    @Test
    @DisplayName("이슈 flow 중 상태 오류")
    void stateError() {
        // given & when & then
        Issue issue = testIssue();
        ProjectLeader pl = getUser(issue.getProject(), ProjectLeader.class);
        Developer dev = getUser(issue.getProject(), Developer.class);
        // NEW
        assertThrows(RuntimeException.class, issue::resolve);
        assertThrows(RuntimeException.class, issue::close);
        assertThrows(RuntimeException.class, issue::reopen);
        assertThrows(RuntimeException.class, () -> dev.fix(issue));

        pl.assign(issue, dev);
        // ASSIGNED
        assertThrows(RuntimeException.class, () -> pl.assign(issue, dev));
        assertThrows(RuntimeException.class, issue::resolve);
        assertThrows(RuntimeException.class, issue::close);
        assertThrows(RuntimeException.class, issue::reopen);

        dev.fix(issue);
        // FIXED
        assertThrows(RuntimeException.class, issue::close);
        assertThrows(RuntimeException.class, issue::reopen);
        assertThrows(RuntimeException.class, () -> dev.fix(issue));

        issue.resolve();
        // RESOLVED
        assertThrows(RuntimeException.class, issue::resolve);
        assertThrows(RuntimeException.class, () -> pl.assign(issue, dev));
        assertThrows(RuntimeException.class, () -> dev.fix(issue));

        issue.close();
        // CLOSED
        assertThrows(RuntimeException.class, issue::close);
        assertThrows(RuntimeException.class, issue::reopen);
        assertThrows(RuntimeException.class, issue::resolve);
        assertThrows(RuntimeException.class, () -> pl.assign(issue, dev));
        assertThrows(RuntimeException.class, () -> dev.fix(issue));
    }

    @Test
    @DisplayName("이슈 코멘트 생성")
    void commentToIssue() {
        // given
        Issue issue = testIssue();
        User commenter = issue.getProject().getParticipants().get(new Random().nextInt(issue.getProject().getParticipants().size()));
        String content = "코멘트1";
        // when
        IssueComment comment = commenter.commentToIssue(issue, content);
        // then
        assertEquals(issue.getComments().get(0), comment);
        assertEquals(comment.getUser(), commenter);
        assertEquals(comment.getContent(), content);
        assertEquals(comment.getIssue(), issue);
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getEditedAt());
    }

    @Test
    @DisplayName("이슈 코멘트 수정")
    void editComment() {
        // given
        Issue issue = testIssue();
        User commenter = issue.getProject().getParticipants().get(new Random().nextInt(issue.getProject().getParticipants().size()));
        String content = "코멘트1";
        IssueComment comment = commenter.commentToIssue(issue, content);
        String newContent = "코멘트1 수정";
        // when
        comment.edit(newContent);
        // then
        assertEquals(comment.getContent(), newContent);
        assertNotEquals(comment.getCreatedAt(), comment.getEditedAt());
    }
}
