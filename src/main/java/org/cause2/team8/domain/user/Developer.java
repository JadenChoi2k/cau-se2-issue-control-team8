package org.cause2.team8.domain.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cause2.team8.domain.project.Issue;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DEV")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Developer extends User {

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assignedIssues = new ArrayList<>();

    @Override
    public UserRole getUserRole() {
        return UserRole.DEV;
    }

    public Developer(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        super.validate();
    }

    public void fix(Issue issue) {
        issue.setFixer(this);
    }
}
