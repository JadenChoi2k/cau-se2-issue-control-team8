package org.cause2.team8.common.utils.data;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cause2.team8.domain.user.*;
import org.cause2.team8.repository.user.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile("test")
@Component
@RequiredArgsConstructor
public class TestDataHandler {
    private final UserRepository userRepository;

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
        log.info("test data inserted");
    }
}
