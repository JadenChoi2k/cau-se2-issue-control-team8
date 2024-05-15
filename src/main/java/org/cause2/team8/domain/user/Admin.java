package org.cause2.team8.domain.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cause2.team8.domain.project.Project;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User {
    @Override
    public UserRole getUserRole() {
        return UserRole.ADMIN;
    }

    public Admin(String loginId, String name, String password) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        super.validate();
    }

    public Project createProject(String projectId, String title, String description) {
        Project project = new Project(projectId, title, description);
        participate(project, this);
        return project;
    }

    public void participate(Project project, User user) {
        project.getParticipants().add(user);
    }
}
