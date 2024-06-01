package org.cause2.team8.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cause2.team8.common.utils.exceptions.ErrorCode;
import org.cause2.team8.common.utils.exceptions.SimpleError;
import org.cause2.team8.domain.user.Developer;
import org.cause2.team8.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 페이징 단계에서 노출되므로 EAGER 로딩
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    private LocalDateTime reportedAt;
    private LocalDateTime lastEditedAt;
    private LocalDateTime fixedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Developer assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixer_id")
    private Developer fixer;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueComment> comments = new ArrayList<>();

    public Issue(IssuePriority priority, String title, String description, Project project, User reporter) {
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.project = project;
        this.reporter = reporter;
        this.reportedAt = LocalDateTime.now();
        this. lastEditedAt = LocalDateTime.now();
    }

    public void edit(String title, String description, IssuePriority priority) {
        if (title != null && !title.isBlank()) this.title = title;
        if (description != null && !description.isBlank()) this.description = description;
        if (priority != null) this.priority = priority;
        this.lastEditedAt = LocalDateTime.now();
    }

    public void setAssignee(Developer assignee) {
        if (status != IssueStatus.NEW && status != IssueStatus.REOPENED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        this.assignee = assignee;
        this.status = IssueStatus.ASSIGNED;
        this.lastEditedAt = LocalDateTime.now();
    }

    public void setFixer(Developer fixer) {
        if (status != IssueStatus.ASSIGNED && status != IssueStatus.REOPENED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        if (!assignee.equals(fixer)) {
            throw new SimpleError(ErrorCode.FORBIDDEN);
        }
        this.fixer = fixer;
        this.status = IssueStatus.FIXED;
        this.fixedAt = LocalDateTime.now();
        this.lastEditedAt = LocalDateTime.now();
    }

    public void resolve() {
        if (status != IssueStatus.FIXED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        this.status = IssueStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.lastEditedAt = LocalDateTime.now();
    }

    public void close() {
        if (status != IssueStatus.RESOLVED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        this.status = IssueStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
        this.lastEditedAt = LocalDateTime.now();
    }

    public void reopen() {
        if (status != IssueStatus.RESOLVED) {
            throw new SimpleError(ErrorCode.CONFLICT);
        }
        this.status = IssueStatus.REOPENED;
        this.lastEditedAt = LocalDateTime.now();
    }

    public double calculateSimilarity(Issue other) {
        return
            (
                calculateSimilarity(
                    this.title,
                    other.title
                ) +
                calculateSimilarity(
                    this.description,
                    other.description
                )
            )
            / 2;
    }

    private static double calculateSimilarity(String source, String target) {
        int distance = calculateLevenshteinDistance(source, target);
        int maxLength = Math.max(source.length(), target.length());

        if (maxLength == 0) {
            return 1.0; // 둘 다 빈 문자열인 경우, 유사도는 1.0
        }

        return 1.0 - (double) distance / maxLength;
    }

    private static int calculateLevenshteinDistance(String source, String target) {
        int[][] dp = new int[source.length() + 1][target.length() + 1];

        for (int i = 0; i <= source.length(); i++) {
            for (int j = 0; j <= target.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j; // target 문자열을 source 문자열로 변환하기 위한 삽입 횟수
                } else if (j == 0) {
                    dp[i][j] = i; // source 문자열을 target 문자열로 변환하기 위한 삭제 횟수
                } else {
                    int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }

        return dp[source.length()][target.length()];
    }
}
