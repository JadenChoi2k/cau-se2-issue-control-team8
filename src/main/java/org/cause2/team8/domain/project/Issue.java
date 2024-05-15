package org.cause2.team8.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cause2.team8.domain.user.Developer;
import org.cause2.team8.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue {
    @Id
    @GeneratedValue
    private Long issueId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private IssuePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private IssueStatus status = IssueStatus.NEW;

    @Column(nullable = false, length = 300)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    private LocalDateTime reportedAt;
    private LocalDateTime fixedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Developer assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixer_id")
    private Developer fixer;

    public Issue(IssuePriority priority, String title, String description, Project project, User reporter) {
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.project = project;
        this.reporter = reporter;
    }
}
