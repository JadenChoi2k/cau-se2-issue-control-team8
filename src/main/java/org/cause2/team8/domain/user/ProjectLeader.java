package org.cause2.team8.domain.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cause2.team8.domain.project.Issue;

@Entity
@DiscriminatorValue("PL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectLeader extends User {
    @Override
    public UserRole getUserRole() {
        return UserRole.PL;
    }

    public ProjectLeader(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        super.validate();
    }

    public void assign(Issue issue, Developer developer) {
        issue.setAssignee(developer);
    }
}
