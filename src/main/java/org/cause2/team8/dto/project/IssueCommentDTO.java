package org.cause2.team8.dto.project;

import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssueComment;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.dto.user.UserDTO;

import java.time.LocalDateTime;

public abstract class IssueCommentDTO {
    @Getter
    @RequiredArgsConstructor
    public static class Main {
        private final Long commentId;
        private final UserDTO.Info user;
        private final String content;
        private final LocalDateTime createdAt;
        private final LocalDateTime editedAt;

        public static Main from(org.cause2.team8.domain.project.IssueComment comment) {
            return new Main(
                comment.getCommentId(),
                UserDTO.Info.from(comment.getUser()),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getEditedAt()
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Request {
        @Max(1000)
        private final String content;

        public IssueComment create(User user, Issue issue) {
            return user.commentToIssue(issue, content);
        }

        public IssueComment edit(IssueComment comment) {
            comment.edit(content);
            return comment;
        }
    }
}
