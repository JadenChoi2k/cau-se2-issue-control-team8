package org.cause2.team8.common.utils.data;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssuePriority;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.domain.user.*;
import org.cause2.team8.repository.project.IssueRepository;
import org.cause2.team8.repository.project.ProjectRepository;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("test")
@Component
@RequiredArgsConstructor
public class TestDataHandler {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final EntityManager entityManager;

    @Transactional
    @PostConstruct
    public void insertTestData() {
        if (userRepository.existsByLoginId("admin")) {
            return;
        }
        log.info("test data insert started");
        Admin admin = new Admin("admin", "어드민", "1q2w3e4r!!");
        ProjectLeader pl1 = new ProjectLeader("plpl1", "PL1", "iamplpl1!!");
        ProjectLeader pl2 = new ProjectLeader("plpl2", "PL2", "iamplpl2!!");
        List<Developer> devs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            devs.add(new Developer("dev" + i, "DEV" + i, "iamdev" + i + "!"));
        }
        List<Tester> testers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            testers.add(new Tester("tester" + i, "TESTER" + i, "iamtester" + i + "!"));
        }
        userRepository.save(admin);
        userRepository.save(pl1);
        userRepository.save(pl2);
        userRepository.saveAll(devs);
        userRepository.saveAll(testers);

        Project project1 = admin.createProject("project1", "프로젝트1", "예시 프로젝트입니다.", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        admin.participate(project1, pl1);
        admin.participate(project1, pl2);
        for (Developer dev : devs) {
            admin.participate(project1, dev);
        }
        for (Tester tester : testers) {
            admin.participate(project1, tester);
        }
        projectRepository.save(project1);

        Issue issue = testers.get(0).reportIssue(project1, IssuePriority.MAJOR, "테스트 이슈1", "테스트 이슈1입니다.");
        pl1.assign(issue, devs.get(0));
        pl1.commentToIssue(issue, "테스트 이슈1에 대한 코멘트입니다.");
        issueRepository.save(issue);

        log.info("test data inserted");
    }
}
