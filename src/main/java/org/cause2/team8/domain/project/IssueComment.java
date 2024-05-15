package org.cause2.team8.domain.project;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cause2.team8.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueComment {
    @Id
    @GeneratedValue
    private Long commentId;

    // 페이징 단계에서 노출되므로 EAGER 로딩
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Column(nullable = false, length = 1000)
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public IssueComment(User user, Issue issue, String content) {
        this.user = user;
        this.issue = issue;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.editedAt = LocalDateTime.now();
    }

    public void edit(String content) {
        this.content = content;
        this.editedAt = LocalDateTime.now();
    }
}
