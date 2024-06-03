package org.cause2.team8.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cause2.team8.domain.project.Issue;
import org.cause2.team8.domain.project.IssuePriority;
import org.cause2.team8.domain.project.IssueStatus;
import org.cause2.team8.domain.project.Project;
import org.cause2.team8.domain.user.User;
import org.cause2.team8.dto.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public abstract class IssueDTO {

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "issuePageItem")
    public static class PageItem {
        private final Long id;
        private final IssuePriority priority;
        private final IssueStatus status;
        private final String title;
        private final LocalDateTime reportedAt;
        private final LocalDateTime dueDate;
        private final LocalDateTime fixedAt;
        private final LocalDateTime resolvedAt;
        private final LocalDateTime closedAt;
        private final UserDTO.Info reporter;
        private final UserDTO.Info assignee;

        public static PageItem from(org.cause2.team8.domain.project.Issue issue) {
            return new PageItem(
                issue.getIssueId(),
                issue.getPriority(),
                issue.getStatus(),
                issue.getTitle(),
                issue.getReportedAt(),
                issue.getDueDate(),
                issue.getFixedAt(),
                issue.getResolvedAt(),
                issue.getClosedAt(),
                UserDTO.Info.from(issue.getReporter()),
                issue.getAssignee() == null ? null : UserDTO.Info.from(issue.getAssignee())
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "IssueDetail")
    public static class Detail {
        private final Long id;
        private final IssuePriority priority;
        private final IssueStatus status;
        private final String title;
        private final String description;
        private final LocalDateTime reportedAt;
        private final LocalDateTime dueDate;
        private final LocalDateTime fixedAt;
        private final LocalDateTime resolvedAt;
        private final LocalDateTime closedAt;
        private final UserDTO.Info reporter;
        private final UserDTO.Info assignee;
        private final UserDTO.Info fixer;
        private final ProjectDTO.Info project;
        private final List<IssueCommentDTO.Main> comments;

        public static Detail from(Issue issue) {
            return new Detail(
                issue.getIssueId(),
                issue.getPriority(),
                issue.getStatus(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getReportedAt(),
                issue.getDueDate(),
                issue.getFixedAt(),
                issue.getResolvedAt(),
                issue.getClosedAt(),
                UserDTO.Info.from(issue.getReporter()),
                issue.getAssignee() == null ? null : UserDTO.Info.from(issue.getAssignee()),
                issue.getFixer() == null ? null : UserDTO.Info.from(issue.getFixer()),
                ProjectDTO.Info.from(issue.getProject()),
                issue.getComments().stream().map(IssueCommentDTO.Main::from).toList()
            );
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "IssueCreateRequest")
    public static class CreateRequest {
        @NotEmpty
        private final String title;
        @NotEmpty
        private final String description;
        @NotNull
        private final IssuePriority priority;
        @NotNull
        private final LocalDateTime dueDate;

        public Issue create(Project project, User reporter) {
            return reporter.reportIssue(project, priority, title, description, dueDate);
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Schema(name = "IssueEditRequest")
    public static class EditRequest {
        private final String title;
        private final String description;
        private final IssuePriority priority;

        public Issue edit(Issue issue) {
            issue.edit(title, description, priority);
            return issue;
        }
    }
}
