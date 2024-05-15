package org.cause2.team8.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cause2.team8.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    private String projectId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private List<Issue> issueList = new ArrayList<>();

    public Project(String projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    public void edit(String title, String description) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
    }

    public Issue createIssue(IssuePriority priority, String title, String description, User reporter) {
        Issue issue = new Issue(priority, title, description, this, reporter);
        issueList.add(issue);
        return issue;
    }
}
